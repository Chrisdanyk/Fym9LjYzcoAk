package org.shekinah.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
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
import org.primefaces.model.UploadedFile;
import org.shekinah.domain.InfosEntity;
import org.shekinah.domain.InfosGenre;
import org.shekinah.domain.InfosImage;
import org.shekinah.domain.InfosStatutMarital;
import org.shekinah.service.InfosService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("infosBean")
@ViewScoped
public class InfosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(InfosBean.class.getName());
    
    private GenericLazyDataModel<InfosEntity> lazyModel;
    
    private InfosEntity infos;
    
    @Inject
    private InfosService infosService;
    
    UploadedFile uploadedImage;
    byte[] uploadedImageContents;
    
    public void prepareNewInfos() {
        reset();
        this.infos = new InfosEntity();
        // set any default values now, if you need
        // Example: this.infos.setAnything("test");
    }

    public GenericLazyDataModel<InfosEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(infosService);
        }
        return this.lazyModel;
    }
    
    public String persist() {

        if (infos.getId() == null && !isPermitted("infos:create")) {
            return "accessDenied";
        } else if (infos.getId() != null && !isPermitted(infos, "infos:update")) {
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
            
                    InfosImage infosImage = new InfosImage();
                    infosImage.setContent(baos.toByteArray());
                    infos.setImage(infosImage);
                } catch (Exception e) {
                    FacesMessage facesMessage = MessageFactory.getMessage(
                            "message_upload_exception");
                    FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                    FacesContext.getCurrentInstance().validationFailed();
                    return null;
                }
            }
            
            if (infos.getId() != null) {
                infos = infosService.update(infos);
                message = "message_successfully_updated";
            } else {
                infos = infosService.save(infos);
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
        
        if (!isPermitted(infos, "infos:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            infosService.delete(infos);
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
    
    public void onDialogOpen(InfosEntity infos) {
        reset();
        this.infos = infos;
    }
    
    public void reset() {
        infos = null;

        uploadedImage = null;
        uploadedImageContents = null;
        
    }

    public SelectItem[] getGenreSelectItems() {
        SelectItem[] items = new SelectItem[InfosGenre.values().length];

        int i = 0;
        for (InfosGenre genre : InfosGenre.values()) {
            items[i++] = new SelectItem(genre, getLabelForGenre(genre));
        }
        return items;
    }
    
    public String getLabelForGenre(InfosGenre value) {
        if (value == null) {
            return "";
        }
        String label = MessageFactory.getMessageString(
                "enum_label_infos_genre_" + value);
        return label == null? value.toString() : label;
    }
    
    public SelectItem[] getStatutMaritalSelectItems() {
        SelectItem[] items = new SelectItem[InfosStatutMarital.values().length];

        int i = 0;
        for (InfosStatutMarital statutMarital : InfosStatutMarital.values()) {
            items[i++] = new SelectItem(statutMarital, getLabelForStatutMarital(statutMarital));
        }
        return items;
    }
    
    public String getLabelForStatutMarital(InfosStatutMarital value) {
        if (value == null) {
            return "";
        }
        String label = MessageFactory.getMessageString(
                "enum_label_infos_statutMarital_" + value);
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
        } else if (infos != null && infos.getImage() != null) {
            infos = infosService.lazilyLoadImageToInfos(infos);
            return infos.getImage().getContent();
        }
        return null;
    }
    
    public InfosEntity getInfos() {
        if (this.infos == null) {
            prepareNewInfos();
        }
        return this.infos;
    }
    
    public void setInfos(InfosEntity infos) {
        this.infos = infos;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(InfosEntity infos, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
