package org.shekinah.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.primefaces.model.SortOrder;
import org.shekinah.domain.AbonnementEntity;
import org.shekinah.domain.SocieteAbonnementEntity;
import org.shekinah.service.AbonnementService;

@Named
public class SocieteAbonnementService extends BaseService<SocieteAbonnementEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public SocieteAbonnementService(){
        super(SocieteAbonnementEntity.class);
    }
    
    @Transactional
    public List<SocieteAbonnementEntity> findAllSocieteAbonnementEntities() {
        
        return entityManager.createQuery("SELECT o FROM SocieteAbonnement o ", SocieteAbonnementEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM SocieteAbonnement o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(SocieteAbonnementEntity societeAbonnement) {

        /* This is called before a SocieteAbonnement is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.cutAllSocieteAbonnementsAssignments(societeAbonnement);
        
    }

    // Remove all assignments from all abonnement a societeAbonnement. Called before delete a societeAbonnement.
    @Transactional
    private void cutAllSocieteAbonnementsAssignments(SocieteAbonnementEntity societeAbonnement) {
        entityManager
                .createQuery("UPDATE Abonnement c SET c.societe = NULL WHERE c.societe = :p")
                .setParameter("p", societeAbonnement).executeUpdate();
    }
    
    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<SocieteAbonnementEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM SocieteAbonnement o");
        
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

                case "telephone":
                    query.append(nextConnective).append(" o.telephone LIKE :telephone");
                    queryParameters.put("telephone", "%" + filters.get(filterProperty) + "%");
                    break;

                case "email":
                    query.append(nextConnective).append(" o.email LIKE :email");
                    queryParameters.put("email", "%" + filters.get(filterProperty) + "%");
                    break;

                case "adresse":
                    query.append(nextConnective).append(" o.adresse LIKE :adresse");
                    queryParameters.put("adresse", "%" + filters.get(filterProperty) + "%");
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
        
        TypedQuery<SocieteAbonnementEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
