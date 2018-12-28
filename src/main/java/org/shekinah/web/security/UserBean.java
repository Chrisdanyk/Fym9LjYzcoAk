package org.shekinah.web.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;
import org.shekinah.domain.ConsultationEntity;
import org.shekinah.domain.CreneauEntity;
import org.shekinah.domain.HopitalEntity;
import org.shekinah.domain.InfosEntity;
import org.shekinah.domain.LaboEntity;
import org.shekinah.domain.ServiceEntity;
import org.shekinah.domain.VenteEntity;
import org.shekinah.domain.security.UserEntity;
import org.shekinah.domain.security.UserRole;
import org.shekinah.domain.security.UserStatus;
import org.shekinah.service.ConsultationService;
import org.shekinah.service.CreneauService;
import org.shekinah.service.HopitalService;
import org.shekinah.service.InfosService;
import org.shekinah.service.LaboService;
import org.shekinah.service.ServiceService;
import org.shekinah.service.VenteService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.service.security.UserService;
import org.shekinah.web.generic.GenericLazyDataModel;
import org.shekinah.web.util.MessageFactory;

@Named("userBean")
@ViewScoped
public class UserBean implements Serializable {
    
    private static final Logger logger = Logger.getLogger(UserBean.class.getName());
    
    private LazyDataModel<UserEntity> lazyModel;

    private static final long serialVersionUID = 1L;

    private UserEntity user;

    @Inject
    private UserService userService;
    
    @Inject
    private InfosService infosService;
    
    @Inject
    private HopitalService hopitalService;
    
    @Inject
    private ServiceService serviceService;
    
    @Inject
    private VenteService venteService;
    
    @Inject
    private CreneauService creneauService;
    
    @Inject
    private ConsultationService consultationService;
    
    @Inject
    private LaboService laboService;
    
    private DualListModel<VenteEntity> ventes;
    private List<String> transferedVenteIDs;
    private List<String> removedVenteIDs;
    
    private DualListModel<CreneauEntity> creneaus;
    private List<String> transferedCreneauIDs;
    private List<String> removedCreneauIDs;
    
    private DualListModel<ConsultationEntity> consultations;
    private List<String> transferedConsultationIDs;
    private List<String> removedConsultationIDs;
    
    private DualListModel<ConsultationEntity> consultation2s;
    private List<String> transferedConsultation2IDs;
    private List<String> removedConsultation2IDs;
    
    private DualListModel<ConsultationEntity> consultation3s;
    private List<String> transferedConsultation3IDs;
    private List<String> removedConsultation3IDs;
    
    private DualListModel<LaboEntity> labos;
    private List<String> transferedLaboIDs;
    private List<String> removedLaboIDs;
    
    private List<HopitalEntity> allHopitalsList;
    
    private List<ServiceEntity> allServicesList;
    
    private List<InfosEntity> availableInfosList;
    
    public UserEntity getUser() {
        if (user == null) {
            user = new UserEntity();
        }
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void prepareNewUser() {
        this.user = new UserEntity();
        // set any default values now, if you need
    }
    
    public LazyDataModel<UserEntity> getLazyModel() {
        if (this.lazyModel == null) {
            this.lazyModel = new GenericLazyDataModel<>(userService);
        }
        return this.lazyModel;
    }
    
    public void persist() {

        String message;
        
        try {
            
            if (user.getId() != null) {
                user = userService.update(user);
                message = "message_successfully_updated";
            } else {
                
                // Check if a user with same username already exists
                if (userService.findUserByUsername(user.getUsername()) != null) {
                    FacesMessage facesMessage = MessageFactory.getMessage(
                            "user_username_exists");
                    facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
                    FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                    FacesContext.getCurrentInstance().validationFailed();
                    return;
                }
                // Check if a user with same email already exists            
                if (userService.findUserByEmail(user.getEmail()) != null) {
                    FacesMessage facesMessage = MessageFactory.getMessage(
                            "user_email_exists");
                    facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
                    FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                    FacesContext.getCurrentInstance().validationFailed();
                    return;
                }
                
                user = userService.save(user);
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
        
        FacesMessage facesMessage = MessageFactory.getMessage(message,
                "User");
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    public String delete() {
        userService.delete(user);
        FacesMessage facesMessage = MessageFactory.getMessage(
                "message_successfully_deleted", "User");
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        reset();
        return null;
    }
    
    public void reset() {
        user = null;
        
        ventes = null;
        transferedVenteIDs = null;
        removedVenteIDs = null;
        
        creneaus = null;
        transferedCreneauIDs = null;
        removedCreneauIDs = null;
        
        consultations = null;
        transferedConsultationIDs = null;
        removedConsultationIDs = null;
        
        consultation2s = null;
        transferedConsultation2IDs = null;
        removedConsultation2IDs = null;
        
        consultation3s = null;
        transferedConsultation3IDs = null;
        removedConsultation3IDs = null;
        
        labos = null;
        transferedLaboIDs = null;
        removedLaboIDs = null;
        
        allHopitalsList = null;
        
        allServicesList = null;
        
        availableInfosList = null;
        
    }
    
    public SelectItem[] getRolesSelectItems() {
        SelectItem[] items = new SelectItem[UserRole.values().length];

        int i = 0;
        for (UserRole role : UserRole.values()) {
            String label = MessageFactory.getMessageString(
                    "enumeration_label_user_roles_" + role.toString());
            items[i++] = new SelectItem(role, label == null? role.toString() : label);
        }
        return items;
    }
    
    public SelectItem[] getStatusSelectItems() {
        SelectItem[] items = new SelectItem[UserStatus.values().length];

        int i = 0;
        for (UserStatus status : UserStatus.values()) {
            items[i++] = new SelectItem(status, status.toString());
        }
        return items;
    }
    
    // Get a List of all hopital
    public List<HopitalEntity> getHopitals() {
        if (this.allHopitalsList == null) {
            this.allHopitalsList = hopitalService.findAllHopitalEntities();
        }
        return this.allHopitalsList;
    }
    
    // Update hopital of the current user
    public void updateHopital(HopitalEntity hopital) {
        this.user.setHopital(hopital);
        // Maybe we just created and assigned a new hopital. So reset the allHopitalList.
        allHopitalsList = null;
    }
    
    // Get a List of all service
    public List<ServiceEntity> getServices() {
        if (this.allServicesList == null) {
            this.allServicesList = serviceService.findAllServiceEntities();
        }
        return this.allServicesList;
    }
    
    // Update service of the current user
    public void updateService(ServiceEntity service) {
        this.user.setService(service);
        // Maybe we just created and assigned a new service. So reset the allServiceList.
        allServicesList = null;
    }
    
    // Get a List of all infos
    public List<InfosEntity> getAvailableInfos() {
        if (this.availableInfosList == null) {
            this.availableInfosList = infosService.findAvailableInfos(this.user);
        }
        return this.availableInfosList;
    }
    
    // Update infos of the current user
    public void updateInfos(InfosEntity infos) {
        this.user.setInfos(infos);
        // Maybe we just created and assigned a new infos. So reset the availableInfosList.
        availableInfosList = null;
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
    
    public void onVentesDialog(UserEntity user) {
        // Prepare the vente PickList
        this.user = user;
        List<VenteEntity> selectedVentesFromDB = venteService
                .findVentesByPharmacien(this.user);
        List<VenteEntity> availableVentesFromDB = venteService
                .findAvailableVentes(this.user);
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
        // but we do not assign it to this user in the database, yet.
        ventes.getTarget().add(vente);
        transferedVenteIDs.add(vente.getId().toString());
    }
    
    public DualListModel<CreneauEntity> getCreneaus() {
        return creneaus;
    }

    public void setCreneaus(DualListModel<CreneauEntity> creneaus) {
        this.creneaus = creneaus;
    }
    
    public List<CreneauEntity> getFullCreneausList() {
        List<CreneauEntity> allList = new ArrayList<>();
        allList.addAll(creneaus.getSource());
        allList.addAll(creneaus.getTarget());
        return allList;
    }
    
    public void onCreneausDialog(UserEntity user) {
        // Prepare the creneau PickList
        this.user = user;
        List<CreneauEntity> selectedCreneausFromDB = creneauService
                .findCreneausByMedecin(this.user);
        List<CreneauEntity> availableCreneausFromDB = creneauService
                .findAvailableCreneaus(this.user);
        this.creneaus = new DualListModel<>(availableCreneausFromDB, selectedCreneausFromDB);
        
        transferedCreneauIDs = new ArrayList<>();
        removedCreneauIDs = new ArrayList<>();
    }
    
    public void onCreneausPickListTransfer(TransferEvent event) {
        // If a creneau is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((CreneauEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedCreneauIDs.add(id);
                removedCreneauIDs.remove(id);
            } else if (event.isRemove()) {
                removedCreneauIDs.add(id);
                transferedCreneauIDs.remove(id);
            }
        }
        
    }
    
    public void updateCreneau(CreneauEntity creneau) {
        // If a new creneau is created, we persist it to the database,
        // but we do not assign it to this user in the database, yet.
        creneaus.getTarget().add(creneau);
        transferedCreneauIDs.add(creneau.getId().toString());
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
    
    public void onConsultationsDialog(UserEntity user) {
        // Prepare the consultation PickList
        this.user = user;
        List<ConsultationEntity> selectedConsultationsFromDB = consultationService
                .findConsultationsByMedecin(this.user);
        List<ConsultationEntity> availableConsultationsFromDB = consultationService
                .findAvailableConsultations(this.user);
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
        // but we do not assign it to this user in the database, yet.
        consultations.getTarget().add(consultation);
        transferedConsultationIDs.add(consultation.getId().toString());
    }
    
    public DualListModel<ConsultationEntity> getConsultation2s() {
        return consultation2s;
    }

    public void setConsultation2s(DualListModel<ConsultationEntity> consultations) {
        this.consultation2s = consultations;
    }
    
    public List<ConsultationEntity> getFullConsultation2sList() {
        List<ConsultationEntity> allList = new ArrayList<>();
        allList.addAll(consultation2s.getSource());
        allList.addAll(consultation2s.getTarget());
        return allList;
    }
    
    public void onConsultation2sDialog(UserEntity user) {
        // Prepare the consultation2 PickList
        this.user = user;
        List<ConsultationEntity> selectedConsultationsFromDB = consultationService
                .findConsultation2sByInfirmier(this.user);
        List<ConsultationEntity> availableConsultationsFromDB = consultationService
                .findAvailableConsultation2s(this.user);
        this.consultation2s = new DualListModel<>(availableConsultationsFromDB, selectedConsultationsFromDB);
        
        transferedConsultation2IDs = new ArrayList<>();
        removedConsultation2IDs = new ArrayList<>();
    }
    
    public void onConsultation2sPickListTransfer(TransferEvent event) {
        // If a consultation2 is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((ConsultationEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedConsultation2IDs.add(id);
                removedConsultation2IDs.remove(id);
            } else if (event.isRemove()) {
                removedConsultation2IDs.add(id);
                transferedConsultation2IDs.remove(id);
            }
        }
        
    }
    
    public void updateConsultation2(ConsultationEntity consultation) {
        // If a new consultation2 is created, we persist it to the database,
        // but we do not assign it to this user in the database, yet.
        consultation2s.getTarget().add(consultation);
        transferedConsultation2IDs.add(consultation.getId().toString());
    }
    
    public DualListModel<ConsultationEntity> getConsultation3s() {
        return consultation3s;
    }

    public void setConsultation3s(DualListModel<ConsultationEntity> consultations) {
        this.consultation3s = consultations;
    }
    
    public List<ConsultationEntity> getFullConsultation3sList() {
        List<ConsultationEntity> allList = new ArrayList<>();
        allList.addAll(consultation3s.getSource());
        allList.addAll(consultation3s.getTarget());
        return allList;
    }
    
    public void onConsultation3sDialog(UserEntity user) {
        // Prepare the consultation3 PickList
        this.user = user;
        List<ConsultationEntity> selectedConsultationsFromDB = consultationService
                .findConsultation3sByLaborantin(this.user);
        List<ConsultationEntity> availableConsultationsFromDB = consultationService
                .findAvailableConsultation3s(this.user);
        this.consultation3s = new DualListModel<>(availableConsultationsFromDB, selectedConsultationsFromDB);
        
        transferedConsultation3IDs = new ArrayList<>();
        removedConsultation3IDs = new ArrayList<>();
    }
    
    public void onConsultation3sPickListTransfer(TransferEvent event) {
        // If a consultation3 is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((ConsultationEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedConsultation3IDs.add(id);
                removedConsultation3IDs.remove(id);
            } else if (event.isRemove()) {
                removedConsultation3IDs.add(id);
                transferedConsultation3IDs.remove(id);
            }
        }
        
    }
    
    public void updateConsultation3(ConsultationEntity consultation) {
        // If a new consultation3 is created, we persist it to the database,
        // but we do not assign it to this user in the database, yet.
        consultation3s.getTarget().add(consultation);
        transferedConsultation3IDs.add(consultation.getId().toString());
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
    
    public void onLabosDialog(UserEntity user) {
        // Prepare the labo PickList
        this.user = user;
        List<LaboEntity> selectedLabosFromDB = laboService
                .findLabosByLaborantin(this.user);
        List<LaboEntity> availableLabosFromDB = laboService
                .findAvailableLabos(this.user);
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
        // but we do not assign it to this user in the database, yet.
        labos.getTarget().add(labo);
        transferedLaboIDs.add(labo.getId().toString());
    }
    
    public void onVentesSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<VenteEntity> selectedVentesFromDB = venteService
                    .findVentesByPharmacien(this.user);
            List<VenteEntity> availableVentesFromDB = venteService
                    .findAvailableVentes(this.user);
            
            for (VenteEntity vente : selectedVentesFromDB) {
                if (removedVenteIDs.contains(vente.getId().toString())) {
                    vente.setPharmacien(null);
                    venteService.update(vente);
                }
            }
    
            for (VenteEntity vente : availableVentesFromDB) {
                if (transferedVenteIDs.contains(vente.getId().toString())) {
                    vente.setPharmacien(user);
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
    
    public void onCreneausSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<CreneauEntity> selectedCreneausFromDB = creneauService
                    .findCreneausByMedecin(this.user);
            List<CreneauEntity> availableCreneausFromDB = creneauService
                    .findAvailableCreneaus(this.user);
            
            for (CreneauEntity creneau : selectedCreneausFromDB) {
                if (removedCreneauIDs.contains(creneau.getId().toString())) {
                    creneau.setMedecin(null);
                    creneauService.update(creneau);
                }
            }
    
            for (CreneauEntity creneau : availableCreneausFromDB) {
                if (transferedCreneauIDs.contains(creneau.getId().toString())) {
                    creneau.setMedecin(user);
                    creneauService.update(creneau);
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
                    .findConsultationsByMedecin(this.user);
            List<ConsultationEntity> availableConsultationsFromDB = consultationService
                    .findAvailableConsultations(this.user);
            
            for (ConsultationEntity consultation : selectedConsultationsFromDB) {
                if (removedConsultationIDs.contains(consultation.getId().toString())) {
                    consultation.setMedecin(null);
                    consultationService.update(consultation);
                }
            }
    
            for (ConsultationEntity consultation : availableConsultationsFromDB) {
                if (transferedConsultationIDs.contains(consultation.getId().toString())) {
                    consultation.setMedecin(user);
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
    
    public void onConsultation2sSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<ConsultationEntity> selectedConsultationsFromDB = consultationService
                    .findConsultation2sByInfirmier(this.user);
            List<ConsultationEntity> availableConsultationsFromDB = consultationService
                    .findAvailableConsultation2s(this.user);
            
            for (ConsultationEntity consultation : selectedConsultationsFromDB) {
                if (removedConsultation2IDs.contains(consultation.getId().toString())) {
                    consultation.setInfirmier(null);
                    consultationService.update(consultation);
                }
            }
    
            for (ConsultationEntity consultation : availableConsultationsFromDB) {
                if (transferedConsultation2IDs.contains(consultation.getId().toString())) {
                    consultation.setInfirmier(user);
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
    
    public void onConsultation3sSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            List<ConsultationEntity> selectedConsultationsFromDB = consultationService
                    .findConsultation3sByLaborantin(this.user);
            List<ConsultationEntity> availableConsultationsFromDB = consultationService
                    .findAvailableConsultation3s(this.user);
            
            for (ConsultationEntity consultation : selectedConsultationsFromDB) {
                if (removedConsultation3IDs.contains(consultation.getId().toString())) {
                    consultation.setLaborantin(null);
                    consultationService.update(consultation);
                }
            }
    
            for (ConsultationEntity consultation : availableConsultationsFromDB) {
                if (transferedConsultation3IDs.contains(consultation.getId().toString())) {
                    consultation.setLaborantin(user);
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
                    .findLabosByLaborantin(this.user);
            List<LaboEntity> availableLabosFromDB = laboService
                    .findAvailableLabos(this.user);
            
            for (LaboEntity labo : selectedLabosFromDB) {
                if (removedLaboIDs.contains(labo.getId().toString())) {
                    labo.setLaborantin(null);
                    laboService.update(labo);
                }
            }
    
            for (LaboEntity labo : availableLabosFromDB) {
                if (transferedLaboIDs.contains(labo.getId().toString())) {
                    labo.setLaborantin(user);
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
    
    public void loadCurrentUser() {
        String username = SecurityWrapper.getUsername();
        if (username != null) {
            this.user = this.userService.findUserByUsername(username);
        } else {
            throw new RuntimeException("Can not get authorized user name");
        }
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }
}
