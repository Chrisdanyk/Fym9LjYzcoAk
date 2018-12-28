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
import org.shekinah.domain.ServiceEntity;
import org.shekinah.domain.security.UserEntity;
import org.shekinah.service.ServiceService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.service.security.UserService;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("serviceBean")
@ViewScoped
public class ServiceBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(ServiceBean.class.getName());
    
    private GenericLazyDataModel<ServiceEntity> lazyModel;
    
    private ServiceEntity service;
    
    @Inject
    private ServiceService serviceService;
    
    @Inject
    private UserService userService;
    
    private DualListModel<UserEntity> users;
    private List<String> transferedUserIDs;
    private List<String> removedUserIDs;
    
    public void prepareNewService() {
        reset();
        this.service = new ServiceEntity();
        // set any default values now, if you need
        // Example: this.service.setAnything("test");
    }

    public GenericLazyDataModel<ServiceEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(serviceService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (service.getId() == null && !isPermitted("service:create")) {
            return "accessDenied";
        } else if (service.getId() != null && !isPermitted(service, "service:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (service.getId() != null) {
                service = serviceService.update(service);
                message = "message_successfully_updated";
            } else {
                service = serviceService.save(service);
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
        
        if (!isPermitted(service, "service:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            serviceService.delete(service);
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
    
    public void onDialogOpen(ServiceEntity service) {
        reset();
        this.service = service;
    }
    
    public void reset() {
        service = null;

        users = null;
        transferedUserIDs = null;
        removedUserIDs = null;
        
    }

    public DualListModel<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(DualListModel<UserEntity> users) {
        this.users = users;
    }
    
    public List<UserEntity> getFullUsersList() {
        List<UserEntity> allList = new ArrayList<>();
        allList.addAll(users.getSource());
        allList.addAll(users.getTarget());
        return allList;
    }
    
    public void onUsersDialog(ServiceEntity service) {
        // Prepare the user PickList
        this.service = service;
        List<UserEntity> selectedUsersFromDB = userService
                .findUsersByService(this.service);
        List<UserEntity> availableUsersFromDB = userService
                .findAvailableUsers(this.service);
        this.users = new DualListModel<>(availableUsersFromDB, selectedUsersFromDB);
        
        transferedUserIDs = new ArrayList<>();
        removedUserIDs = new ArrayList<>();
    }
    
    public void onUsersPickListTransfer(TransferEvent event) {
        // If a user is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((UserEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedUserIDs.add(id);
                removedUserIDs.remove(id);
            } else if (event.isRemove()) {
                removedUserIDs.add(id);
                transferedUserIDs.remove(id);
            }
        }
        
    }
    
    public void updateUser(UserEntity user) {
        // If a new user is created, we persist it to the database,
        // but we do not assign it to this service in the database, yet.
        users.getTarget().add(user);
        transferedUserIDs.add(user.getId().toString());
    }
    
    public void onUsersSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<UserEntity> selectedUsersFromDB = userService
                    .findUsersByService(this.service);
            List<UserEntity> availableUsersFromDB = userService
                    .findAvailableUsers(this.service);
            
            for (UserEntity user : selectedUsersFromDB) {
                if (removedUserIDs.contains(user.getId().toString())) {
                    user.setService(null);
                    userService.update(user);
                }
            }
    
            for (UserEntity user : availableUsersFromDB) {
                if (transferedUserIDs.contains(user.getId().toString())) {
                    user.setService(service);
                    userService.update(user);
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
    
    public ServiceEntity getService() {
        if (this.service == null) {
            prepareNewService();
        }
        return this.service;
    }
    
    public void setService(ServiceEntity service) {
        this.service = service;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(ServiceEntity service, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
