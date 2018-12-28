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
import javax.faces.model.SelectItem;
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
import org.shekinah.domain.HospitalisationEntity;
import org.shekinah.domain.PavillonEntity;
import org.shekinah.domain.SalleCategorie;
import org.shekinah.domain.SalleEntity;
import org.shekinah.domain.SalleImage;
import org.shekinah.service.HospitalisationService;
import org.shekinah.service.PavillonService;
import org.shekinah.service.SalleService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("salleBean")
@ViewScoped
public class SalleBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(SalleBean.class.getName());
    
    private GenericLazyDataModel<SalleEntity> lazyModel;
    
    private SalleEntity salle;
    
    @Inject
    private SalleService salleService;
    
    UploadedFile uploadedImage;
    byte[] uploadedImageContents;
    
    @Inject
    private PavillonService pavillonService;
    
    @Inject
    private HospitalisationService hospitalisationService;
    
    private DualListModel<HospitalisationEntity> hospitalisations;
    private List<String> transferedHospitalisationIDs;
    private List<String> removedHospitalisationIDs;
    
    private List<PavillonEntity> allPavillonsList;
    
    public void prepareNewSalle() {
        reset();
        this.salle = new SalleEntity();
        // set any default values now, if you need
        // Example: this.salle.setAnything("test");
    }

    public GenericLazyDataModel<SalleEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(salleService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (salle.getId() == null && !isPermitted("salle:create")) {
            return "accessDenied";
        } else if (salle.getId() != null && !isPermitted(salle, "salle:update")) {
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
            
                    SalleImage salleImage = new SalleImage();
                    salleImage.setContent(baos.toByteArray());
                    salle.setImage(salleImage);
                } catch (Exception e) {
                    FacesMessage facesMessage = MessageFactory.getMessage(
                            "message_upload_exception");
                    FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                    FacesContext.getCurrentInstance().validationFailed();
                    return null;
                }
            }
            
            if (salle.getId() != null) {
                salle = salleService.update(salle);
                message = "message_successfully_updated";
            } else {
                salle = salleService.save(salle);
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
        
        if (!isPermitted(salle, "salle:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            salleService.delete(salle);
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
    
    public void onDialogOpen(SalleEntity salle) {
        reset();
        this.salle = salle;
    }
    
    public void reset() {
        salle = null;

        hospitalisations = null;
        transferedHospitalisationIDs = null;
        removedHospitalisationIDs = null;
        
        allPavillonsList = null;
        
        uploadedImage = null;
        uploadedImageContents = null;
        
    }

    // Get a List of all pavillon
    public List<PavillonEntity> getPavillons() {
        if (this.allPavillonsList == null) {
            this.allPavillonsList = pavillonService.findAllPavillonEntities();
        }
        return this.allPavillonsList;
    }
    
    // Update pavillon of the current salle
    public void updatePavillon(PavillonEntity pavillon) {
        this.salle.setPavillon(pavillon);
        // Maybe we just created and assigned a new pavillon. So reset the allPavillonList.
        allPavillonsList = null;
    }
    
    public DualListModel<HospitalisationEntity> getHospitalisations() {
        return hospitalisations;
    }

    public void setHospitalisations(DualListModel<HospitalisationEntity> hospitalisations) {
        this.hospitalisations = hospitalisations;
    }
    
    public List<HospitalisationEntity> getFullHospitalisationsList() {
        List<HospitalisationEntity> allList = new ArrayList<>();
        allList.addAll(hospitalisations.getSource());
        allList.addAll(hospitalisations.getTarget());
        return allList;
    }
    
    public void onHospitalisationsDialog(SalleEntity salle) {
        // Prepare the hospitalisation PickList
        this.salle = salle;
        List<HospitalisationEntity> selectedHospitalisationsFromDB = hospitalisationService
                .findHospitalisationsBySalle(this.salle);
        List<HospitalisationEntity> availableHospitalisationsFromDB = hospitalisationService
                .findAvailableHospitalisations(this.salle);
        this.hospitalisations = new DualListModel<>(availableHospitalisationsFromDB, selectedHospitalisationsFromDB);
        
        transferedHospitalisationIDs = new ArrayList<>();
        removedHospitalisationIDs = new ArrayList<>();
    }
    
    public void onHospitalisationsPickListTransfer(TransferEvent event) {
        // If a hospitalisation is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((HospitalisationEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedHospitalisationIDs.add(id);
                removedHospitalisationIDs.remove(id);
            } else if (event.isRemove()) {
                removedHospitalisationIDs.add(id);
                transferedHospitalisationIDs.remove(id);
            }
        }
        
    }
    
    public void updateHospitalisation(HospitalisationEntity hospitalisation) {
        // If a new hospitalisation is created, we persist it to the database,
        // but we do not assign it to this salle in the database, yet.
        hospitalisations.getTarget().add(hospitalisation);
        transferedHospitalisationIDs.add(hospitalisation.getId().toString());
    }
    
    public void onHospitalisationsSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<HospitalisationEntity> selectedHospitalisationsFromDB = hospitalisationService
                    .findHospitalisationsBySalle(this.salle);
            List<HospitalisationEntity> availableHospitalisationsFromDB = hospitalisationService
                    .findAvailableHospitalisations(this.salle);
            
            for (HospitalisationEntity hospitalisation : selectedHospitalisationsFromDB) {
                if (removedHospitalisationIDs.contains(hospitalisation.getId().toString())) {
                    hospitalisation.setSalle(null);
                    hospitalisationService.update(hospitalisation);
                }
            }
    
            for (HospitalisationEntity hospitalisation : availableHospitalisationsFromDB) {
                if (transferedHospitalisationIDs.contains(hospitalisation.getId().toString())) {
                    hospitalisation.setSalle(salle);
                    hospitalisationService.update(hospitalisation);
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
    
    public SelectItem[] getCategorieSelectItems() {
        SelectItem[] items = new SelectItem[SalleCategorie.values().length];

        int i = 0;
        for (SalleCategorie categorie : SalleCategorie.values()) {
            items[i++] = new SelectItem(categorie, getLabelForCategorie(categorie));
        }
        return items;
    }
    
    public String getLabelForCategorie(SalleCategorie value) {
        if (value == null) {
            return "";
        }
        String label = MessageFactory.getMessageString(
                "enum_label_salle_categorie_" + value);
        return label == null? value.toString() : label;
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
        } else if (salle != null && salle.getImage() != null) {
            salle = salleService.lazilyLoadImageToSalle(salle);
            return salle.getImage().getContent();
        }
        return null;
    }
    
    public SalleEntity getSalle() {
        if (this.salle == null) {
            prepareNewSalle();
        }
        return this.salle;
    }
    
    public void setSalle(SalleEntity salle) {
        this.salle = salle;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(SalleEntity salle, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
