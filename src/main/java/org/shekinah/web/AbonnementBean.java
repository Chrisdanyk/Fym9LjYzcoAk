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

import org.shekinah.domain.AbonnementEntity;
import org.shekinah.domain.PatientEntity;
import org.shekinah.domain.SocieteAbonnementEntity;
import org.shekinah.service.AbonnementService;
import org.shekinah.service.PatientService;
import org.shekinah.service.SocieteAbonnementService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("abonnementBean")
@ViewScoped
public class AbonnementBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(AbonnementBean.class.getName());
    
    private GenericLazyDataModel<AbonnementEntity> lazyModel;
    
    private AbonnementEntity abonnement;
    
    @Inject
    private AbonnementService abonnementService;
    
    @Inject
    private PatientService patientService;
    
    @Inject
    private SocieteAbonnementService societeAbonnementService;
    
    private List<PatientEntity> allPatientsList;
    
    private List<SocieteAbonnementEntity> allSocietesList;
    
    public void prepareNewAbonnement() {
        reset();
        this.abonnement = new AbonnementEntity();
        // set any default values now, if you need
        // Example: this.abonnement.setAnything("test");
    }

    public GenericLazyDataModel<AbonnementEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(abonnementService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (abonnement.getId() == null && !isPermitted("abonnement:create")) {
            return "accessDenied";
        } else if (abonnement.getId() != null && !isPermitted(abonnement, "abonnement:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (abonnement.getId() != null) {
                abonnement = abonnementService.update(abonnement);
                message = "message_successfully_updated";
            } else {
                abonnement = abonnementService.save(abonnement);
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
        
        if (!isPermitted(abonnement, "abonnement:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            abonnementService.delete(abonnement);
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
    
    public void onDialogOpen(AbonnementEntity abonnement) {
        reset();
        this.abonnement = abonnement;
    }
    
    public void reset() {
        abonnement = null;

        allPatientsList = null;
        
        allSocietesList = null;
        
    }

    // Get a List of all patient
    public List<PatientEntity> getPatients() {
        if (this.allPatientsList == null) {
            this.allPatientsList = patientService.findAllPatientEntities();
        }
        return this.allPatientsList;
    }
    
    // Update patient of the current abonnement
    public void updatePatient(PatientEntity patient) {
        this.abonnement.setPatient(patient);
        // Maybe we just created and assigned a new patient. So reset the allPatientList.
        allPatientsList = null;
    }
    
    // Get a List of all societe
    public List<SocieteAbonnementEntity> getSocietes() {
        if (this.allSocietesList == null) {
            this.allSocietesList = societeAbonnementService.findAllSocieteAbonnementEntities();
        }
        return this.allSocietesList;
    }
    
    // Update societe of the current abonnement
    public void updateSociete(SocieteAbonnementEntity societeAbonnement) {
        this.abonnement.setSociete(societeAbonnement);
        // Maybe we just created and assigned a new societeAbonnement. So reset the allSocieteList.
        allSocietesList = null;
    }
    
    public AbonnementEntity getAbonnement() {
        if (this.abonnement == null) {
            prepareNewAbonnement();
        }
        return this.abonnement;
    }
    
    public void setAbonnement(AbonnementEntity abonnement) {
        this.abonnement = abonnement;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(AbonnementEntity abonnement, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
