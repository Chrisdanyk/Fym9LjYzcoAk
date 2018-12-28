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
import org.shekinah.domain.AbonnementEntity;
import org.shekinah.domain.SocieteAbonnementEntity;
import org.shekinah.service.AbonnementService;
import org.shekinah.service.SocieteAbonnementService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("societeAbonnementBean")
@ViewScoped
public class SocieteAbonnementBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(SocieteAbonnementBean.class.getName());
    
    private GenericLazyDataModel<SocieteAbonnementEntity> lazyModel;
    
    private SocieteAbonnementEntity societeAbonnement;
    
    @Inject
    private SocieteAbonnementService societeAbonnementService;
    
    @Inject
    private AbonnementService abonnementService;
    
    private DualListModel<AbonnementEntity> abonnements;
    private List<String> transferedAbonnementIDs;
    private List<String> removedAbonnementIDs;
    
    public void prepareNewSocieteAbonnement() {
        reset();
        this.societeAbonnement = new SocieteAbonnementEntity();
        // set any default values now, if you need
        // Example: this.societeAbonnement.setAnything("test");
    }

    public GenericLazyDataModel<SocieteAbonnementEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(societeAbonnementService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (societeAbonnement.getId() == null && !isPermitted("societeAbonnement:create")) {
            return "accessDenied";
        } else if (societeAbonnement.getId() != null && !isPermitted(societeAbonnement, "societeAbonnement:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (societeAbonnement.getId() != null) {
                societeAbonnement = societeAbonnementService.update(societeAbonnement);
                message = "message_successfully_updated";
            } else {
                societeAbonnement = societeAbonnementService.save(societeAbonnement);
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
        
        if (!isPermitted(societeAbonnement, "societeAbonnement:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            societeAbonnementService.delete(societeAbonnement);
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
    
    public void onDialogOpen(SocieteAbonnementEntity societeAbonnement) {
        reset();
        this.societeAbonnement = societeAbonnement;
    }
    
    public void reset() {
        societeAbonnement = null;

        abonnements = null;
        transferedAbonnementIDs = null;
        removedAbonnementIDs = null;
        
    }

    public DualListModel<AbonnementEntity> getAbonnements() {
        return abonnements;
    }

    public void setAbonnements(DualListModel<AbonnementEntity> abonnements) {
        this.abonnements = abonnements;
    }
    
    public List<AbonnementEntity> getFullAbonnementsList() {
        List<AbonnementEntity> allList = new ArrayList<>();
        allList.addAll(abonnements.getSource());
        allList.addAll(abonnements.getTarget());
        return allList;
    }
    
    public void onAbonnementsDialog(SocieteAbonnementEntity societeAbonnement) {
        // Prepare the abonnement PickList
        this.societeAbonnement = societeAbonnement;
        List<AbonnementEntity> selectedAbonnementsFromDB = abonnementService
                .findAbonnementsBySociete(this.societeAbonnement);
        List<AbonnementEntity> availableAbonnementsFromDB = abonnementService
                .findAvailableAbonnements(this.societeAbonnement);
        this.abonnements = new DualListModel<>(availableAbonnementsFromDB, selectedAbonnementsFromDB);
        
        transferedAbonnementIDs = new ArrayList<>();
        removedAbonnementIDs = new ArrayList<>();
    }
    
    public void onAbonnementsPickListTransfer(TransferEvent event) {
        // If a abonnement is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((AbonnementEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedAbonnementIDs.add(id);
                removedAbonnementIDs.remove(id);
            } else if (event.isRemove()) {
                removedAbonnementIDs.add(id);
                transferedAbonnementIDs.remove(id);
            }
        }
        
    }
    
    public void updateAbonnement(AbonnementEntity abonnement) {
        // If a new abonnement is created, we persist it to the database,
        // but we do not assign it to this societeAbonnement in the database, yet.
        abonnements.getTarget().add(abonnement);
        transferedAbonnementIDs.add(abonnement.getId().toString());
    }
    
    public void onAbonnementsSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<AbonnementEntity> selectedAbonnementsFromDB = abonnementService
                    .findAbonnementsBySociete(this.societeAbonnement);
            List<AbonnementEntity> availableAbonnementsFromDB = abonnementService
                    .findAvailableAbonnements(this.societeAbonnement);
            
            for (AbonnementEntity abonnement : selectedAbonnementsFromDB) {
                if (removedAbonnementIDs.contains(abonnement.getId().toString())) {
                    abonnement.setSociete(null);
                    abonnementService.update(abonnement);
                }
            }
    
            for (AbonnementEntity abonnement : availableAbonnementsFromDB) {
                if (transferedAbonnementIDs.contains(abonnement.getId().toString())) {
                    abonnement.setSociete(societeAbonnement);
                    abonnementService.update(abonnement);
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
    
    public SocieteAbonnementEntity getSocieteAbonnement() {
        if (this.societeAbonnement == null) {
            prepareNewSocieteAbonnement();
        }
        return this.societeAbonnement;
    }
    
    public void setSocieteAbonnement(SocieteAbonnementEntity societeAbonnement) {
        this.societeAbonnement = societeAbonnement;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(SocieteAbonnementEntity societeAbonnement, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
