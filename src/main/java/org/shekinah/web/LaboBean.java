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

import org.shekinah.domain.ExamenEntity;
import org.shekinah.domain.LaboEntity;
import org.shekinah.domain.PatientEntity;
import org.shekinah.domain.security.UserEntity;
import org.shekinah.service.ExamenService;
import org.shekinah.service.LaboService;
import org.shekinah.service.PatientService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.service.security.UserService;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("laboBean")
@ViewScoped
public class LaboBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(LaboBean.class.getName());
    
    private GenericLazyDataModel<LaboEntity> lazyModel;
    
    private LaboEntity labo;
    
    @Inject
    private LaboService laboService;
    
    @Inject
    private ExamenService examenService;
    
    @Inject
    private PatientService patientService;
    
    @Inject
    private UserService userService;
    
    private List<ExamenEntity> allExamensList;
    
    private List<PatientEntity> allPatientsList;
    
    private List<UserEntity> allLaborantinsList;
    
    public void prepareNewLabo() {
        reset();
        this.labo = new LaboEntity();
        // set any default values now, if you need
        // Example: this.labo.setAnything("test");
    }

    public GenericLazyDataModel<LaboEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(laboService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (labo.getId() == null && !isPermitted("labo:create")) {
            return "accessDenied";
        } else if (labo.getId() != null && !isPermitted(labo, "labo:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (labo.getId() != null) {
                labo = laboService.update(labo);
                message = "message_successfully_updated";
            } else {
                labo = laboService.save(labo);
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
        
        if (!isPermitted(labo, "labo:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            laboService.delete(labo);
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
    
    public void onDialogOpen(LaboEntity labo) {
        reset();
        this.labo = labo;
    }
    
    public void reset() {
        labo = null;

        allExamensList = null;
        
        allPatientsList = null;
        
        allLaborantinsList = null;
        
    }

    // Get a List of all examen
    public List<ExamenEntity> getExamens() {
        if (this.allExamensList == null) {
            this.allExamensList = examenService.findAllExamenEntities();
        }
        return this.allExamensList;
    }
    
    // Update examen of the current labo
    public void updateExamen(ExamenEntity examen) {
        this.labo.setExamen(examen);
        // Maybe we just created and assigned a new examen. So reset the allExamenList.
        allExamensList = null;
    }
    
    // Get a List of all patient
    public List<PatientEntity> getPatients() {
        if (this.allPatientsList == null) {
            this.allPatientsList = patientService.findAllPatientEntities();
        }
        return this.allPatientsList;
    }
    
    // Update patient of the current labo
    public void updatePatient(PatientEntity patient) {
        this.labo.setPatient(patient);
        // Maybe we just created and assigned a new patient. So reset the allPatientList.
        allPatientsList = null;
    }
    
    // Get a List of all laborantin
    public List<UserEntity> getLaborantins() {
        if (this.allLaborantinsList == null) {
            this.allLaborantinsList = userService.findAllUserEntities();
        }
        return this.allLaborantinsList;
    }
    
    // Update laborantin of the current labo
    public void updateLaborantin(UserEntity user) {
        this.labo.setLaborantin(user);
        // Maybe we just created and assigned a new user. So reset the allLaborantinList.
        allLaborantinsList = null;
    }
    
    public LaboEntity getLabo() {
        if (this.labo == null) {
            prepareNewLabo();
        }
        return this.labo;
    }
    
    public void setLabo(LaboEntity labo) {
        this.labo = labo;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(LaboEntity labo, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
