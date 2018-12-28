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
import org.shekinah.domain.FamilleEntity;
import org.shekinah.domain.FournisseurEntity;
import org.shekinah.domain.MedicamentEntity;
import org.shekinah.domain.MedicamentImage;
import org.shekinah.domain.MedicamentType;
import org.shekinah.domain.VenteEntity;
import org.shekinah.service.FamilleService;
import org.shekinah.service.FournisseurService;
import org.shekinah.service.MedicamentService;
import org.shekinah.service.VenteService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("medicamentBean")
@ViewScoped
public class MedicamentBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(MedicamentBean.class.getName());
    
    private GenericLazyDataModel<MedicamentEntity> lazyModel;
    
    private MedicamentEntity medicament;
    
    @Inject
    private MedicamentService medicamentService;
    
    UploadedFile uploadedImage;
    byte[] uploadedImageContents;
    
    @Inject
    private FournisseurService fournisseurService;
    
    @Inject
    private FamilleService familleService;
    
    @Inject
    private VenteService venteService;
    
    private DualListModel<VenteEntity> ventes;
    private List<String> transferedVenteIDs;
    private List<String> removedVenteIDs;
    
    private List<FournisseurEntity> allFournisseurssList;
    
    private List<FamilleEntity> allFamillesList;
    
    public void prepareNewMedicament() {
        reset();
        this.medicament = new MedicamentEntity();
        // set any default values now, if you need
        // Example: this.medicament.setAnything("test");
    }

    public GenericLazyDataModel<MedicamentEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(medicamentService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (medicament.getId() == null && !isPermitted("medicament:create")) {
            return "accessDenied";
        } else if (medicament.getId() != null && !isPermitted(medicament, "medicament:update")) {
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
            
                    MedicamentImage medicamentImage = new MedicamentImage();
                    medicamentImage.setContent(baos.toByteArray());
                    medicament.setImage(medicamentImage);
                } catch (Exception e) {
                    FacesMessage facesMessage = MessageFactory.getMessage(
                            "message_upload_exception");
                    FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                    FacesContext.getCurrentInstance().validationFailed();
                    return null;
                }
            }
            
            if (medicament.getId() != null) {
                medicament = medicamentService.update(medicament);
                message = "message_successfully_updated";
            } else {
                medicament = medicamentService.save(medicament);
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
        
        if (!isPermitted(medicament, "medicament:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            medicamentService.delete(medicament);
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
    
    public void onDialogOpen(MedicamentEntity medicament) {
        reset();
        this.medicament = medicament;
    }
    
    public void reset() {
        medicament = null;

        ventes = null;
        transferedVenteIDs = null;
        removedVenteIDs = null;
        
        allFournisseurssList = null;
        
        allFamillesList = null;
        
        uploadedImage = null;
        uploadedImageContents = null;
        
    }

    // Get a List of all fournisseurs
    public List<FournisseurEntity> getFournisseurss() {
        if (this.allFournisseurssList == null) {
            this.allFournisseurssList = fournisseurService.findAllFournisseurEntities();
        }
        return this.allFournisseurssList;
    }
    
    // Update fournisseurs of the current medicament
    public void updateFournisseurs(FournisseurEntity fournisseur) {
        this.medicament.setFournisseurs(fournisseur);
        // Maybe we just created and assigned a new fournisseur. So reset the allFournisseursList.
        allFournisseurssList = null;
    }
    
    // Get a List of all famille
    public List<FamilleEntity> getFamilles() {
        if (this.allFamillesList == null) {
            this.allFamillesList = familleService.findAllFamilleEntities();
        }
        return this.allFamillesList;
    }
    
    // Update famille of the current medicament
    public void updateFamille(FamilleEntity famille) {
        this.medicament.setFamille(famille);
        // Maybe we just created and assigned a new famille. So reset the allFamilleList.
        allFamillesList = null;
    }
    
    public DualListModel<VenteEntity> getVentes() {
        return ventes;
    }

    public void setVentes(DualListModel<VenteEntity> ventes) {
        this.ventes = ventes;
    }
    
    public List<VenteEntity> getFullVentesList() {
        List<VenteEntity> allList = new ArrayList<>();
        allList.addAll(ventes.getSource());
        allList.addAll(ventes.getTarget());
        return allList;
    }
    
    public void onVentesDialog(MedicamentEntity medicament) {
        // Prepare the vente PickList
        this.medicament = medicament;
        List<VenteEntity> selectedVentesFromDB = venteService
                .findVentesByMedicament(this.medicament);
        List<VenteEntity> availableVentesFromDB = venteService
                .findAvailableVentes(this.medicament);
        this.ventes = new DualListModel<>(availableVentesFromDB, selectedVentesFromDB);
        
        transferedVenteIDs = new ArrayList<>();
        removedVenteIDs = new ArrayList<>();
    }
    
    public void onVentesPickListTransfer(TransferEvent event) {
        // If a vente is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((VenteEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedVenteIDs.add(id);
                removedVenteIDs.remove(id);
            } else if (event.isRemove()) {
                removedVenteIDs.add(id);
                transferedVenteIDs.remove(id);
            }
        }
        
    }
    
    public void updateVente(VenteEntity vente) {
        // If a new vente is created, we persist it to the database,
        // but we do not assign it to this medicament in the database, yet.
        ventes.getTarget().add(vente);
        transferedVenteIDs.add(vente.getId().toString());
    }
    
    public void onVentesSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<VenteEntity> selectedVentesFromDB = venteService
                    .findVentesByMedicament(this.medicament);
            List<VenteEntity> availableVentesFromDB = venteService
                    .findAvailableVentes(this.medicament);
            
            for (VenteEntity vente : selectedVentesFromDB) {
                if (removedVenteIDs.contains(vente.getId().toString())) {
                    vente.setMedicament(null);
                    venteService.update(vente);
                }
            }
    
            for (VenteEntity vente : availableVentesFromDB) {
                if (transferedVenteIDs.contains(vente.getId().toString())) {
                    vente.setMedicament(medicament);
                    venteService.update(vente);
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
    
    public SelectItem[] getTypeSelectItems() {
        SelectItem[] items = new SelectItem[MedicamentType.values().length];

        int i = 0;
        for (MedicamentType type : MedicamentType.values()) {
            items[i++] = new SelectItem(type, getLabelForType(type));
        }
        return items;
    }
    
    public String getLabelForType(MedicamentType value) {
        if (value == null) {
            return "";
        }
        String label = MessageFactory.getMessageString(
                "enum_label_medicament_type_" + value);
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
        } else if (medicament != null && medicament.getImage() != null) {
            medicament = medicamentService.lazilyLoadImageToMedicament(medicament);
            return medicament.getImage().getContent();
        }
        return null;
    }
    
    public MedicamentEntity getMedicament() {
        if (this.medicament == null) {
            prepareNewMedicament();
        }
        return this.medicament;
    }
    
    public void setMedicament(MedicamentEntity medicament) {
        this.medicament = medicament;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(MedicamentEntity medicament, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
