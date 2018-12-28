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
import org.shekinah.domain.CreneauEntity;
import org.shekinah.domain.RendezvousEntity;
import org.shekinah.domain.security.UserEntity;
import org.shekinah.service.CreneauService;
import org.shekinah.service.RendezvousService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.service.security.UserService;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("creneauBean")
@ViewScoped
public class CreneauBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(CreneauBean.class.getName());
    
    private GenericLazyDataModel<CreneauEntity> lazyModel;
    
    private CreneauEntity creneau;
    
    @Inject
    private CreneauService creneauService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private RendezvousService rendezvousService;
    
    private DualListModel<RendezvousEntity> rendezvouss;
    private List<String> transferedRendezvousIDs;
    private List<String> removedRendezvousIDs;
    
    private List<UserEntity> allMedecinsList;
    
    public void prepareNewCreneau() {
        reset();
        this.creneau = new CreneauEntity();
        // set any default values now, if you need
        // Example: this.creneau.setAnything("test");
    }

    public GenericLazyDataModel<CreneauEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(creneauService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (creneau.getId() == null && !isPermitted("creneau:create")) {
            return "accessDenied";
        } else if (creneau.getId() != null && !isPermitted(creneau, "creneau:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (creneau.getId() != null) {
                creneau = creneauService.update(creneau);
                message = "message_successfully_updated";
            } else {
                creneau = creneauService.save(creneau);
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
        
        if (!isPermitted(creneau, "creneau:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            creneauService.delete(creneau);
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
    
    public void onDialogOpen(CreneauEntity creneau) {
        reset();
        this.creneau = creneau;
    }
    
    public void reset() {
        creneau = null;

        rendezvouss = null;
        transferedRendezvousIDs = null;
        removedRendezvousIDs = null;
        
        allMedecinsList = null;
        
    }

    // Get a List of all medecin
    public List<UserEntity> getMedecins() {
        if (this.allMedecinsList == null) {
            this.allMedecinsList = userService.findAllUserEntities();
        }
        return this.allMedecinsList;
    }
    
    // Update medecin of the current creneau
    public void updateMedecin(UserEntity user) {
        this.creneau.setMedecin(user);
        // Maybe we just created and assigned a new user. So reset the allMedecinList.
        allMedecinsList = null;
    }
    
    public DualListModel<RendezvousEntity> getRendezvouss() {
        return rendezvouss;
    }

    public void setRendezvouss(DualListModel<RendezvousEntity> rendezvouss) {
        this.rendezvouss = rendezvouss;
    }
    
    public List<RendezvousEntity> getFullRendezvoussList() {
        List<RendezvousEntity> allList = new ArrayList<>();
        allList.addAll(rendezvouss.getSource());
        allList.addAll(rendezvouss.getTarget());
        return allList;
    }
    
    public void onRendezvoussDialog(CreneauEntity creneau) {
        // Prepare the rendezvous PickList
        this.creneau = creneau;
        List<RendezvousEntity> selectedRendezvoussFromDB = rendezvousService
                .findRendezvoussByCreneau(this.creneau);
        List<RendezvousEntity> availableRendezvoussFromDB = rendezvousService
                .findAvailableRendezvouss(this.creneau);
        this.rendezvouss = new DualListModel<>(availableRendezvoussFromDB, selectedRendezvoussFromDB);
        
        transferedRendezvousIDs = new ArrayList<>();
        removedRendezvousIDs = new ArrayList<>();
    }
    
    public void onRendezvoussPickListTransfer(TransferEvent event) {
        // If a rendezvous is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((RendezvousEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedRendezvousIDs.add(id);
                removedRendezvousIDs.remove(id);
            } else if (event.isRemove()) {
                removedRendezvousIDs.add(id);
                transferedRendezvousIDs.remove(id);
            }
        }
        
    }
    
    public void updateRendezvous(RendezvousEntity rendezvous) {
        // If a new rendezvous is created, we persist it to the database,
        // but we do not assign it to this creneau in the database, yet.
        rendezvouss.getTarget().add(rendezvous);
        transferedRendezvousIDs.add(rendezvous.getId().toString());
    }
    
    public void onRendezvoussSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<RendezvousEntity> selectedRendezvoussFromDB = rendezvousService
                    .findRendezvoussByCreneau(this.creneau);
            List<RendezvousEntity> availableRendezvoussFromDB = rendezvousService
                    .findAvailableRendezvouss(this.creneau);
            
            for (RendezvousEntity rendezvous : selectedRendezvoussFromDB) {
                if (removedRendezvousIDs.contains(rendezvous.getId().toString())) {
                    rendezvous.setCreneau(null);
                    rendezvousService.update(rendezvous);
                }
            }
    
            for (RendezvousEntity rendezvous : availableRendezvoussFromDB) {
                if (transferedRendezvousIDs.contains(rendezvous.getId().toString())) {
                    rendezvous.setCreneau(creneau);
                    rendezvousService.update(rendezvous);
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
    
    public CreneauEntity getCreneau() {
        if (this.creneau == null) {
            prepareNewCreneau();
        }
        return this.creneau;
    }
    
    public void setCreneau(CreneauEntity creneau) {
        this.creneau = creneau;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(CreneauEntity creneau, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
