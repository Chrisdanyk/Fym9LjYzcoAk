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

import org.shekinah.domain.HospitalisationEntity;
import org.shekinah.domain.PatientEntity;
import org.shekinah.domain.SalleEntity;
import org.shekinah.service.HospitalisationService;
import org.shekinah.service.PatientService;
import org.shekinah.service.SalleService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("hospitalisationBean")
@ViewScoped
public class HospitalisationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(HospitalisationBean.class.getName());
    
    private GenericLazyDataModel<HospitalisationEntity> lazyModel;
    
    private HospitalisationEntity hospitalisation;
    
    @Inject
    private HospitalisationService hospitalisationService;
    
    @Inject
    private SalleService salleService;
    
    @Inject
    private PatientService patientService;
    
    private List<SalleEntity> allSallesList;
    
    private List<PatientEntity> allPatientsList;
    
    public void prepareNewHospitalisation() {
        reset();
        this.hospitalisation = new HospitalisationEntity();
        // set any default values now, if you need
        // Example: this.hospitalisation.setAnything("test");
    }

    public GenericLazyDataModel<HospitalisationEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(hospitalisationService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (hospitalisation.getId() == null && !isPermitted("hospitalisation:create")) {
            return "accessDenied";
        } else if (hospitalisation.getId() != null && !isPermitted(hospitalisation, "hospitalisation:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (hospitalisation.getId() != null) {
                hospitalisation = hospitalisationService.update(hospitalisation);
                message = "message_successfully_updated";
            } else {
                hospitalisation = hospitalisationService.save(hospitalisation);
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
        
        if (!isPermitted(hospitalisation, "hospitalisation:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            hospitalisationService.delete(hospitalisation);
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
    
    public void onDialogOpen(HospitalisationEntity hospitalisation) {
        reset();
        this.hospitalisation = hospitalisation;
    }
    
    public void reset() {
        hospitalisation = null;

        allSallesList = null;
        
        allPatientsList = null;
        
    }

    // Get a List of all salle
    public List<SalleEntity> getSalles() {
        if (this.allSallesList == null) {
            this.allSallesList = salleService.findAllSalleEntities();
        }
        return this.allSallesList;
    }
    
    // Update salle of the current hospitalisation
    public void updateSalle(SalleEntity salle) {
        this.hospitalisation.setSalle(salle);
        // Maybe we just created and assigned a new salle. So reset the allSalleList.
        allSallesList = null;
    }
    
    // Get a List of all patient
    public List<PatientEntity> getPatients() {
        if (this.allPatientsList == null) {
            this.allPatientsList = patientService.findAllPatientEntities();
        }
        return this.allPatientsList;
    }
    
    // Update patient of the current hospitalisation
    public void updatePatient(PatientEntity patient) {
        this.hospitalisation.setPatient(patient);
        // Maybe we just created and assigned a new patient. So reset the allPatientList.
        allPatientsList = null;
    }
    
    public HospitalisationEntity getHospitalisation() {
        if (this.hospitalisation == null) {
            prepareNewHospitalisation();
        }
        return this.hospitalisation;
    }
    
    public void setHospitalisation(HospitalisationEntity hospitalisation) {
        this.hospitalisation = hospitalisation;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(HospitalisationEntity hospitalisation, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
