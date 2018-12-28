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
import org.shekinah.domain.FamilleEntity;
import org.shekinah.domain.MedicamentEntity;
import org.shekinah.service.FamilleService;
import org.shekinah.service.MedicamentService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("familleBean")
@ViewScoped
public class FamilleBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(FamilleBean.class.getName());
    
    private GenericLazyDataModel<FamilleEntity> lazyModel;
    
    private FamilleEntity famille;
    
    @Inject
    private FamilleService familleService;
    
    @Inject
    private MedicamentService medicamentService;
    
    private DualListModel<MedicamentEntity> medicaments;
    private List<String> transferedMedicamentIDs;
    private List<String> removedMedicamentIDs;
    
    public void prepareNewFamille() {
        reset();
        this.famille = new FamilleEntity();
        // set any default values now, if you need
        // Example: this.famille.setAnything("test");
    }

    public GenericLazyDataModel<FamilleEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(familleService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (famille.getId() == null && !isPermitted("famille:create")) {
            return "accessDenied";
        } else if (famille.getId() != null && !isPermitted(famille, "famille:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (famille.getId() != null) {
                famille = familleService.update(famille);
                message = "message_successfully_updated";
            } else {
                famille = familleService.save(famille);
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
        
        if (!isPermitted(famille, "famille:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            familleService.delete(famille);
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
    
    public void onDialogOpen(FamilleEntity famille) {
        reset();
        this.famille = famille;
    }
    
    public void reset() {
        famille = null;

        medicaments = null;
        transferedMedicamentIDs = null;
        removedMedicamentIDs = null;
        
    }

    public DualListModel<MedicamentEntity> getMedicaments() {
        return medicaments;
    }

    public void setMedicaments(DualListModel<MedicamentEntity> medicaments) {
        this.medicaments = medicaments;
    }
    
    public List<MedicamentEntity> getFullMedicamentsList() {
        List<MedicamentEntity> allList = new ArrayList<>();
        allList.addAll(medicaments.getSource());
        allList.addAll(medicaments.getTarget());
        return allList;
    }
    
    public void onMedicamentsDialog(FamilleEntity famille) {
        // Prepare the medicament PickList
        this.famille = famille;
        List<MedicamentEntity> selectedMedicamentsFromDB = medicamentService
                .findMedicamentsByFamille(this.famille);
        List<MedicamentEntity> availableMedicamentsFromDB = medicamentService
                .findAvailableMedicaments(this.famille);
        this.medicaments = new DualListModel<>(availableMedicamentsFromDB, selectedMedicamentsFromDB);
        
        transferedMedicamentIDs = new ArrayList<>();
        removedMedicamentIDs = new ArrayList<>();
    }
    
    public void onMedicamentsPickListTransfer(TransferEvent event) {
        // If a medicament is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((MedicamentEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedMedicamentIDs.add(id);
                removedMedicamentIDs.remove(id);
            } else if (event.isRemove()) {
                removedMedicamentIDs.add(id);
                transferedMedicamentIDs.remove(id);
            }
        }
        
    }
    
    public void updateMedicament(MedicamentEntity medicament) {
        // If a new medicament is created, we persist it to the database,
        // but we do not assign it to this famille in the database, yet.
        medicaments.getTarget().add(medicament);
        transferedMedicamentIDs.add(medicament.getId().toString());
    }
    
    public void onMedicamentsSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<MedicamentEntity> selectedMedicamentsFromDB = medicamentService
                    .findMedicamentsByFamille(this.famille);
            List<MedicamentEntity> availableMedicamentsFromDB = medicamentService
                    .findAvailableMedicaments(this.famille);
            
            for (MedicamentEntity medicament : selectedMedicamentsFromDB) {
                if (removedMedicamentIDs.contains(medicament.getId().toString())) {
                    medicament.setFamille(null);
                    medicamentService.update(medicament);
                }
            }
    
            for (MedicamentEntity medicament : availableMedicamentsFromDB) {
                if (transferedMedicamentIDs.contains(medicament.getId().toString())) {
                    medicament.setFamille(famille);
                    medicamentService.update(medicament);
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
    
    public FamilleEntity getFamille() {
        if (this.famille == null) {
            prepareNewFamille();
        }
        return this.famille;
    }
    
    public void setFamille(FamilleEntity famille) {
        this.famille = famille;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(FamilleEntity famille, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
