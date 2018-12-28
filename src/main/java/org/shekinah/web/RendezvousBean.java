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

import org.shekinah.domain.CreneauEntity;
import org.shekinah.domain.PatientEntity;
import org.shekinah.domain.RendezvousEntity;
import org.shekinah.service.CreneauService;
import org.shekinah.service.PatientService;
import org.shekinah.service.RendezvousService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("rendezvousBean")
@ViewScoped
public class RendezvousBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(RendezvousBean.class.getName());
    
    private GenericLazyDataModel<RendezvousEntity> lazyModel;
    
    private RendezvousEntity rendezvous;
    
    @Inject
    private RendezvousService rendezvousService;
    
    @Inject
    private PatientService patientService;
    
    @Inject
    private CreneauService creneauService;
    
    private List<PatientEntity> allPatientsList;
    
    private List<CreneauEntity> allCreneausList;
    
    public void prepareNewRendezvous() {
        reset();
        this.rendezvous = new RendezvousEntity();
        // set any default values now, if you need
        // Example: this.rendezvous.setAnything("test");
    }

    public GenericLazyDataModel<RendezvousEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(rendezvousService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (rendezvous.getId() == null && !isPermitted("rendezvous:create")) {
            return "accessDenied";
        } else if (rendezvous.getId() != null && !isPermitted(rendezvous, "rendezvous:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (rendezvous.getId() != null) {
                rendezvous = rendezvousService.update(rendezvous);
                message = "message_successfully_updated";
            } else {
                rendezvous = rendezvousService.save(rendezvous);
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
        
        if (!isPermitted(rendezvous, "rendezvous:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            rendezvousService.delete(rendezvous);
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
    
    public void onDialogOpen(RendezvousEntity rendezvous) {
        reset();
        this.rendezvous = rendezvous;
    }
    
    public void reset() {
        rendezvous = null;

        allPatientsList = null;
        
        allCreneausList = null;
        
    }

    // Get a List of all patient
    public List<PatientEntity> getPatients() {
        if (this.allPatientsList == null) {
            this.allPatientsList = patientService.findAllPatientEntities();
        }
        return this.allPatientsList;
    }
    
    // Update patient of the current rendezvous
    public void updatePatient(PatientEntity patient) {
        this.rendezvous.setPatient(patient);
        // Maybe we just created and assigned a new patient. So reset the allPatientList.
        allPatientsList = null;
    }
    
    // Get a List of all creneau
    public List<CreneauEntity> getCreneaus() {
        if (this.allCreneausList == null) {
            this.allCreneausList = creneauService.findAllCreneauEntities();
        }
        return this.allCreneausList;
    }
    
    // Update creneau of the current rendezvous
    public void updateCreneau(CreneauEntity creneau) {
        this.rendezvous.setCreneau(creneau);
        // Maybe we just created and assigned a new creneau. So reset the allCreneauList.
        allCreneausList = null;
    }
    
    public RendezvousEntity getRendezvous() {
        if (this.rendezvous == null) {
            prepareNewRendezvous();
        }
        return this.rendezvous;
    }
    
    public void setRendezvous(RendezvousEntity rendezvous) {
        this.rendezvous = rendezvous;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(RendezvousEntity rendezvous, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
