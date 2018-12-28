package org.shekinah.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.primefaces.model.SortOrder;
import org.shekinah.domain.MedicamentEntity;
import org.shekinah.domain.VenteEntity;
import org.shekinah.domain.security.UserEntity;
import org.shekinah.service.security.SecurityWrapper;

@Named
public class VenteService extends BaseService<VenteEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public VenteService(){
        super(VenteEntity.class);
    }
    
    @Transactional
    public List<VenteEntity> findAllVenteEntities() {
        
        return entityManager.createQuery("SELECT o FROM Vente o ", VenteEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Vente o", Long.class).getSingleResult();
    }
    
    @Override
    @Transactional
    public VenteEntity save(VenteEntity vente) {
        String username = SecurityWrapper.getUsername();
        
        vente.updateAuditInformation(username);
        
        return super.save(vente);
    }
    
    @Override
    @Transactional
    public VenteEntity update(VenteEntity vente) {
        String username = SecurityWrapper.getUsername();
        vente.updateAuditInformation(username);
        return super.update(vente);
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(VenteEntity vente) {

        /* This is called before a Vente is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

    @Transactional
    public List<VenteEntity> findAvailableVentes(UserEntity user) {
        return entityManager.createQuery("SELECT o FROM Vente o WHERE o.pharmacien IS NULL", VenteEntity.class).getResultList();
    }

    @Transactional
    public List<VenteEntity> findVentesByPharmacien(UserEntity user) {
        return entityManager.createQuery("SELECT o FROM Vente o WHERE o.pharmacien = :user", VenteEntity.class).setParameter("user", user).getResultList();
    }

    @Transactional
    public List<VenteEntity> findAvailableVentes(MedicamentEntity medicament) {
        return entityManager.createQuery("SELECT o FROM Vente o WHERE o.medicament IS NULL", VenteEntity.class).getResultList();
    }

    @Transactional
    public List<VenteEntity> findVentesByMedicament(MedicamentEntity medicament) {
        return entityManager.createQuery("SELECT o FROM Vente o WHERE o.medicament = :medicament", VenteEntity.class).setParameter("medicament", medicament).getResultList();
    }

    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<VenteEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM Vente o");
        
        // Can be optimized: We need this join only when pharmacien filter is set
        query.append(" LEFT OUTER JOIN o.pharmacien pharmacien");
        
        // Can be optimized: We need this join only when medicament filter is set
        query.append(" LEFT OUTER JOIN o.medicament medicament");
        
        String nextConnective = " WHERE";
        
        Map<String, Object> queryParameters = new HashMap<>();
        
        if (filters != null && !filters.isEmpty()) {
            
            nextConnective += " ( ";
            
            for(String filterProperty : filters.keySet()) {
                
                if (filters.get(filterProperty) == null) {
                    continue;
                }
                
                switch (filterProperty) {
                
                case "date":
                    query.append(nextConnective).append(" o.date = :date");
                    queryParameters.put("date", filters.get(filterProperty));
                    break;

                case "quantite":
                    query.append(nextConnective).append(" o.quantite = :quantite");
                    queryParameters.put("quantite", new BigDecimal(filters.get(filterProperty).toString()));
                    break;

                case "client":
                    query.append(nextConnective).append(" o.client LIKE :client");
                    queryParameters.put("client", "%" + filters.get(filterProperty) + "%");
                    break;

                case "pharmacien":
                    query.append(nextConnective).append(" o.pharmacien = :pharmacien");
                    queryParameters.put("pharmacien", filters.get(filterProperty));
                    break;
                
                case "medicament":
                    query.append(nextConnective).append(" o.medicament = :medicament");
                    queryParameters.put("medicament", filters.get(filterProperty));
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
        
        TypedQuery<VenteEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
