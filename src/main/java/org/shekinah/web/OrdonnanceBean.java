package org.shekinah.web;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import org.shekinah.domain.OrdonnanceEntity;
import org.shekinah.service.OrdonnanceService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("ordonnanceBean")
@ViewScoped
public class OrdonnanceBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(OrdonnanceBean.class.getName());
    
    private GenericLazyDataModel<OrdonnanceEntity> lazyModel;
    
    private OrdonnanceEntity ordonnance;
    
    @Inject
    private OrdonnanceService ordonnanceService;
    
    public void prepareNewOrdonnance() {
        reset();
        this.ordonnance = new OrdonnanceEntity();
        // set any default values now, if you need
        // Example: this.ordonnance.setAnything("test");
    }

    public GenericLazyDataModel<OrdonnanceEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(ordonnanceService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (ordonnance.getId() == null && !isPermitted("ordonnance:create")) {
            return "accessDenied";
        } else if (ordonnance.getId() != null && !isPermitted(ordonnance, "ordonnance:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (ordonnance.getId() != null) {
                ordonnance = ordonnanceService.update(ordonnance);
                message = "message_successfully_updated";
            } else {
                ordonnance = ordonnanceService.save(ordonnance);
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
        
        if (!isPermitted(ordonnance, "ordonnance:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            ordonnanceService.delete(ordonnance);
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
    
    public void onDialogOpen(OrdonnanceEntity ordonnance) {
        reset();
        this.ordonnance = ordonnance;
    }
    
    public void reset() {
        ordonnance = null;

    }

    public OrdonnanceEntity getOrdonnance() {
        if (this.ordonnance == null) {
            prepareNewOrdonnance();
        }
        return this.ordonnance;
    }
    
    public void setOrdonnance(OrdonnanceEntity ordonnance) {
        this.ordonnance = ordonnance;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(OrdonnanceEntity ordonnance, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
