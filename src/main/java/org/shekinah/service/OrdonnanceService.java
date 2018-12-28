package org.shekinah.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.primefaces.model.SortOrder;
import org.shekinah.domain.ConsultationEntity;
import org.shekinah.domain.OrdonnanceEntity;

@Named
public class OrdonnanceService extends BaseService<OrdonnanceEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public OrdonnanceService(){
        super(OrdonnanceEntity.class);
    }
    
    @Transactional
    public List<OrdonnanceEntity> findAllOrdonnanceEntities() {
        
        return entityManager.createQuery("SELECT o FROM Ordonnance o ", OrdonnanceEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Ordonnance o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(OrdonnanceEntity ordonnance) {

        /* This is called before a Ordonnance is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.cutAllOrdonnanceConsultationsAssignments(ordonnance);
        
    }

    // Remove all assignments from all consultation a ordonnance. Called before delete a ordonnance.
    @Transactional
    private void cutAllOrdonnanceConsultationsAssignments(OrdonnanceEntity ordonnance) {
        entityManager
                .createQuery("UPDATE Consultation c SET c.ordonnance = NULL WHERE c.ordonnance = :p")
                .setParameter("p", ordonnance).executeUpdate();
    }
    
    // Find all consultation which are not yet assigned to a ordonnance
    @Transactional
    public List<OrdonnanceEntity> findAvailableOrdonnance(ConsultationEntity consultation) {
        Long id = -1L;
        if (consultation != null && consultation.getOrdonnance() != null && consultation.getOrdonnance().getId() != null) {
            id = consultation.getOrdonnance().getId();
        }
        return entityManager.createQuery(
                "SELECT o FROM Ordonnance o where o.id NOT IN (SELECT p.ordonnance.id FROM Consultation p where p.ordonnance.id != :id)", OrdonnanceEntity.class)
                .setParameter("id", id).getResultList();    
    }

    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<OrdonnanceEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM Ordonnance o");
        
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

                case "details":
                    query.append(nextConnective).append(" o.details LIKE :details");
                    queryParameters.put("details", "%" + filters.get(filterProperty) + "%");
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
        
        TypedQuery<OrdonnanceEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
