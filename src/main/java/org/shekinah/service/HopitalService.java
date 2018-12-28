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
import org.shekinah.domain.HopitalEntity;

@Named
public class HopitalService extends BaseService<HopitalEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public HopitalService(){
        super(HopitalEntity.class);
    }
    
    @Transactional
    public List<HopitalEntity> findAllHopitalEntities() {
        
        return entityManager.createQuery("SELECT o FROM Hopital o ", HopitalEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Hopital o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(HopitalEntity hopital) {

        /* This is called before a Hopital is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.cutAllHopitalUsersAssignments(hopital);
        
        this.cutAllHopitalPatientsAssignments(hopital);
        
    }

    // Remove all assignments from all user a hopital. Called before delete a hopital.
    @Transactional
    private void cutAllHopitalUsersAssignments(HopitalEntity hopital) {
        entityManager
                .createQuery("UPDATE User c SET c.hopital = NULL WHERE c.hopital = :p")
                .setParameter("p", hopital).executeUpdate();
    }
    
    // Remove all assignments from all patient a hopital. Called before delete a hopital.
    @Transactional
    private void cutAllHopitalPatientsAssignments(HopitalEntity hopital) {
        entityManager
                .createQuery("UPDATE Patient c SET c.hopital = NULL WHERE c.hopital = :p")
                .setParameter("p", hopital).executeUpdate();
    }
    
    @Transactional
    public HopitalEntity lazilyLoadImageToHopital(HopitalEntity hopital) {
        PersistenceUnitUtil u = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        if (!u.isLoaded(hopital, "image") && hopital.getId() != null) {
            hopital = find(hopital.getId());
            hopital.getImage().getId();
        }
        return hopital;
    }
    
    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<HopitalEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM Hopital o");
        
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

                case "adresse":
                    query.append(nextConnective).append(" o.adresse LIKE :adresse");
                    queryParameters.put("adresse", "%" + filters.get(filterProperty) + "%");
                    break;

                case "telephone":
                    query.append(nextConnective).append(" o.telephone LIKE :telephone");
                    queryParameters.put("telephone", "%" + filters.get(filterProperty) + "%");
                    break;

                case "email":
                    query.append(nextConnective).append(" o.email LIKE :email");
                    queryParameters.put("email", "%" + filters.get(filterProperty) + "%");
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
        
        TypedQuery<HopitalEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
