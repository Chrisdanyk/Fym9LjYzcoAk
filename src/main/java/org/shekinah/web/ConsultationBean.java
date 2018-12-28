package org.shekinah.web;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import org.shekinah.domain.ConsultationEntity;
import org.shekinah.domain.ExamenEntity;
import org.shekinah.domain.OrdonnanceEntity;
import org.shekinah.domain.PatientEntity;
import org.shekinah.domain.security.UserEntity;
import org.shekinah.service.ConsultationService;
import org.shekinah.service.ExamenService;
import org.shekinah.service.OrdonnanceService;
import org.shekinah.service.PatientService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.service.security.UserService;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("consultationBean")
@ViewScoped
public class ConsultationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(ConsultationBean.class.getName());
    
    private GenericLazyDataModel<ConsultationEntity> lazyModel;
    
    private ConsultationEntity consultation;
    
    @Inject
    private ConsultationService consultationService;
    
    @Inject
    private PatientService patientService;
    
    @Inject
    private ExamenService examenService;
    
    @Inject
    private OrdonnanceService ordonnanceService;
    
    @Inject
    private UserService userService;
    
    private List<PatientEntity> allPatientsList;
    
    private List<ExamenEntity> allExamenssList;
    
    private List<UserEntity> allMedecinsList;
    
    private List<UserEntity> allInfirmiersList;
    
    private List<UserEntity> allLaborantinsList;
    
    private List<OrdonnanceEntity> availableOrdonnanceList;
    
    public void prepareNewConsultation() {
        reset();
        this.consultation = new ConsultationEntity();
        // set any default values now, if you need
        // Example: this.consultation.setAnything("test");
    }

    public GenericLazyDataModel<ConsultationEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(consultationService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (consultation.getId() == null && !isPermitted("consultation:create")) {
            return "accessDenied";
        } else if (consultation.getId() != null && !isPermitted(consultation, "consultation:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (consultation.getId() != null) {
                consultation = consultationService.update(consultation);
                message = "message_successfully_updated";
            } else {
                consultation = consultationService.save(consultation);
                message = "message_successfully_created";
            }
        } catch (OptimisticLockException e) {
            logger.log(Level.SEVERE, "Error occured", e);
            message = "message_optimistic_locking_exception";
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        } catch (PersistenceException e) {
            logger.log(Level.SEVERE, "Error occured", e);
            message = "message_save_exception";
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        }
        
        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(consultation, "consultation:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            consultationService.delete(consultation);
            message = "message_successfully_deleted";
            reset();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occured", e);
            message = "message_delete_exception";
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        }
        FacesContext.getCurrentInstance().addMessage(null, MessageFactory.getMessage(message));
        
        return null;
    }
    
    public void onDialogOpen(ConsultationEntity consultation) {
        reset();
        this.consultation = consultation;
    }
    
    public void reset() {
        consultation = null;

        allPatientsList = null;
        
        allExamenssList = null;
        
        allMedecinsList = null;
        
        allInfirmiersList = null;
        
        allLaborantinsList = null;
        
        availableOrdonnanceList = null;
        
    }

    // Get a List of all patient
    public List<PatientEntity> getPatients() {
        if (this.allPatientsList == null) {
            this.allPatientsList = patientService.findAllPatientEntities();
        }
        return this.allPatientsList;
    }
    
    // Update patient of the current consultation
    public void updatePatient(PatientEntity patient) {
        this.consultation.setPatient(patient);
        // Maybe we just created and assigned a new patient. So reset the allPatientList.
        allPatientsList = null;
    }
    
    // Get a List of all examens
    public List<ExamenEntity> getExamenss() {
        if (this.allExamenssList == null) {
            this.allExamenssList = examenService.findAllExamenEntities();
        }
        return this.allExamenssList;
    }
    
    // Update examens of the current consultation
    public void updateExamens(ExamenEntity examen) {
        this.consultation.setExamens(examen);
        // Maybe we just created and assigned a new examen. So reset the allExamensList.
        allExamenssList = null;
    }
    
    // Get a List of all medecin
    public List<UserEntity> getMedecins() {
        if (this.allMedecinsList == null) {
            this.allMedecinsList = userService.findAllUserEntities();
        }
        return this.allMedecinsList;
    }
    
    // Update medecin of the current consultation
    public void updateMedecin(UserEntity user) {
        this.consultation.setMedecin(user);
        // Maybe we just created and assigned a new user. So reset the allMedecinList.
        allMedecinsList = null;
    }
    
    // Get a List of all infirmier
    public List<UserEntity> getInfirmiers() {
        if (this.allInfirmiersList == null) {
            this.allInfirmiersList = userService.findAllUserEntities();
        }
        return this.allInfirmiersList;
    }
    
    // Update infirmier of the current consultation
    public void updateInfirmier(UserEntity user) {
        this.consultation.setInfirmier(user);
        // Maybe we just created and assigned a new user. So reset the allInfirmierList.
        allInfirmiersList = null;
    }
    
    // Get a List of all laborantin
    public List<UserEntity> getLaborantins() {
        if (this.allLaborantinsList == null) {
            this.allLaborantinsList = userService.findAllUserEntities();
        }
        return this.allLaborantinsList;
    }
    
    // Update laborantin of the current consultation
    public void updateLaborantin(UserEntity user) {
        this.consultation.setLaborantin(user);
        // Maybe we just created and assigned a new user. So reset the allLaborantinList.
        allLaborantinsList = null;
    }
    
    // Get a List of all ordonnance
    public List<OrdonnanceEntity> getAvailableOrdonnance() {
        if (this.availableOrdonnanceList == null) {
            this.availableOrdonnanceList = ordonnanceService.findAvailableOrdonnance(this.consultation);
        }
        return this.availableOrdonnanceList;
    }
    
    // Update ordonnance of the current consultation
    public void updateOrdonnance(OrdonnanceEntity ordonnance) {
        this.consultation.setOrdonnance(ordonnance);
        // Maybe we just created and assigned a new ordonnance. So reset the availableOrdonnanceList.
        availableOrdonnanceList = null;
    }
    
    public ConsultationEntity getConsultation() {
        if (this.consultation == null) {
            prepareNewConsultation();
        }
        return this.consultation;
    }
    
    public void setConsultation(ConsultationEntity consultation) {
        this.consultation = consultation;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(ConsultationEntity consultation, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
