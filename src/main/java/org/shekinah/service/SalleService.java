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
import org.shekinah.domain.PavillonEntity;
import org.shekinah.domain.SalleEntity;

@Named
public class SalleService extends BaseService<SalleEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public SalleService(){
        super(SalleEntity.class);
    }
    
    @Transactional
    public List<SalleEntity> findAllSalleEntities() {
        
        return entityManager.createQuery("SELECT o FROM Salle o ", SalleEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Salle o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(SalleEntity salle) {

        /* This is called before a Salle is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.cutAllSalleHospitalisationsAssignments(salle);
        
    }

    // Remove all assignments from all hospitalisation a salle. Called before delete a salle.
    @Transactional
    private void cutAllSalleHospitalisationsAssignments(SalleEntity salle) {
        entityManager
                .createQuery("UPDATE Hospitalisation c SET c.salle = NULL WHERE c.salle = :p")
                .setParameter("p", salle).executeUpdate();
    }
    
    @Transactional
    public List<SalleEntity> findAvailableSalles(PavillonEntity pavillon) {
        return entityManager.createQuery("SELECT o FROM Salle o WHERE o.pavillon IS NULL", SalleEntity.class).getResultList();
    }

    @Transactional
    public List<SalleEntity> findSallesByPavillon(PavillonEntity pavillon) {
        return entityManager.createQuery("SELECT o FROM Salle o WHERE o.pavillon = :pavillon", SalleEntity.class).setParameter("pavillon", pavillon).getResultList();
    }

    @Transactional
    public SalleEntity lazilyLoadImageToSalle(SalleEntity salle) {
        PersistenceUnitUtil u = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        if (!u.isLoaded(salle, "image") && salle.getId() != null) {
            salle = find(salle.getId());
            salle.getImage().getId();
        }
        return salle;
    }
    
    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<SalleEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM Salle o");
        
        // Can be optimized: We need this join only when pavillon filter is set
        query.append(" LEFT OUTER JOIN o.pavillon pavillon");
        
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
                
                case "prix":
                    query.append(nextConnective).append(" o.prix = :prix");
                    queryParameters.put("prix", new BigDecimal(filters.get(filterProperty).toString()));
                    break;

                case "designation":
                    query.append(nextConnective).append(" o.designation LIKE :designation");
                    queryParameters.put("designation", "%" + filters.get(filterProperty) + "%");
                    break;

                case "pavillon":
                    query.append(nextConnective).append(" o.pavillon = :pavillon");
                    queryParameters.put("pavillon", filters.get(filterProperty));
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
        
        TypedQuery<SalleEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
