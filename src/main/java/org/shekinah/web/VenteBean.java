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

import org.shekinah.domain.MedicamentEntity;
import org.shekinah.domain.VenteEntity;
import org.shekinah.domain.security.UserEntity;
import org.shekinah.service.MedicamentService;
import org.shekinah.service.VenteService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.service.security.UserService;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("venteBean")
@ViewScoped
public class VenteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(VenteBean.class.getName());
    
    private GenericLazyDataModel<VenteEntity> lazyModel;
    
    private VenteEntity vente;
    
    @Inject
    private VenteService venteService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private MedicamentService medicamentService;
    
    private List<UserEntity> allPharmaciensList;
    
    private List<MedicamentEntity> allMedicamentsList;
    
    public void prepareNewVente() {
        reset();
        this.vente = new VenteEntity();
        // set any default values now, if you need
        // Example: this.vente.setAnything("test");
    }

    public GenericLazyDataModel<VenteEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(venteService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (vente.getId() == null && !isPermitted("vente:create")) {
            return "accessDenied";
        } else if (vente.getId() != null && !isPermitted(vente, "vente:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (vente.getId() != null) {
                vente = venteService.update(vente);
                message = "message_successfully_updated";
            } else {
                vente = venteService.save(vente);
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
        
        if (!isPermitted(vente, "vente:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            venteService.delete(vente);
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
    
    public void onDialogOpen(VenteEntity vente) {
        reset();
        this.vente = vente;
    }
    
    public void reset() {
        vente = null;

        allPharmaciensList = null;
        
        allMedicamentsList = null;
        
    }

    // Get a List of all pharmacien
    public List<UserEntity> getPharmaciens() {
        if (this.allPharmaciensList == null) {
            this.allPharmaciensList = userService.findAllUserEntities();
        }
        return this.allPharmaciensList;
    }
    
    // Update pharmacien of the current vente
    public void updatePharmacien(UserEntity user) {
        this.vente.setPharmacien(user);
        // Maybe we just created and assigned a new user. So reset the allPharmacienList.
        allPharmaciensList = null;
    }
    
    // Get a List of all medicament
    public List<MedicamentEntity> getMedicaments() {
        if (this.allMedicamentsList == null) {
            this.allMedicamentsList = medicamentService.findAllMedicamentEntities();
        }
        return this.allMedicamentsList;
    }
    
    // Update medicament of the current vente
    public void updateMedicament(MedicamentEntity medicament) {
        this.vente.setMedicament(medicament);
        // Maybe we just created and assigned a new medicament. So reset the allMedicamentList.
        allMedicamentsList = null;
    }
    
    public VenteEntity getVente() {
        if (this.vente == null) {
            prepareNewVente();
        }
        return this.vente;
    }
    
    public void setVente(VenteEntity vente) {
        this.vente = vente;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(VenteEntity vente, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
