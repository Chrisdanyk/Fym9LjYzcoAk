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
import org.shekinah.domain.ConsultationEntity;
import org.shekinah.domain.ExamenEntity;
import org.shekinah.domain.PatientEntity;
import org.shekinah.domain.security.UserEntity;

@Named
public class ConsultationService extends BaseService<ConsultationEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public ConsultationService(){
        super(ConsultationEntity.class);
    }
    
    @Transactional
    public List<ConsultationEntity> findAllConsultationEntities() {
        
        return entityManager.createQuery("SELECT o FROM Consultation o ", ConsultationEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Consultation o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(ConsultationEntity consultation) {

        /* This is called before a Consultation is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

    @Transactional
    public List<ConsultationEntity> findAvailableConsultations(PatientEntity patient) {
        return entityManager.createQuery("SELECT o FROM Consultation o WHERE o.patient IS NULL", ConsultationEntity.class).getResultList();
    }

    @Transactional
    public List<ConsultationEntity> findConsultationsByPatient(PatientEntity patient) {
        return entityManager.createQuery("SELECT o FROM Consultation o WHERE o.patient = :patient", ConsultationEntity.class).setParameter("patient", patient).getResultList();
    }

    @Transactional
    public List<ConsultationEntity> findAvailableConsultations(ExamenEntity examen) {
        return entityManager.createQuery("SELECT o FROM Consultation o WHERE o.examens IS NULL", ConsultationEntity.class).getResultList();
    }

    @Transactional
    public List<ConsultationEntity> findConsultationsByExamens(ExamenEntity examen) {
        return entityManager.createQuery("SELECT o FROM Consultation o WHERE o.examens = :examen", ConsultationEntity.class).setParameter("examen", examen).getResultList();
    }

    @Transactional
    public List<ConsultationEntity> findAvailableConsultations(UserEntity user) {
        return entityManager.createQuery("SELECT o FROM Consultation o WHERE o.medecin IS NULL", ConsultationEntity.class).getResultList();
    }

    @Transactional
    public List<ConsultationEntity> findConsultationsByMedecin(UserEntity user) {
        return entityManager.createQuery("SELECT o FROM Consultation o WHERE o.medecin = :user", ConsultationEntity.class).setParameter("user", user).getResultList();
    }

    @Transactional
    public List<ConsultationEntity> findAvailableConsultation2s(UserEntity user) {
        return entityManager.createQuery("SELECT o FROM Consultation o WHERE o.infirmier IS NULL", ConsultationEntity.class).getResultList();
    }

    @Transactional
    public List<ConsultationEntity> findConsultation2sByInfirmier(UserEntity user) {
        return entityManager.createQuery("SELECT o FROM Consultation o WHERE o.infirmier = :user", ConsultationEntity.class).setParameter("user", user).getResultList();
    }

    @Transactional
    public List<ConsultationEntity> findAvailableConsultation3s(UserEntity user) {
        return entityManager.createQuery("SELECT o FROM Consultation o WHERE o.laborantin IS NULL", ConsultationEntity.class).getResultList();
    }

    @Transactional
    public List<ConsultationEntity> findConsultation3sByLaborantin(UserEntity user) {
        return entityManager.createQuery("SELECT o FROM Consultation o WHERE o.laborantin = :user", ConsultationEntity.class).setParameter("user", user).getResultList();
    }

    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<ConsultationEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM Consultation o");
        
        // Can be optimized: We need this join only when patient filter is set
        query.append(" LEFT OUTER JOIN o.patient patient");
        
        // Can be optimized: We need this join only when examens filter is set
        query.append(" LEFT OUTER JOIN o.examens examens");
        
        // Can be optimized: We need this join only when medecin filter is set
        query.append(" LEFT OUTER JOIN o.medecin medecin");
        
        // Can be optimized: We need this join only when infirmier filter is set
        query.append(" LEFT OUTER JOIN o.infirmier infirmier");
        
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
                
                case "diagnostique":
                    query.append(nextConnective).append(" o.diagnostique LIKE :diagnostique");
                    queryParameters.put("diagnostique", "%" + filters.get(filterProperty) + "%");
                    break;

                case "date":
                    query.append(nextConnective).append(" o.date = :date");
                    queryParameters.put("date", filters.get(filterProperty));
                    break;

                case "tensionArterielle":
                    query.append(nextConnective).append(" o.tensionArterielle LIKE :tensionArterielle");
                    queryParameters.put("tensionArterielle", "%" + filters.get(filterProperty) + "%");
                    break;

                case "temperature":
                    query.append(nextConnective).append(" o.temperature = :temperature");
                    queryParameters.put("temperature", new BigDecimal(filters.get(filterProperty).toString()));
                    break;

                case "poids":
                    query.append(nextConnective).append(" o.poids = :poids");
                    queryParameters.put("poids", new BigDecimal(filters.get(filterProperty).toString()));
                    break;

                case "plainte":
                    query.append(nextConnective).append(" o.plainte LIKE :plainte");
                    queryParameters.put("plainte", "%" + filters.get(filterProperty) + "%");
                    break;

                case "prescription":
                    query.append(nextConnective).append(" o.prescription LIKE :prescription");
                    queryParameters.put("prescription", "%" + filters.get(filterProperty) + "%");
                    break;

                case "resultatsExamen":
                    query.append(nextConnective).append(" o.resultatsExamen LIKE :resultatsExamen");
                    queryParameters.put("resultatsExamen", "%" + filters.get(filterProperty) + "%");
                    break;

                case "pouls":
                    query.append(nextConnective).append(" o.pouls = :pouls");
                    queryParameters.put("pouls", new Integer(filters.get(filterProperty).toString()));
                    break;

                case "frequenceRespiratoire":
                    query.append(nextConnective).append(" o.frequenceRespiratoire = :frequenceRespiratoire");
                    queryParameters.put("frequenceRespiratoire", new Integer(filters.get(filterProperty).toString()));
                    break;

                case "patient":
                    query.append(nextConnective).append(" o.patient = :patient");
                    queryParameters.put("patient", filters.get(filterProperty));
                    break;
                
                case "examens":
                    query.append(nextConnective).append(" o.examens = :examens");
                    queryParameters.put("examens", filters.get(filterProperty));
                    break;
                
                case "medecin":
                    query.append(nextConnective).append(" o.medecin = :medecin");
                    queryParameters.put("medecin", filters.get(filterProperty));
                    break;
                
                case "infirmier":
                    query.append(nextConnective).append(" o.infirmier = :infirmier");
                    queryParameters.put("infirmier", filters.get(filterProperty));
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
        
        TypedQuery<ConsultationEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
