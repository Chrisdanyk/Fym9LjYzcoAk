package org.shekinah.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.primefaces.model.SortOrder;
import org.shekinah.domain.ExamenEntity;
import org.shekinah.domain.LaboEntity;
import org.shekinah.domain.PatientEntity;
import org.shekinah.domain.security.UserEntity;

@Named
public class LaboService extends BaseService<LaboEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public LaboService(){
        super(LaboEntity.class);
    }
    
    @Transactional
    public List<LaboEntity> findAllLaboEntities() {
        
        return entityManager.createQuery("SELECT o FROM Labo o ", LaboEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Labo o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(LaboEntity labo) {

        /* This is called before a Labo is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

    @Transactional
    public List<LaboEntity> findAvailableLabos(ExamenEntity examen) {
        return entityManager.createQuery("SELECT o FROM Labo o WHERE o.examen IS NULL", LaboEntity.class).getResultList();
    }

    @Transactional
    public List<LaboEntity> findLabosByExamen(ExamenEntity examen) {
        return entityManager.createQuery("SELECT o FROM Labo o WHERE o.examen = :examen", LaboEntity.class).setParameter("examen", examen).getResultList();
    }

    @Transactional
    public List<LaboEntity> findAvailableLabos(PatientEntity patient) {
        return entityManager.createQuery("SELECT o FROM Labo o WHERE o.patient IS NULL", LaboEntity.class).getResultList();
    }

    @Transactional
    public List<LaboEntity> findLabosByPatient(PatientEntity patient) {
        return entityManager.createQuery("SELECT o FROM Labo o WHERE o.patient = :patient", LaboEntity.class).setParameter("patient", patient).getResultList();
    }

    @Transactional
    public List<LaboEntity> findAvailableLabos(UserEntity user) {
        return entityManager.createQuery("SELECT o FROM Labo o WHERE o.laborantin IS NULL", LaboEntity.class).getResultList();
    }

    @Transactional
    public List<LaboEntity> findLabosByLaborantin(UserEntity user) {
        return entityManager.createQuery("SELECT o FROM Labo o WHERE o.laborantin = :user", LaboEntity.class).setParameter("user", user).getResultList();
    }

    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<LaboEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM Labo o");
        
        // Can be optimized: We need this join only when examen filter is set
        query.append(" LEFT OUTER JOIN o.examen examen");
        
        // Can be optimized: We need this join only when patient filter is set
        query.append(" LEFT OUTER JOIN o.patient patient");
        
        // Can be optimized: We need this join only when laborantin filter is set
        query.append(" LEFT OUTER JOIN o.laborantin laborantin");
        
        String nextConnective = " WHERE";
        
        Map<String, Object> queryParameters = new HashMap<>();
        
        if (filters != null && !filters.isEmpty()) {
            
            nextConnective += " ( ";
            
            for(String filterProperty : filters.keySet()) {
                
                if (filters.get(filterProperty) == null) {
                    continue;
                }
                
                switch (filterProperty) {
                
                case "resultat":
                    query.append(nextConnective).append(" o.resultat LIKE :resultat");
                    queryParameters.put("resultat", "%" + filters.get(filterProperty) + "%");
                    break;

                case "date":
                    query.append(nextConnective).append(" o.date = :date");
                    queryParameters.put("date", filters.get(filterProperty));
                    break;

                case "examen":
                    query.append(nextConnective).append(" o.examen = :examen");
                    queryParameters.put("examen", filters.get(filterProperty));
                    break;
                
                case "patient":
                    query.append(nextConnective).append(" o.patient = :patient");
                    queryParameters.put("patient", filters.get(filterProperty));
                    break;
                
                case "laborantin":
                    query.append(nextConnective).append(" o.laborantin = :laborantin");
                    queryParameters.put("laborantin", filters.get(filterProperty));
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
        
        TypedQuery<LaboEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
