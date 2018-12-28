package org.shekinah.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.primefaces.model.SortOrder;
import org.shekinah.domain.CreneauEntity;
import org.shekinah.domain.PatientEntity;
import org.shekinah.domain.RendezvousEntity;

@Named
public class RendezvousService extends BaseService<RendezvousEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public RendezvousService(){
        super(RendezvousEntity.class);
    }
    
    @Transactional
    public List<RendezvousEntity> findAllRendezvousEntities() {
        
        return entityManager.createQuery("SELECT o FROM Rendezvous o ", RendezvousEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Rendezvous o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(RendezvousEntity rendezvous) {

        /* This is called before a Rendezvous is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

    @Transactional
    public List<RendezvousEntity> findAvailableRendezvouss(PatientEntity patient) {
        return entityManager.createQuery("SELECT o FROM Rendezvous o WHERE o.patient IS NULL", RendezvousEntity.class).getResultList();
    }

    @Transactional
    public List<RendezvousEntity> findRendezvoussByPatient(PatientEntity patient) {
        return entityManager.createQuery("SELECT o FROM Rendezvous o WHERE o.patient = :patient", RendezvousEntity.class).setParameter("patient", patient).getResultList();
    }

    @Transactional
    public List<RendezvousEntity> findAvailableRendezvouss(CreneauEntity creneau) {
        return entityManager.createQuery("SELECT o FROM Rendezvous o WHERE o.creneau IS NULL", RendezvousEntity.class).getResultList();
    }

    @Transactional
    public List<RendezvousEntity> findRendezvoussByCreneau(CreneauEntity creneau) {
        return entityManager.createQuery("SELECT o FROM Rendezvous o WHERE o.creneau = :creneau", RendezvousEntity.class).setParameter("creneau", creneau).getResultList();
    }

    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<RendezvousEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM Rendezvous o");
        
        // Can be optimized: We need this join only when patient filter is set
        query.append(" LEFT OUTER JOIN o.patient patient");
        
        // Can be optimized: We need this join only when creneau filter is set
        query.append(" LEFT OUTER JOIN o.creneau creneau");
        
        String nextConnective = " WHERE";
        
        Map<String, Object> queryParameters = new HashMap<>();
        
        if (filters != null && !filters.isEmpty()) {
            
            nextConnective += " ( ";
            
            for(String filterProperty : filters.keySet()) {
                
                if (filters.get(filterProperty) == null) {
                    continue;
                }
                
                switch (filterProperty) {
                
                case "patient":
                    query.append(nextConnective).append(" o.patient = :patient");
                    queryParameters.put("patient", filters.get(filterProperty));
                    break;
                
                case "creneau":
                    query.append(nextConnective).append(" o.creneau = :creneau");
                    queryParameters.put("creneau", filters.get(filterProperty));
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
        
        TypedQuery<RendezvousEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
