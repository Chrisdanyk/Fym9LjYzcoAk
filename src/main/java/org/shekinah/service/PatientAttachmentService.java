package org.shekinah.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

import org.shekinah.domain.PatientAttachment;
import org.shekinah.domain.PatientEntity;

@Named
public class PatientAttachmentService extends BaseService<PatientAttachment> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public PatientAttachmentService(){
        super(PatientAttachment.class);
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM PatientAttachment o", Long.class).getSingleResult();
    }

    @Transactional
    public void deleteAttachmentsByPatient(PatientEntity patient) {
        entityManager
                .createQuery("DELETE FROM PatientAttachment c WHERE c.patient = :p")
                .setParameter("p", patient).executeUpdate();
    }
    
    @Transactional
    public List<PatientAttachment> getAttachmentsList(PatientEntity patient) {
        if (patient == null || patient.getId() == null) {
            return new ArrayList<>();
        }
        // The byte streams are not loaded from database with following line. This would cost too much.
        return entityManager.createQuery("SELECT NEW org.shekinah.domain.PatientAttachment(o.id, o.fileName) FROM PatientAttachment o WHERE o.patient.id = :id", PatientAttachment.class).setParameter("id", patient.getId()).getResultList();
    }
}
