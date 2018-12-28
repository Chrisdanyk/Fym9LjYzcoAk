package org.shekinah.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.primefaces.model.SortOrder;
import org.shekinah.domain.FournisseurEntity;

@Named
public class FournisseurService extends BaseService<FournisseurEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public FournisseurService(){
        super(FournisseurEntity.class);
    }
    
    @Transactional
    public List<FournisseurEntity> findAllFournisseurEntities() {
        
        return entityManager.createQuery("SELECT o FROM Fournisseur o ", FournisseurEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Fournisseur o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(FournisseurEntity fournisseur) {

        /* This is called before a Fournisseur is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.cutAllFournisseursMedicamentsAssignments(fournisseur);
        
    }

    // Remove all assignments from all medicament a fournisseur. Called before delete a fournisseur.
    @Transactional
    private void cutAllFournisseursMedicamentsAssignments(FournisseurEntity fournisseur) {
        entityManager
                .createQuery("UPDATE Medicament c SET c.fournisseurs = NULL WHERE c.fournisseurs = :p")
                .setParameter("p", fournisseur).executeUpdate();
    }
    
    @Transactional
    public FournisseurEntity lazilyLoadImageToFournisseur(FournisseurEntity fournisseur) {
        PersistenceUnitUtil u = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        if (!u.isLoaded(fournisseur, "image") && fournisseur.getId() != null) {
            fournisseur = find(fournisseur.getId());
            fournisseur.getImage().getId();
        }
        return fournisseur;
    }
    
    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<FournisseurEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM Fournisseur o");
        
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
                
                case "nom":
                    query.append(nextConnective).append(" o.nom LIKE :nom");
                    queryParameters.put("nom", "%" + filters.get(filterProperty) + "%");
                    break;

                case "adresse":
                    query.append(nextConnective).append(" o.adresse LIKE :adresse");
                    queryParameters.put("adresse", "%" + filters.get(filterProperty) + "%");
                    break;

                case "telephone":
                    query.append(nextConnective).append(" o.telephone LIKE :telephone");
                    queryParameters.put("telephone", "%" + filters.get(filterProperty) + "%");
                    break;

                case "mail":
                    query.append(nextConnective).append(" o.mail LIKE :mail");
                    queryParameters.put("mail", "%" + filters.get(filterProperty) + "%");
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
        
        TypedQuery<FournisseurEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
