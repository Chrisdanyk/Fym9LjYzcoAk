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
import org.shekinah.domain.PatientEntity;
import org.shekinah.domain.SocieteAbonnementEntity;

@Named
public class AbonnementService extends BaseService<AbonnementEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public AbonnementService(){
        super(AbonnementEntity.class);
    }
    
    @Transactional
    public List<AbonnementEntity> findAllAbonnementEntities() {
        
        return entityManager.createQuery("SELECT o FROM Abonnement o ", AbonnementEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Abonnement o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(AbonnementEntity abonnement) {

        /* This is called before a Abonnement is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

    @Transactional
    public List<AbonnementEntity> findAvailableAbonnements(PatientEntity patient) {
        return entityManager.createQuery("SELECT o FROM Abonnement o WHERE o.patient IS NULL", AbonnementEntity.class).getResultList();
    }

    @Transactional
    public List<AbonnementEntity> findAbonnementsByPatient(PatientEntity patient) {
        return entityManager.createQuery("SELECT o FROM Abonnement o WHERE o.patient = :patient", AbonnementEntity.class).setParameter("patient", patient).getResultList();
    }

    @Transactional
    public List<AbonnementEntity> findAvailableAbonnements(SocieteAbonnementEntity societeAbonnement) {
        return entityManager.createQuery("SELECT o FROM Abonnement o WHERE o.societe IS NULL", AbonnementEntity.class).getResultList();
    }

    @Transactional
    public List<AbonnementEntity> findAbonnementsBySociete(SocieteAbonnementEntity societeAbonnement) {
        return entityManager.createQuery("SELECT o FROM Abonnement o WHERE o.societe = :societeAbonnement", AbonnementEntity.class).setParameter("societeAbonnement", societeAbonnement).getResultList();
    }

    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<AbonnementEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM Abonnement o");
        
        // Can be optimized: We need this join only when patient filter is set
        query.append(" LEFT OUTER JOIN o.patient patient");
        
        // Can be optimized: We need this join only when societe filter is set
        query.append(" LEFT OUTER JOIN o.societe societe");
        
        String nextConnective = " WHERE";
        
        Map<String, Object> queryParameters = new HashMap<>();
        
        if (filters != null && !filters.isEmpty()) {
            
            nextConnective += " ( ";
            
            for(String filterProperty : filters.keySet()) {
                
                if (filters.get(filterProperty) == null) {
                    continue;
                }
                
                switch (filterProperty) {
                
                case "debut":
                    query.append(nextConnective).append(" o.debut = :debut");
                    queryParameters.put("debut", filters.get(filterProperty));
                    break;

                case "fin":
                    query.append(nextConnective).append(" o.fin = :fin");
                    queryParameters.put("fin", filters.get(filterProperty));
                    break;

                case "patient":
                    query.append(nextConnective).append(" o.patient = :patient");
                    queryParameters.put("patient", filters.get(filterProperty));
                    break;
                
                case "societe":
                    query.append(nextConnective).append(" o.societe = :societe");
                    queryParameters.put("societe", filters.get(filterProperty));
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
        
        TypedQuery<AbonnementEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
