package org.shekinah.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.primefaces.model.SortOrder;
import org.shekinah.domain.HospitalisationEntity;
import org.shekinah.domain.PatientEntity;
import org.shekinah.domain.SalleEntity;

@Named
public class HospitalisationService extends BaseService<HospitalisationEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public HospitalisationService(){
        super(HospitalisationEntity.class);
    }
    
    @Transactional
    public List<HospitalisationEntity> findAllHospitalisationEntities() {
        
        return entityManager.createQuery("SELECT o FROM Hospitalisation o ", HospitalisationEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Hospitalisation o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(HospitalisationEntity hospitalisation) {

        /* This is called before a Hospitalisation is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

    @Transactional
    public List<HospitalisationEntity> findAvailableHospitalisations(SalleEntity salle) {
        return entityManager.createQuery("SELECT o FROM Hospitalisation o WHERE o.salle IS NULL", HospitalisationEntity.class).getResultList();
    }

    @Transactional
    public List<HospitalisationEntity> findHospitalisationsBySalle(SalleEntity salle) {
        return entityManager.createQuery("SELECT o FROM Hospitalisation o WHERE o.salle = :salle", HospitalisationEntity.class).setParameter("salle", salle).getResultList();
    }

    @Transactional
    public List<HospitalisationEntity> findAvailableHospitalisations(PatientEntity patient) {
        return entityManager.createQuery("SELECT o FROM Hospitalisation o WHERE o.patient IS NULL", HospitalisationEntity.class).getResultList();
    }

    @Transactional
    public List<HospitalisationEntity> findHospitalisationsByPatient(PatientEntity patient) {
        return entityManager.createQuery("SELECT o FROM Hospitalisation o WHERE o.patient = :patient", HospitalisationEntity.class).setParameter("patient", patient).getResultList();
    }

    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<HospitalisationEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM Hospitalisation o");
        
        // Can be optimized: We need this join only when salle filter is set
        query.append(" LEFT OUTER JOIN o.salle salle");
        
        // Can be optimized: We need this join only when patient filter is set
        query.append(" LEFT OUTER JOIN o.patient patient");
        
        String nextConnective = " WHERE";
        
        Map<String, Object> queryParameters = new HashMap<>();
        
        if (filters != null && !filters.isEmpty()) {
            
            nextConnective += " ( ";
            
            for(String filterProperty : filters.keySet()) {
                
                if (filters.get(filterProperty) == null) {
                    continue;
                }
                
                switch (filterProperty) {
                
                case "dateEntree":
                    query.append(nextConnective).append(" o.dateEntree = :dateEntree");
                    queryParameters.put("dateEntree", filters.get(filterProperty));
                    break;

                case "dateSortie":
                    query.append(nextConnective).append(" o.dateSortie = :dateSortie");
                    queryParameters.put("dateSortie", filters.get(filterProperty));
                    break;

                case "observation":
                    query.append(nextConnective).append(" o.observation LIKE :observation");
                    queryParameters.put("observation", "%" + filters.get(filterProperty) + "%");
                    break;

                case "salle":
                    query.append(nextConnective).append(" o.salle = :salle");
                    queryParameters.put("salle", filters.get(filterProperty));
                    break;
                
                case "patient":
                    query.append(nextConnective).append(" o.patient = :patient");
                    queryParameters.put("patient", filters.get(filterProperty));
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
        
        TypedQuery<HospitalisationEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
