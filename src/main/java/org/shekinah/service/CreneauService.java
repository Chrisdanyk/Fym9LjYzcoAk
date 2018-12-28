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
import org.shekinah.domain.security.UserEntity;

@Named
public class CreneauService extends BaseService<CreneauEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public CreneauService(){
        super(CreneauEntity.class);
    }
    
    @Transactional
    public List<CreneauEntity> findAllCreneauEntities() {
        
        return entityManager.createQuery("SELECT o FROM Creneau o ", CreneauEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Creneau o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(CreneauEntity creneau) {

        /* This is called before a Creneau is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.cutAllCreneauRendezvoussAssignments(creneau);
        
    }

    // Remove all assignments from all rendezvous a creneau. Called before delete a creneau.
    @Transactional
    private void cutAllCreneauRendezvoussAssignments(CreneauEntity creneau) {
        entityManager
                .createQuery("UPDATE Rendezvous c SET c.creneau = NULL WHERE c.creneau = :p")
                .setParameter("p", creneau).executeUpdate();
    }
    
    @Transactional
    public List<CreneauEntity> findAvailableCreneaus(UserEntity user) {
        return entityManager.createQuery("SELECT o FROM Creneau o WHERE o.medecin IS NULL", CreneauEntity.class).getResultList();
    }

    @Transactional
    public List<CreneauEntity> findCreneausByMedecin(UserEntity user) {
        return entityManager.createQuery("SELECT o FROM Creneau o WHERE o.medecin = :user", CreneauEntity.class).setParameter("user", user).getResultList();
    }

    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<CreneauEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM Creneau o");
        
        // Can be optimized: We need this join only when medecin filter is set
        query.append(" LEFT OUTER JOIN o.medecin medecin");
        
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

                case "medecin":
                    query.append(nextConnective).append(" o.medecin = :medecin");
                    queryParameters.put("medecin", filters.get(filterProperty));
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
        
        TypedQuery<CreneauEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
