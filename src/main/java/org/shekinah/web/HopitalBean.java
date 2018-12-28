package org.shekinah.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;
import org.shekinah.domain.HopitalEntity;
import org.shekinah.domain.HopitalImage;
import org.shekinah.domain.PatientEntity;
import org.shekinah.domain.security.UserEntity;
import org.shekinah.service.HopitalService;
import org.shekinah.service.PatientService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.service.security.UserService;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("hopitalBean")
@ViewScoped
public class HopitalBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(HopitalBean.class.getName());
    
    private GenericLazyDataModel<HopitalEntity> lazyModel;
    
    private HopitalEntity hopital;
    
    @Inject
    private HopitalService hopitalService;
    
    UploadedFile uploadedImage;
    byte[] uploadedImageContents;
    
    @Inject
    private UserService userService;
    
    @Inject
    private PatientService patientService;
    
    private DualListModel<UserEntity> users;
    private List<String> transferedUserIDs;
    private List<String> removedUserIDs;
    
    private DualListModel<PatientEntity> patients;
    private List<String> transferedPatientIDs;
    private List<String> removedPatientIDs;
    
    public void prepareNewHopital() {
        reset();
        this.hopital = new HopitalEntity();
        // set any default values now, if you need
        // Example: this.hopital.setAnything("test");
    }

    public GenericLazyDataModel<HopitalEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(hopitalService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (hopital.getId() == null && !isPermitted("hopital:create")) {
            return "accessDenied";
        } else if (hopital.getId() != null && !isPermitted(hopital, "hopital:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (this.uploadedImage != null) {
                try {

                    BufferedImage image;
                    try (InputStream in = new ByteArrayInputStream(uploadedImageContents)) {
                        image = ImageIO.read(in);
                    }
                    image = Scalr.resize(image, Method.BALANCED, 300);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageOutputStream imageOS = ImageIO.createImageOutputStream(baos);
                    Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(
                            uploadedImage.getContentType());
                    ImageWriter imageWriter = (ImageWriter) imageWriters.next();
                    imageWriter.setOutput(imageOS);
                    imageWriter.write(image);
                    
                    baos.close();
                    imageOS.close();
                    
                    logger.log(Level.INFO, "Resized uploaded image from {0} to {1}", new Object[]{uploadedImageContents.length, baos.toByteArray().length});
            
                    HopitalImage hopitalImage = new HopitalImage();
                    hopitalImage.setContent(baos.toByteArray());
                    hopital.setImage(hopitalImage);
                } catch (Exception e) {
                    FacesMessage facesMessage = MessageFactory.getMessage(
                            "message_upload_exception");
                    FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                    FacesContext.getCurrentInstance().validationFailed();
                    return null;
                }
            }
            
            if (hopital.getId() != null) {
                hopital = hopitalService.update(hopital);
                message = "message_successfully_updated";
            } else {
                hopital = hopitalService.save(hopital);
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
        
        if (!isPermitted(hopital, "hopital:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            hopitalService.delete(hopital);
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
    
    public void onDialogOpen(HopitalEntity hopital) {
        reset();
        this.hopital = hopital;
    }
    
    public void reset() {
        hopital = null;

        users = null;
        transferedUserIDs = null;
        removedUserIDs = null;
        
        patients = null;
        transferedPatientIDs = null;
        removedPatientIDs = null;
        
        uploadedImage = null;
        uploadedImageContents = null;
        
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
    
    public void onUsersDialog(HopitalEntity hopital) {
        // Prepare the user PickList
        this.hopital = hopital;
        List<UserEntity> selectedUsersFromDB = userService
                .findUsersByHopital(this.hopital);
        List<UserEntity> availableUsersFromDB = userService
                .findAvailableUsers(this.hopital);
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
        // but we do not assign it to this hopital in the database, yet.
        users.getTarget().add(user);
        transferedUserIDs.add(user.getId().toString());
    }
    
    public DualListModel<PatientEntity> getPatients() {
        return patients;
    }

    public void setPatients(DualListModel<PatientEntity> patients) {
        this.patients = patients;
    }
    
    public List<PatientEntity> getFullPatientsList() {
        List<PatientEntity> allList = new ArrayList<>();
        allList.addAll(patients.getSource());
        allList.addAll(patients.getTarget());
        return allList;
    }
    
    public void onPatientsDialog(HopitalEntity hopital) {
        // Prepare the patient PickList
        this.hopital = hopital;
        List<PatientEntity> selectedPatientsFromDB = patientService
                .findPatientsByHopital(this.hopital);
        List<PatientEntity> availablePatientsFromDB = patientService
                .findAvailablePatients(this.hopital);
        this.patients = new DualListModel<>(availablePatientsFromDB, selectedPatientsFromDB);
        
        transferedPatientIDs = new ArrayList<>();
        removedPatientIDs = new ArrayList<>();
    }
    
    public void onPatientsPickListTransfer(TransferEvent event) {
        // If a patient is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((PatientEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedPatientIDs.add(id);
                removedPatientIDs.remove(id);
            } else if (event.isRemove()) {
                removedPatientIDs.add(id);
                transferedPatientIDs.remove(id);
            }
        }
        
    }
    
    public void updatePatient(PatientEntity patient) {
        // If a new patient is created, we persist it to the database,
        // but we do not assign it to this hopital in the database, yet.
        patients.getTarget().add(patient);
        transferedPatientIDs.add(patient.getId().toString());
    }
    
    public void onUsersSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<UserEntity> selectedUsersFromDB = userService
                    .findUsersByHopital(this.hopital);
            List<UserEntity> availableUsersFromDB = userService
                    .findAvailableUsers(this.hopital);
            
            for (UserEntity user : selectedUsersFromDB) {
                if (removedUserIDs.contains(user.getId().toString())) {
                    user.setHopital(null);
                    userService.update(user);
                }
            }
    
            for (UserEntity user : availableUsersFromDB) {
                if (transferedUserIDs.contains(user.getId().toString())) {
                    user.setHopital(hopital);
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
    
    public void onPatientsSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<PatientEntity> selectedPatientsFromDB = patientService
                    .findPatientsByHopital(this.hopital);
            List<PatientEntity> availablePatientsFromDB = patientService
                    .findAvailablePatients(this.hopital);
            
            for (PatientEntity patient : selectedPatientsFromDB) {
                if (removedPatientIDs.contains(patient.getId().toString())) {
                    patient.setHopital(null);
                    patientService.update(patient);
                }
            }
    
            for (PatientEntity patient : availablePatientsFromDB) {
                if (transferedPatientIDs.contains(patient.getId().toString())) {
                    patient.setHopital(hopital);
                    patientService.update(patient);
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
    
    public void handleImageUpload(FileUploadEvent event) {
        
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(
                event.getFile().getContentType());
        if (!imageWriters.hasNext()) {
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_image_type_not_supported");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            return;
        }
        
        this.uploadedImage = event.getFile();
        this.uploadedImageContents = event.getFile().getContents();
        
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public byte[] getUploadedImageContents() {
        if (uploadedImageContents != null) {
            return uploadedImageContents;
        } else if (hopital != null && hopital.getImage() != null) {
            hopital = hopitalService.lazilyLoadImageToHopital(hopital);
            return hopital.getImage().getContent();
        }
        return null;
    }
    
    public HopitalEntity getHopital() {
        if (this.hopital == null) {
            prepareNewHopital();
        }
        return this.hopital;
    }
    
    public void setHopital(HopitalEntity hopital) {
        this.hopital = hopital;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(HopitalEntity hopital, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
