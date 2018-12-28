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
import org.shekinah.domain.ExamenEntity;

@Named
public class ExamenService extends BaseService<ExamenEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public ExamenService(){
        super(ExamenEntity.class);
    }
    
    @Transactional
    public List<ExamenEntity> findAllExamenEntities() {
        
        return entityManager.createQuery("SELECT o FROM Examen o ", ExamenEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Examen o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(ExamenEntity examen) {

        /* This is called before a Examen is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.cutAllExamensConsultationsAssignments(examen);
        
        this.cutAllExamenLabosAssignments(examen);
        
    }

    // Remove all assignments from all consultation a examen. Called before delete a examen.
    @Transactional
    private void cutAllExamensConsultationsAssignments(ExamenEntity examen) {
        entityManager
                .createQuery("UPDATE Consultation c SET c.examens = NULL WHERE c.examens = :p")
                .setParameter("p", examen).executeUpdate();
    }
    
    // Remove all assignments from all labo a examen. Called before delete a examen.
    @Transactional
    private void cutAllExamenLabosAssignments(ExamenEntity examen) {
        entityManager
                .createQuery("UPDATE Labo c SET c.examen = NULL WHERE c.examen = :p")
                .setParameter("p", examen).executeUpdate();
    }
    
    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<ExamenEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM Examen o");
        
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

                case "prix":
                    query.append(nextConnective).append(" o.prix = :prix");
                    queryParameters.put("prix", new BigDecimal(filters.get(filterProperty).toString()));
                    break;

                case "type":
                    query.append(nextConnective).append(" o.type LIKE :type");
                    queryParameters.put("type", "%" + filters.get(filterProperty) + "%");
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
        
        TypedQuery<ExamenEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
