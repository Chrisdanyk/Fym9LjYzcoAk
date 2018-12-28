package org.shekinah.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.primefaces.model.SortOrder;
import org.shekinah.domain.FamilleEntity;
import org.shekinah.domain.FournisseurEntity;
import org.shekinah.domain.MedicamentEntity;

@Named
public class MedicamentService extends BaseService<MedicamentEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public MedicamentService(){
        super(MedicamentEntity.class);
    }
    
    @Transactional
    public List<MedicamentEntity> findAllMedicamentEntities() {
        
        return entityManager.createQuery("SELECT o FROM Medicament o ", MedicamentEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Medicament o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(MedicamentEntity medicament) {

        /* This is called before a Medicament is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.cutAllMedicamentVentesAssignments(medicament);
        
    }

    // Remove all assignments from all vente a medicament. Called before delete a medicament.
    @Transactional
    private void cutAllMedicamentVentesAssignments(MedicamentEntity medicament) {
        entityManager
                .createQuery("UPDATE Vente c SET c.medicament = NULL WHERE c.medicament = :p")
                .setParameter("p", medicament).executeUpdate();
    }
    
    @Transactional
    public List<MedicamentEntity> findAvailableMedicaments(FournisseurEntity fournisseur) {
        return entityManager.createQuery("SELECT o FROM Medicament o WHERE o.fournisseurs IS NULL", MedicamentEntity.class).getResultList();
    }

    @Transactional
    public List<MedicamentEntity> findMedicamentsByFournisseurs(FournisseurEntity fournisseur) {
        return entityManager.createQuery("SELECT o FROM Medicament o WHERE o.fournisseurs = :fournisseur", MedicamentEntity.class).setParameter("fournisseur", fournisseur).getResultList();
    }

    @Transactional
    public List<MedicamentEntity> findAvailableMedicaments(FamilleEntity famille) {
        return entityManager.createQuery("SELECT o FROM Medicament o WHERE o.famille IS NULL", MedicamentEntity.class).getResultList();
    }

    @Transactional
    public List<MedicamentEntity> findMedicamentsByFamille(FamilleEntity famille) {
        return entityManager.createQuery("SELECT o FROM Medicament o WHERE o.famille = :famille", MedicamentEntity.class).setParameter("famille", famille).getResultList();
    }

    @Transactional
    public MedicamentEntity lazilyLoadImageToMedicament(MedicamentEntity medicament) {
        PersistenceUnitUtil u = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        if (!u.isLoaded(medicament, "image") && medicament.getId() != null) {
            medicament = find(medicament.getId());
            medicament.getImage().getId();
        }
        return medicament;
    }
    
    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<MedicamentEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM Medicament o");
        
        // Can be optimized: We need this join only when fournisseurs filter is set
        query.append(" LEFT OUTER JOIN o.fournisseurs fournisseurs");
        
        // Can be optimized: We need this join only when famille filter is set
        query.append(" LEFT OUTER JOIN o.famille famille");
        
        query.append(" LEFT JOIN FETCH o.image");
        
        String nextConnective = " WHERE";
        
        Map<String, Object> queryParameters = new HashMap<>();
        
        if (filters != null && !filters.isEmpty()) {
            
            nextConnective += " ( ";
            
            for(String filterProperty : filters.keySet()) {
                
                if (filters.get(filterProperty) == null) {
                    continue;
                }
                
                switch (filterProperty) {
                
                case "designation":
                    query.append(nextConnective).append(" o.designation LIKE :designation");
                    queryParameters.put("designation", "%" + filters.get(filterProperty) + "%");
                    break;

                case "dateFabrication":
                    query.append(nextConnective).append(" o.dateFabrication = :dateFabrication");
                    queryParameters.put("dateFabrication", filters.get(filterProperty));
                    break;

                case "dateExpiration":
                    query.append(nextConnective).append(" o.dateExpiration = :dateExpiration");
                    queryParameters.put("dateExpiration", filters.get(filterProperty));
                    break;

                case "prix":
                    query.append(nextConnective).append(" o.prix = :prix");
                    queryParameters.put("prix", new BigDecimal(filters.get(filterProperty).toString()));
                    break;

                case "stock":
                    query.append(nextConnective).append(" o.stock = :stock");
                    queryParameters.put("stock", new Integer(filters.get(filterProperty).toString()));
                    break;

                case "fournisseurs":
                    query.append(nextConnective).append(" o.fournisseurs = :fournisseurs");
                    queryParameters.put("fournisseurs", filters.get(filterProperty));
                    break;
                
                case "famille":
                    query.append(nextConnective).append(" o.famille = :famille");
                    queryParameters.put("famille", filters.get(filterProperty));
                    break;
                
                }
                
                nextConnective = " AND";
            }
            
            query.append(" ) ");
            nextConnective = " AND";
        }
        
        if (sortField != null && !sortField.isEmpty()) {
            query.append(" ORDER BY o.").append(sortField);
            query.append(SortOrder.DESCENDING.equals(sortOrder) ? " DESC" : " ASC");
        }
        
        TypedQuery<MedicamentEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
