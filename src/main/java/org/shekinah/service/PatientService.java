package org.shekinah.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceUnitUtil;
import javax.transaction.Transactional;

import org.shekinah.domain.HopitalEntity;
import org.shekinah.domain.PatientEntity;

@Named
public class PatientService extends BaseService<PatientEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public PatientService(){
        super(PatientEntity.class);
    }
    
    @Inject
    private PatientAttachmentService attachmentService;
    
    @Transactional
    public List<PatientEntity> findAllPatientEntities() {
        
        return entityManager.createQuery("SELECT o FROM Patient o LEFT JOIN FETCH o.image", PatientEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Patient o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(PatientEntity patient) {

        /* This is called before a Patient is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.attachmentService.deleteAttachmentsByPatient(patient);
        
        this.cutAllPatientRendezvoussAssignments(patient);
        
        this.cutAllPatientConsultationsAssignments(patient);
        
        this.cutAllPatientLabosAssignments(patient);
        
        this.cutAllPatientAbonnementsAssignments(patient);
        
        this.cutAllPatientHospitalisationsAssignments(patient);
        
    }

    // Remove all assignments from all rendezvous a patient. Called before delete a patient.
    @Transactional
    private void cutAllPatientRendezvoussAssignments(PatientEntity patient) {
        entityManager
                .createQuery("UPDATE Rendezvous c SET c.patient = NULL WHERE c.patient = :p")
                .setParameter("p", patient).executeUpdate();
    }
    
    // Remove all assignments from all consultation a patient. Called before delete a patient.
    @Transactional
    private void cutAllPatientConsultationsAssignments(PatientEntity patient) {
        entityManager
                .createQuery("UPDATE Consultation c SET c.patient = NULL WHERE c.patient = :p")
                .setParameter("p", patient).executeUpdate();
    }
    
    // Remove all assignments from all labo a patient. Called before delete a patient.
    @Transactional
    private void cutAllPatientLabosAssignments(PatientEntity patient) {
        entityManager
                .createQuery("UPDATE Labo c SET c.patient = NULL WHERE c.patient = :p")
                .setParameter("p", patient).executeUpdate();
    }
    
    // Remove all assignments from all abonnement a patient. Called before delete a patient.
    @Transactional
    private void cutAllPatientAbonnementsAssignments(PatientEntity patient) {
        entityManager
                .createQuery("UPDATE Abonnement c SET c.patient = NULL WHERE c.patient = :p")
                .setParameter("p", patient).executeUpdate();
    }
    
    // Remove all assignments from all hospitalisation a patient. Called before delete a patient.
    @Transactional
    private void cutAllPatientHospitalisationsAssignments(PatientEntity patient) {
        entityManager
                .createQuery("UPDATE Hospitalisation c SET c.patient = NULL WHERE c.patient = :p")
                .setParameter("p", patient).executeUpdate();
    }
    
    @Transactional
    public List<PatientEntity> findAvailablePatients(HopitalEntity hopital) {
        return entityManager.createQuery("SELECT o FROM Patient o WHERE o.hopital IS NULL", PatientEntity.class).getResultList();
    }

    @Transactional
    public List<PatientEntity> findPatientsByHopital(HopitalEntity hopital) {
        return entityManager.createQuery("SELECT o FROM Patient o WHERE o.hopital = :hopital", PatientEntity.class).setParameter("hopital", hopital).getResultList();
    }

    @Transactional
    public PatientEntity lazilyLoadImageToPatient(PatientEntity patient) {
        PersistenceUnitUtil u = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        if (!u.isLoaded(patient, "image") && patient.getId() != null) {
            patient = find(patient.getId());
            patient.getImage().getId();
        }
        return patient;
    }
    
}
