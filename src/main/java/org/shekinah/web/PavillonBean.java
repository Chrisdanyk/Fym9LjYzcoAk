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
import org.shekinah.domain.PavillonEntity;
import org.shekinah.domain.SalleEntity;
import org.shekinah.service.PavillonService;
import org.shekinah.service.SalleService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("pavillonBean")
@ViewScoped
public class PavillonBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(PavillonBean.class.getName());
    
    private GenericLazyDataModel<PavillonEntity> lazyModel;
    
    private PavillonEntity pavillon;
    
    @Inject
    private PavillonService pavillonService;
    
    @Inject
    private SalleService salleService;
    
    private DualListModel<SalleEntity> salles;
    private List<String> transferedSalleIDs;
    private List<String> removedSalleIDs;
    
    public void prepareNewPavillon() {
        reset();
        this.pavillon = new PavillonEntity();
        // set any default values now, if you need
        // Example: this.pavillon.setAnything("test");
    }

    public GenericLazyDataModel<PavillonEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(pavillonService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (pavillon.getId() == null && !isPermitted("pavillon:create")) {
            return "accessDenied";
        } else if (pavillon.getId() != null && !isPermitted(pavillon, "pavillon:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (pavillon.getId() != null) {
                pavillon = pavillonService.update(pavillon);
                message = "message_successfully_updated";
            } else {
                pavillon = pavillonService.save(pavillon);
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
        
        if (!isPermitted(pavillon, "pavillon:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            pavillonService.delete(pavillon);
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
    
    public void onDialogOpen(PavillonEntity pavillon) {
        reset();
        this.pavillon = pavillon;
    }
    
    public void reset() {
        pavillon = null;

        salles = null;
        transferedSalleIDs = null;
        removedSalleIDs = null;
        
    }

    public DualListModel<SalleEntity> getSalles() {
        return salles;
    }

    public void setSalles(DualListModel<SalleEntity> salles) {
        this.salles = salles;
    }
    
    public List<SalleEntity> getFullSallesList() {
        List<SalleEntity> allList = new ArrayList<>();
        allList.addAll(salles.getSource());
        allList.addAll(salles.getTarget());
        return allList;
    }
    
    public void onSallesDialog(PavillonEntity pavillon) {
        // Prepare the salle PickList
        this.pavillon = pavillon;
        List<SalleEntity> selectedSallesFromDB = salleService
                .findSallesByPavillon(this.pavillon);
        List<SalleEntity> availableSallesFromDB = salleService
                .findAvailableSalles(this.pavillon);
        this.salles = new DualListModel<>(availableSallesFromDB, selectedSallesFromDB);
        
        transferedSalleIDs = new ArrayList<>();
        removedSalleIDs = new ArrayList<>();
    }
    
    public void onSallesPickListTransfer(TransferEvent event) {
        // If a salle is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((SalleEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedSalleIDs.add(id);
                removedSalleIDs.remove(id);
            } else if (event.isRemove()) {
                removedSalleIDs.add(id);
                transferedSalleIDs.remove(id);
            }
        }
        
    }
    
    public void updateSalle(SalleEntity salle) {
        // If a new salle is created, we persist it to the database,
        // but we do not assign it to this pavillon in the database, yet.
        salles.getTarget().add(salle);
        transferedSalleIDs.add(salle.getId().toString());
    }
    
    public void onSallesSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<SalleEntity> selectedSallesFromDB = salleService
                    .findSallesByPavillon(this.pavillon);
            List<SalleEntity> availableSallesFromDB = salleService
                    .findAvailableSalles(this.pavillon);
            
            for (SalleEntity salle : selectedSallesFromDB) {
                if (removedSalleIDs.contains(salle.getId().toString())) {
                    salle.setPavillon(null);
                    salleService.update(salle);
                }
            }
    
            for (SalleEntity salle : availableSallesFromDB) {
                if (transferedSalleIDs.contains(salle.getId().toString())) {
                    salle.setPavillon(pavillon);
                    salleService.update(salle);
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
    
    public PavillonEntity getPavillon() {
        if (this.pavillon == null) {
            prepareNewPavillon();
        }
        return this.pavillon;
    }
    
    public void setPavillon(PavillonEntity pavillon) {
        this.pavillon = pavillon;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(PavillonEntity pavillon, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
