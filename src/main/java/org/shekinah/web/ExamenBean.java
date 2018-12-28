package org.shekinah.web;

import java.io.Serializable;
import java.util.ArrayList;
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

import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.shekinah.domain.ConsultationEntity;
import org.shekinah.domain.ExamenEntity;
import org.shekinah.domain.LaboEntity;
import org.shekinah.service.ConsultationService;
import org.shekinah.service.ExamenService;
import org.shekinah.service.LaboService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("examenBean")
@ViewScoped
public class ExamenBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(ExamenBean.class.getName());
    
    private GenericLazyDataModel<ExamenEntity> lazyModel;
    
    private ExamenEntity examen;
    
    @Inject
    private ExamenService examenService;
    
    @Inject
    private ConsultationService consultationService;
    
    @Inject
    private LaboService laboService;
    
    private DualListModel<ConsultationEntity> consultations;
    private List<String> transferedConsultationIDs;
    private List<String> removedConsultationIDs;
    
    private DualListModel<LaboEntity> labos;
    private List<String> transferedLaboIDs;
    private List<String> removedLaboIDs;
    
    public void prepareNewExamen() {
        reset();
        this.examen = new ExamenEntity();
        // set any default values now, if you need
        // Example: this.examen.setAnything("test");
    }

    public GenericLazyDataModel<ExamenEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(examenService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (examen.getId() == null && !isPermitted("examen:create")) {
            return "accessDenied";
        } else if (examen.getId() != null && !isPermitted(examen, "examen:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (examen.getId() != null) {
                examen = examenService.update(examen);
                message = "message_successfully_updated";
            } else {
                examen = examenService.save(examen);
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
        
        if (!isPermitted(examen, "examen:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            examenService.delete(examen);
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
    
    public void onDialogOpen(ExamenEntity examen) {
        reset();
        this.examen = examen;
    }
    
    public void reset() {
        examen = null;

        consultations = null;
        transferedConsultationIDs = null;
        removedConsultationIDs = null;
        
        labos = null;
        transferedLaboIDs = null;
        removedLaboIDs = null;
        
    }

    public DualListModel<ConsultationEntity> getConsultations() {
        return consultations;
    }

    public void setConsultations(DualListModel<ConsultationEntity> consultations) {
        this.consultations = consultations;
    }
    
    public List<ConsultationEntity> getFullConsultationsList() {
        List<ConsultationEntity> allList = new ArrayList<>();
        allList.addAll(consultations.getSource());
        allList.addAll(consultations.getTarget());
        return allList;
    }
    
    public void onConsultationsDialog(ExamenEntity examen) {
        // Prepare the consultation PickList
        this.examen = examen;
        List<ConsultationEntity> selectedConsultationsFromDB = consultationService
                .findConsultationsByExamens(this.examen);
        List<ConsultationEntity> availableConsultationsFromDB = consultationService
                .findAvailableConsultations(this.examen);
        this.consultations = new DualListModel<>(availableConsultationsFromDB, selectedConsultationsFromDB);
        
        transferedConsultationIDs = new ArrayList<>();
        removedConsultationIDs = new ArrayList<>();
    }
    
    public void onConsultationsPickListTransfer(TransferEvent event) {
        // If a consultation is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((ConsultationEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedConsultationIDs.add(id);
                removedConsultationIDs.remove(id);
            } else if (event.isRemove()) {
                removedConsultationIDs.add(id);
                transferedConsultationIDs.remove(id);
            }
        }
        
    }
    
    public void updateConsultation(ConsultationEntity consultation) {
        // If a new consultation is created, we persist it to the database,
        // but we do not assign it to this examen in the database, yet.
        consultations.getTarget().add(consultation);
        transferedConsultationIDs.add(consultation.getId().toString());
    }
    
    public DualListModel<LaboEntity> getLabos() {
        return labos;
    }

    public void setLabos(DualListModel<LaboEntity> labos) {
        this.labos = labos;
    }
    
    public List<LaboEntity> getFullLabosList() {
        List<LaboEntity> allList = new ArrayList<>();
        allList.addAll(labos.getSource());
        allList.addAll(labos.getTarget());
        return allList;
    }
    
    public void onLabosDialog(ExamenEntity examen) {
        // Prepare the labo PickList
        this.examen = examen;
        List<LaboEntity> selectedLabosFromDB = laboService
                .findLabosByExamen(this.examen);
        List<LaboEntity> availableLabosFromDB = laboService
                .findAvailableLabos(this.examen);
        this.labos = new DualListModel<>(availableLabosFromDB, selectedLabosFromDB);
        
        transferedLaboIDs = new ArrayList<>();
        removedLaboIDs = new ArrayList<>();
    }
    
    public void onLabosPickListTransfer(TransferEvent event) {
        // If a labo is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((LaboEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedLaboIDs.add(id);
                removedLaboIDs.remove(id);
            } else if (event.isRemove()) {
                removedLaboIDs.add(id);
                transferedLaboIDs.remove(id);
            }
        }
        
    }
    
    public void updateLabo(LaboEntity labo) {
        // If a new labo is created, we persist it to the database,
        // but we do not assign it to this examen in the database, yet.
        labos.getTarget().add(labo);
        transferedLaboIDs.add(labo.getId().toString());
    }
    
    public void onConsultationsSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<ConsultationEntity> selectedConsultationsFromDB = consultationService
                    .findConsultationsByExamens(this.examen);
            List<ConsultationEntity> availableConsultationsFromDB = consultationService
                    .findAvailableConsultations(this.examen);
            
            for (ConsultationEntity consultation : selectedConsultationsFromDB) {
                if (removedConsultationIDs.contains(consultation.getId().toString())) {
                    consultation.setExamens(null);
                    consultationService.update(consultation);
                }
            }
    
            for (ConsultationEntity consultation : availableConsultationsFromDB) {
                if (transferedConsultationIDs.contains(consultation.getId().toString())) {
                    consultation.setExamens(examen);
                    consultationService.update(consultation);
                }
            }
            
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_changes_saved");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            
            reset();

        } catch (OptimisticLockException e) {
            logger.log(Level.SEVERE, "Error occured", e);
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_optimistic_locking_exception");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        } catch (PersistenceException e) {
            logger.log(Level.SEVERE, "Error occured", e);
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_picklist_save_exception");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        }
    }
    
    public void onLabosSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<LaboEntity> selectedLabosFromDB = laboService
                    .findLabosByExamen(this.examen);
            List<LaboEntity> availableLabosFromDB = laboService
                    .findAvailableLabos(this.examen);
            
            for (LaboEntity labo : selectedLabosFromDB) {
                if (removedLaboIDs.contains(labo.getId().toString())) {
                    labo.setExamen(null);
                    laboService.update(labo);
                }
            }
    
            for (LaboEntity labo : availableLabosFromDB) {
                if (transferedLaboIDs.contains(labo.getId().toString())) {
                    labo.setExamen(examen);
                    laboService.update(labo);
                }
            }
            
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_changes_saved");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            
            reset();

        } catch (OptimisticLockException e) {
            logger.log(Level.SEVERE, "Error occured", e);
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_optimistic_locking_exception");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        } catch (PersistenceException e) {
            logger.log(Level.SEVERE, "Error occured", e);
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_picklist_save_exception");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        }
    }
    
    public ExamenEntity getExamen() {
        if (this.examen == null) {
            prepareNewExamen();
        }
        return this.examen;
    }
    
    public void setExamen(ExamenEntity examen) {
        this.examen = examen;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(ExamenEntity examen, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
