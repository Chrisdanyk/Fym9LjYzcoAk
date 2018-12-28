package org.shekinah.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.MimetypesFileTypeMap;
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

import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.shekinah.domain.AbonnementEntity;
import org.shekinah.domain.ConsultationEntity;
import org.shekinah.domain.HopitalEntity;
import org.shekinah.domain.HospitalisationEntity;
import org.shekinah.domain.LaboEntity;
import org.shekinah.domain.PatientAttachment;
import org.shekinah.domain.PatientEntity;
import org.shekinah.domain.PatientGenre;
import org.shekinah.domain.PatientGroupeSanguin;
import org.shekinah.domain.PatientImage;
import org.shekinah.domain.PatientStatutMarital;
import org.shekinah.domain.RendezvousEntity;
import org.shekinah.service.AbonnementService;
import org.shekinah.service.ConsultationService;
import org.shekinah.service.HopitalService;
import org.shekinah.service.HospitalisationService;
import org.shekinah.service.LaboService;
import org.shekinah.service.PatientAttachmentService;
import org.shekinah.service.PatientService;
import org.shekinah.service.RendezvousService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.web.util.MessageFactory;

@Named("patientBean")
@ViewScoped
public class PatientBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(PatientBean.class.getName());
    
    private List<PatientEntity> patientList;

    private PatientEntity patient;
    
    private List<PatientAttachment> patientAttachments;
    
    @Inject
    private PatientService patientService;
    
    @Inject
    private PatientAttachmentService patientAttachmentService;
    
    UploadedFile uploadedImage;
    byte[] uploadedImageContents;
    
    @Inject
    private HopitalService hopitalService;
    
    @Inject
    private RendezvousService rendezvousService;
    
    @Inject
    private ConsultationService consultationService;
    
    @Inject
    private LaboService laboService;
    
    @Inject
    private AbonnementService abonnementService;
    
    @Inject
    private HospitalisationService hospitalisationService;
    
    private DualListModel<RendezvousEntity> rendezvouss;
    private List<String> transferedRendezvousIDs;
    private List<String> removedRendezvousIDs;
    
    private DualListModel<ConsultationEntity> consultations;
    private List<String> transferedConsultationIDs;
    private List<String> removedConsultationIDs;
    
    private DualListModel<LaboEntity> labos;
    private List<String> transferedLaboIDs;
    private List<String> removedLaboIDs;
    
    private DualListModel<AbonnementEntity> abonnements;
    private List<String> transferedAbonnementIDs;
    private List<String> removedAbonnementIDs;
    
    private DualListModel<HospitalisationEntity> hospitalisations;
    private List<String> transferedHospitalisationIDs;
    private List<String> removedHospitalisationIDs;
    
    private List<HopitalEntity> allHopitalsList;
    
    public void prepareNewPatient() {
        reset();
        this.patient = new PatientEntity();
        // set any default values now, if you need
        // Example: this.patient.setAnything("test");
    }

    public String persist() {

        if (patient.getId() == null && !isPermitted("patient:create")) {
            return "accessDenied";
        } else if (patient.getId() != null && !isPermitted(patient, "patient:update")) {
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
            
                    PatientImage patientImage = new PatientImage();
                    patientImage.setContent(baos.toByteArray());
                    patient.setImage(patientImage);
                } catch (Exception e) {
                    FacesMessage facesMessage = MessageFactory.getMessage(
                            "message_upload_exception");
                    FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                    FacesContext.getCurrentInstance().validationFailed();
                    return null;
                }
            }
            
            if (patient.getId() != null) {
                patient = patientService.update(patient);
                message = "message_successfully_updated";
            } else {
                patient = patientService.save(patient);
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
        
        patientList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(patient, "patient:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            patientService.delete(patient);
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
    
    public void onDialogOpen(PatientEntity patient) {
        reset();
        this.patient = patient;
    }
    
    public void reset() {
        patient = null;
        patientList = null;
        
        patientAttachments = null;
        
        rendezvouss = null;
        transferedRendezvousIDs = null;
        removedRendezvousIDs = null;
        
        consultations = null;
        transferedConsultationIDs = null;
        removedConsultationIDs = null;
        
        labos = null;
        transferedLaboIDs = null;
        removedLaboIDs = null;
        
        abonnements = null;
        transferedAbonnementIDs = null;
        removedAbonnementIDs = null;
        
        hospitalisations = null;
        transferedHospitalisationIDs = null;
        removedHospitalisationIDs = null;
        
        allHopitalsList = null;
        
        uploadedImage = null;
        uploadedImageContents = null;
        
    }

    // Get a List of all hopital
    public List<HopitalEntity> getHopitals() {
        if (this.allHopitalsList == null) {
            this.allHopitalsList = hopitalService.findAllHopitalEntities();
        }
        return this.allHopitalsList;
    }
    
    // Update hopital of the current patient
    public void updateHopital(HopitalEntity hopital) {
        this.patient.setHopital(hopital);
        // Maybe we just created and assigned a new hopital. So reset the allHopitalList.
        allHopitalsList = null;
    }
    
    public DualListModel<RendezvousEntity> getRendezvouss() {
        return rendezvouss;
    }

    public void setRendezvouss(DualListModel<RendezvousEntity> rendezvouss) {
        this.rendezvouss = rendezvouss;
    }
    
    public List<RendezvousEntity> getFullRendezvoussList() {
        List<RendezvousEntity> allList = new ArrayList<>();
        allList.addAll(rendezvouss.getSource());
        allList.addAll(rendezvouss.getTarget());
        return allList;
    }
    
    public void onRendezvoussDialog(PatientEntity patient) {
        // Prepare the rendezvous PickList
        this.patient = patient;
        List<RendezvousEntity> selectedRendezvoussFromDB = rendezvousService
                .findRendezvoussByPatient(this.patient);
        List<RendezvousEntity> availableRendezvoussFromDB = rendezvousService
                .findAvailableRendezvouss(this.patient);
        this.rendezvouss = new DualListModel<>(availableRendezvoussFromDB, selectedRendezvoussFromDB);
        
        transferedRendezvousIDs = new ArrayList<>();
        removedRendezvousIDs = new ArrayList<>();
    }
    
    public void onRendezvoussPickListTransfer(TransferEvent event) {
        // If a rendezvous is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((RendezvousEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedRendezvousIDs.add(id);
                removedRendezvousIDs.remove(id);
            } else if (event.isRemove()) {
                removedRendezvousIDs.add(id);
                transferedRendezvousIDs.remove(id);
            }
        }
        
    }
    
    public void updateRendezvous(RendezvousEntity rendezvous) {
        // If a new rendezvous is created, we persist it to the database,
        // but we do not assign it to this patient in the database, yet.
        rendezvouss.getTarget().add(rendezvous);
        transferedRendezvousIDs.add(rendezvous.getId().toString());
    }
    
    public DualListModel<ConsultationEntity> getConsultations() {
        return consultations;
    }

    public void setConsultations(DualListModel<ConsultationEntity> consultations) {
        this.consultations = consultations;
    }
    
    public List<ConsultationEntity> getFullConsultationsList() {
        List<ConsultationEntity> allList = new ArrayList<>();
        allList.addAll(consultations.getSource());
        allList.addAll(consultations.getTarget());
        return allList;
    }
    
    public void onConsultationsDialog(PatientEntity patient) {
        // Prepare the consultation PickList
        this.patient = patient;
        List<ConsultationEntity> selectedConsultationsFromDB = consultationService
                .findConsultationsByPatient(this.patient);
        List<ConsultationEntity> availableConsultationsFromDB = consultationService
                .findAvailableConsultations(this.patient);
        this.consultations = new DualListModel<>(availableConsultationsFromDB, selectedConsultationsFromDB);
        
        transferedConsultationIDs = new ArrayList<>();
        removedConsultationIDs = new ArrayList<>();
    }
    
    public void onConsultationsPickListTransfer(TransferEvent event) {
        // If a consultation is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((ConsultationEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedConsultationIDs.add(id);
                removedConsultationIDs.remove(id);
            } else if (event.isRemove()) {
                removedConsultationIDs.add(id);
                transferedConsultationIDs.remove(id);
            }
        }
        
    }
    
    public void updateConsultation(ConsultationEntity consultation) {
        // If a new consultation is created, we persist it to the database,
        // but we do not assign it to this patient in the database, yet.
        consultations.getTarget().add(consultation);
        transferedConsultationIDs.add(consultation.getId().toString());
    }
    
    public DualListModel<LaboEntity> getLabos() {
        return labos;
    }

    public void setLabos(DualListModel<LaboEntity> labos) {
        this.labos = labos;
    }
    
    public List<LaboEntity> getFullLabosList() {
        List<LaboEntity> allList = new ArrayList<>();
        allList.addAll(labos.getSource());
        allList.addAll(labos.getTarget());
        return allList;
    }
    
    public void onLabosDialog(PatientEntity patient) {
        // Prepare the labo PickList
        this.patient = patient;
        List<LaboEntity> selectedLabosFromDB = laboService
                .findLabosByPatient(this.patient);
        List<LaboEntity> availableLabosFromDB = laboService
                .findAvailableLabos(this.patient);
        this.labos = new DualListModel<>(availableLabosFromDB, selectedLabosFromDB);
        
        transferedLaboIDs = new ArrayList<>();
        removedLaboIDs = new ArrayList<>();
    }
    
    public void onLabosPickListTransfer(TransferEvent event) {
        // If a labo is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((LaboEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedLaboIDs.add(id);
                removedLaboIDs.remove(id);
            } else if (event.isRemove()) {
                removedLaboIDs.add(id);
                transferedLaboIDs.remove(id);
            }
        }
        
    }
    
    public void updateLabo(LaboEntity labo) {
        // If a new labo is created, we persist it to the database,
        // but we do not assign it to this patient in the database, yet.
        labos.getTarget().add(labo);
        transferedLaboIDs.add(labo.getId().toString());
    }
    
    public DualListModel<AbonnementEntity> getAbonnements() {
        return abonnements;
    }

    public void setAbonnements(DualListModel<AbonnementEntity> abonnements) {
        this.abonnements = abonnements;
    }
    
    public List<AbonnementEntity> getFullAbonnementsList() {
        List<AbonnementEntity> allList = new ArrayList<>();
        allList.addAll(abonnements.getSource());
        allList.addAll(abonnements.getTarget());
        return allList;
    }
    
    public void onAbonnementsDialog(PatientEntity patient) {
        // Prepare the abonnement PickList
        this.patient = patient;
        List<AbonnementEntity> selectedAbonnementsFromDB = abonnementService
                .findAbonnementsByPatient(this.patient);
        List<AbonnementEntity> availableAbonnementsFromDB = abonnementService
                .findAvailableAbonnements(this.patient);
        this.abonnements = new DualListModel<>(availableAbonnementsFromDB, selectedAbonnementsFromDB);
        
        transferedAbonnementIDs = new ArrayList<>();
        removedAbonnementIDs = new ArrayList<>();
    }
    
    public void onAbonnementsPickListTransfer(TransferEvent event) {
        // If a abonnement is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((AbonnementEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedAbonnementIDs.add(id);
                removedAbonnementIDs.remove(id);
            } else if (event.isRemove()) {
                removedAbonnementIDs.add(id);
                transferedAbonnementIDs.remove(id);
            }
        }
        
    }
    
    public void updateAbonnement(AbonnementEntity abonnement) {
        // If a new abonnement is created, we persist it to the database,
        // but we do not assign it to this patient in the database, yet.
        abonnements.getTarget().add(abonnement);
        transferedAbonnementIDs.add(abonnement.getId().toString());
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
    
    public void onHospitalisationsDialog(PatientEntity patient) {
        // Prepare the hospitalisation PickList
        this.patient = patient;
        List<HospitalisationEntity> selectedHospitalisationsFromDB = hospitalisationService
                .findHospitalisationsByPatient(this.patient);
        List<HospitalisationEntity> availableHospitalisationsFromDB = hospitalisationService
                .findAvailableHospitalisations(this.patient);
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
        // but we do not assign it to this patient in the database, yet.
        hospitalisations.getTarget().add(hospitalisation);
        transferedHospitalisationIDs.add(hospitalisation.getId().toString());
    }
    
    public void onRendezvoussSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<RendezvousEntity> selectedRendezvoussFromDB = rendezvousService
                    .findRendezvoussByPatient(this.patient);
            List<RendezvousEntity> availableRendezvoussFromDB = rendezvousService
                    .findAvailableRendezvouss(this.patient);
            
            for (RendezvousEntity rendezvous : selectedRendezvoussFromDB) {
                if (removedRendezvousIDs.contains(rendezvous.getId().toString())) {
                    rendezvous.setPatient(null);
                    rendezvousService.update(rendezvous);
                }
            }
    
            for (RendezvousEntity rendezvous : availableRendezvoussFromDB) {
                if (transferedRendezvousIDs.contains(rendezvous.getId().toString())) {
                    rendezvous.setPatient(patient);
                    rendezvousService.update(rendezvous);
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
    
    public void onConsultationsSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<ConsultationEntity> selectedConsultationsFromDB = consultationService
                    .findConsultationsByPatient(this.patient);
            List<ConsultationEntity> availableConsultationsFromDB = consultationService
                    .findAvailableConsultations(this.patient);
            
            for (ConsultationEntity consultation : selectedConsultationsFromDB) {
                if (removedConsultationIDs.contains(consultation.getId().toString())) {
                    consultation.setPatient(null);
                    consultationService.update(consultation);
                }
            }
    
            for (ConsultationEntity consultation : availableConsultationsFromDB) {
                if (transferedConsultationIDs.contains(consultation.getId().toString())) {
                    consultation.setPatient(patient);
                    consultationService.update(consultation);
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
    
    public void onLabosSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<LaboEntity> selectedLabosFromDB = laboService
                    .findLabosByPatient(this.patient);
            List<LaboEntity> availableLabosFromDB = laboService
                    .findAvailableLabos(this.patient);
            
            for (LaboEntity labo : selectedLabosFromDB) {
                if (removedLaboIDs.contains(labo.getId().toString())) {
                    labo.setPatient(null);
                    laboService.update(labo);
                }
            }
    
            for (LaboEntity labo : availableLabosFromDB) {
                if (transferedLaboIDs.contains(labo.getId().toString())) {
                    labo.setPatient(patient);
                    laboService.update(labo);
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
    
    public void onAbonnementsSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<AbonnementEntity> selectedAbonnementsFromDB = abonnementService
                    .findAbonnementsByPatient(this.patient);
            List<AbonnementEntity> availableAbonnementsFromDB = abonnementService
                    .findAvailableAbonnements(this.patient);
            
            for (AbonnementEntity abonnement : selectedAbonnementsFromDB) {
                if (removedAbonnementIDs.contains(abonnement.getId().toString())) {
                    abonnement.setPatient(null);
                    abonnementService.update(abonnement);
                }
            }
    
            for (AbonnementEntity abonnement : availableAbonnementsFromDB) {
                if (transferedAbonnementIDs.contains(abonnement.getId().toString())) {
                    abonnement.setPatient(patient);
                    abonnementService.update(abonnement);
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
    
    public void onHospitalisationsSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<HospitalisationEntity> selectedHospitalisationsFromDB = hospitalisationService
                    .findHospitalisationsByPatient(this.patient);
            List<HospitalisationEntity> availableHospitalisationsFromDB = hospitalisationService
                    .findAvailableHospitalisations(this.patient);
            
            for (HospitalisationEntity hospitalisation : selectedHospitalisationsFromDB) {
                if (removedHospitalisationIDs.contains(hospitalisation.getId().toString())) {
                    hospitalisation.setPatient(null);
                    hospitalisationService.update(hospitalisation);
                }
            }
    
            for (HospitalisationEntity hospitalisation : availableHospitalisationsFromDB) {
                if (transferedHospitalisationIDs.contains(hospitalisation.getId().toString())) {
                    hospitalisation.setPatient(patient);
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
    
    public SelectItem[] getGenreSelectItems() {
        SelectItem[] items = new SelectItem[PatientGenre.values().length];

        int i = 0;
        for (PatientGenre genre : PatientGenre.values()) {
            items[i++] = new SelectItem(genre, getLabelForGenre(genre));
        }
        return items;
    }
    
    public String getLabelForGenre(PatientGenre value) {
        if (value == null) {
            return "";
        }
        String label = MessageFactory.getMessageString(
                "enum_label_patient_genre_" + value);
        return label == null? value.toString() : label;
    }
    
    public SelectItem[] getStatutMaritalSelectItems() {
        SelectItem[] items = new SelectItem[PatientStatutMarital.values().length];

        int i = 0;
        for (PatientStatutMarital statutMarital : PatientStatutMarital.values()) {
            items[i++] = new SelectItem(statutMarital, getLabelForStatutMarital(statutMarital));
        }
        return items;
    }
    
    public String getLabelForStatutMarital(PatientStatutMarital value) {
        if (value == null) {
            return "";
        }
        String label = MessageFactory.getMessageString(
                "enum_label_patient_statutMarital_" + value);
        return label == null? value.toString() : label;
    }
    
    public SelectItem[] getGroupeSanguinSelectItems() {
        SelectItem[] items = new SelectItem[PatientGroupeSanguin.values().length];

        int i = 0;
        for (PatientGroupeSanguin groupeSanguin : PatientGroupeSanguin.values()) {
            items[i++] = new SelectItem(groupeSanguin, getLabelForGroupeSanguin(groupeSanguin));
        }
        return items;
    }
    
    public String getLabelForGroupeSanguin(PatientGroupeSanguin value) {
        if (value == null) {
            return "";
        }
        String label = MessageFactory.getMessageString(
                "enum_label_patient_groupeSanguin_" + value);
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
        } else if (patient != null && patient.getImage() != null) {
            patient = patientService.lazilyLoadImageToPatient(patient);
            return patient.getImage().getContent();
        }
        return null;
    }
    
    public List<PatientAttachment> getPatientAttachments() {
        if (this.patientAttachments == null && this.patient != null && this.patient.getId() != null) {
            // The byte streams are not loaded from database with following line. This would cost too much.
            this.patientAttachments = this.patientAttachmentService.getAttachmentsList(patient);
        }
        return this.patientAttachments;
    }
    
    public void handleAttachmentUpload(FileUploadEvent event) {
        
        PatientAttachment patientAttachment = new PatientAttachment();
        
        try {
            // Would be better to use ...getFile().getContents(), but does not work on every environment
            patientAttachment.setContent(IOUtils.toByteArray(event.getFile().getInputstream()));
        
            patientAttachment.setFileName(event.getFile().getFileName());
            patientAttachment.setPatient(patient);
            patientAttachmentService.save(patientAttachment);
            
            // set patientAttachment to null, will be refreshed on next demand
            this.patientAttachments = null;
            
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_successfully_uploaded");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occured", e);
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_upload_exception");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        }
    }

    public StreamedContent getAttachmentStreamedContent(
            PatientAttachment patientAttachment) {
        if (patientAttachment != null && patientAttachment.getContent() == null) {
            patientAttachment = patientAttachmentService
                    .find(patientAttachment.getId());
        }
        return new DefaultStreamedContent(new ByteArrayInputStream(
                patientAttachment.getContent()),
                new MimetypesFileTypeMap().getContentType(patientAttachment
                        .getFileName()), patientAttachment.getFileName());
    }

    public String deleteAttachment(PatientAttachment attachment) {
        
        patientAttachmentService.delete(attachment);
        
        // set patientAttachment to null, will be refreshed on next demand
        this.patientAttachments = null;
        
        FacesMessage facesMessage = MessageFactory.getMessage(
                "message_successfully_deleted", "Attachment");
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        return null;
    }
    
    public PatientEntity getPatient() {
        if (this.patient == null) {
            prepareNewPatient();
        }
        return this.patient;
    }
    
    public void setPatient(PatientEntity patient) {
        this.patient = patient;
    }
    
    public List<PatientEntity> getPatientList() {
        if (patientList == null) {
            patientList = patientService.findAllPatientEntities();
        }
        return patientList;
    }

    public void setPatientList(List<PatientEntity> patientList) {
        this.patientList = patientList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(PatientEntity patient, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
