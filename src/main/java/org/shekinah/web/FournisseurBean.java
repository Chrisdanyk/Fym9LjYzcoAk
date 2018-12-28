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
import org.shekinah.domain.FournisseurEntity;
import org.shekinah.domain.FournisseurImage;
import org.shekinah.domain.MedicamentEntity;
import org.shekinah.service.FournisseurService;
import org.shekinah.service.MedicamentService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("fournisseurBean")
@ViewScoped
public class FournisseurBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(FournisseurBean.class.getName());
    
    private GenericLazyDataModel<FournisseurEntity> lazyModel;
    
    private FournisseurEntity fournisseur;
    
    @Inject
    private FournisseurService fournisseurService;
    
    UploadedFile uploadedImage;
    byte[] uploadedImageContents;
    
    @Inject
    private MedicamentService medicamentService;
    
    private DualListModel<MedicamentEntity> medicaments;
    private List<String> transferedMedicamentIDs;
    private List<String> removedMedicamentIDs;
    
    public void prepareNewFournisseur() {
        reset();
        this.fournisseur = new FournisseurEntity();
        // set any default values now, if you need
        // Example: this.fournisseur.setAnything("test");
    }

    public GenericLazyDataModel<FournisseurEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(fournisseurService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (fournisseur.getId() == null && !isPermitted("fournisseur:create")) {
            return "accessDenied";
        } else if (fournisseur.getId() != null && !isPermitted(fournisseur, "fournisseur:update")) {
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
            
                    FournisseurImage fournisseurImage = new FournisseurImage();
                    fournisseurImage.setContent(baos.toByteArray());
                    fournisseur.setImage(fournisseurImage);
                } catch (Exception e) {
                    FacesMessage facesMessage = MessageFactory.getMessage(
                            "message_upload_exception");
                    FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                    FacesContext.getCurrentInstance().validationFailed();
                    return null;
                }
            }
            
            if (fournisseur.getId() != null) {
                fournisseur = fournisseurService.update(fournisseur);
                message = "message_successfully_updated";
            } else {
                fournisseur = fournisseurService.save(fournisseur);
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
        
        if (!isPermitted(fournisseur, "fournisseur:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            fournisseurService.delete(fournisseur);
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
    
    public void onDialogOpen(FournisseurEntity fournisseur) {
        reset();
        this.fournisseur = fournisseur;
    }
    
    public void reset() {
        fournisseur = null;

        medicaments = null;
        transferedMedicamentIDs = null;
        removedMedicamentIDs = null;
        
        uploadedImage = null;
        uploadedImageContents = null;
        
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
    
    public void onMedicamentsDialog(FournisseurEntity fournisseur) {
        // Prepare the medicament PickList
        this.fournisseur = fournisseur;
        List<MedicamentEntity> selectedMedicamentsFromDB = medicamentService
                .findMedicamentsByFournisseurs(this.fournisseur);
        List<MedicamentEntity> availableMedicamentsFromDB = medicamentService
                .findAvailableMedicaments(this.fournisseur);
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
        // but we do not assign it to this fournisseur in the database, yet.
        medicaments.getTarget().add(medicament);
        transferedMedicamentIDs.add(medicament.getId().toString());
    }
    
    public void onMedicamentsSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<MedicamentEntity> selectedMedicamentsFromDB = medicamentService
                    .findMedicamentsByFournisseurs(this.fournisseur);
            List<MedicamentEntity> availableMedicamentsFromDB = medicamentService
                    .findAvailableMedicaments(this.fournisseur);
            
            for (MedicamentEntity medicament : selectedMedicamentsFromDB) {
                if (removedMedicamentIDs.contains(medicament.getId().toString())) {
                    medicament.setFournisseurs(null);
                    medicamentService.update(medicament);
                }
            }
    
            for (MedicamentEntity medicament : availableMedicamentsFromDB) {
                if (transferedMedicamentIDs.contains(medicament.getId().toString())) {
                    medicament.setFournisseurs(fournisseur);
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
        } else if (fournisseur != null && fournisseur.getImage() != null) {
            fournisseur = fournisseurService.lazilyLoadImageToFournisseur(fournisseur);
            return fournisseur.getImage().getContent();
        }
        return null;
    }
    
    public FournisseurEntity getFournisseur() {
        if (this.fournisseur == null) {
            prepareNewFournisseur();
        }
        return this.fournisseur;
    }
    
    public void setFournisseur(FournisseurEntity fournisseur) {
        this.fournisseur = fournisseur;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(FournisseurEntity fournisseur, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
