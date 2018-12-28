package org.shekinah.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.primefaces.model.SortOrder;
import org.shekinah.domain.FamilleEntity;

@Named
public class FamilleService extends BaseService<FamilleEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public FamilleService(){
        super(FamilleEntity.class);
    }
    
    @Transactional
    public List<FamilleEntity> findAllFamilleEntities() {
        
        return entityManager.createQuery("SELECT o FROM Famille o ", FamilleEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Famille o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(FamilleEntity famille) {

        /* This is called before a Famille is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.cutAllFamilleMedicamentsAssignments(famille);
        
    }

    // Remove all assignments from all medicament a famille. Called before delete a famille.
    @Transactional
    private void cutAllFamilleMedicamentsAssignments(FamilleEntity famille) {
        entityManager
                .createQuery("UPDATE Medicament c SET c.famille = NULL WHERE c.famille = :p")
                .setParameter("p", famille).executeUpdate();
    }
    
    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<FamilleEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM Famille o");
        
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

                case "description":
                    query.append(nextConnective).append(" o.description LIKE :description");
                    queryParameters.put("description", "%" + filters.get(filterProperty) + "%");
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
        
        TypedQuery<FamilleEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
