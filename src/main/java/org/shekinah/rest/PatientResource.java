package org.shekinah.rest;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.shekinah.domain.AbonnementEntity;
import org.shekinah.domain.ConsultationEntity;
import org.shekinah.domain.HospitalisationEntity;
import org.shekinah.domain.LaboEntity;
import org.shekinah.domain.PatientEntity;
import org.shekinah.domain.RendezvousEntity;
import org.shekinah.service.AbonnementService;
import org.shekinah.service.ConsultationService;
import org.shekinah.service.HospitalisationService;
import org.shekinah.service.LaboService;
import org.shekinah.service.PatientService;
import org.shekinah.service.RendezvousService;

@Path("/patients")
@Named
public class PatientResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private PatientService patientService;
    
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
    
    /**
     * Get the complete list of Patient Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /patients
     * @return List of PatientEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PatientEntity> getAllPatients() {
        return patientService.findAllPatientEntities();
    }
    
    /**
     * Get the number of Patient Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /patients/count
     * @return Number of PatientEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return patientService.countAllEntries();
    }
    
    /**
     * Get a Patient Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /patients/3
     * @param id
     * @return A Patient Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PatientEntity getPatientById(@PathParam("id") Long id) {
        return patientService.find(id);
    }
    
    /**
     * Create a Patient Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New PatientEntity (JSON) <br/>
     * Example URL: /patients
     * @param patient
     * @return A PatientEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PatientEntity addPatient(PatientEntity patient) {
        return patientService.save(patient);
    }
    
    /**
     * Update an existing Patient Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated PatientEntity (JSON) <br/>
     * Example URL: /patients
     * @param patient
     * @return A PatientEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PatientEntity updatePatient(PatientEntity patient) {
        return patientService.update(patient);
    }
    
    /**
     * Delete an existing Patient Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /patients/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deletePatient(@PathParam("id") Long id) {
        PatientEntity patient = patientService.find(id);
        patientService.delete(patient);
    }
    
    /**
     * Get the list of Rendezvous that is assigned to a Patient <br/>
     * HTTP Method: GET <br/>
     * Example URL: /patients/3/rendezvouss
     * @param patientId
     * @return List of RendezvousEntity
     */
    @GET
    @Path("{id}/rendezvouss")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RendezvousEntity> getRendezvouss(@PathParam("id") Long patientId) {
        PatientEntity patient = patientService.find(patientId);
        return rendezvousService.findRendezvoussByPatient(patient);
    }
    
    /**
     * Assign an existing Rendezvous to an existing Patient <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /patients/3/rendezvouss/8
     * @param patientId
     * @param rendezvousId
     */
    @PUT
    @Path("{id}/rendezvouss/{rendezvousId}")
    public void assignRendezvous(@PathParam("id") Long patientId, @PathParam("rendezvousId") Long rendezvousId) {
        PatientEntity patient = patientService.find(patientId);
        RendezvousEntity rendezvous = rendezvousService.find(rendezvousId);
        rendezvous.setPatient(patient);
        rendezvousService.update(rendezvous);
    }
    
    /**
     * Remove a Patient-to-Rendezvous Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /patients/3/rendezvouss/8
     * @param patientId
     * @param rendezvousId
     */
    @DELETE
    @Path("{id}/rendezvouss/{rendezvousId}")
    public void unassignRendezvous(@PathParam("id") Long patientId, @PathParam("rendezvousId") Long rendezvousId) {
        RendezvousEntity rendezvous = rendezvousService.find(rendezvousId);
        rendezvous.setPatient(null);
        rendezvousService.update(rendezvous);
    }
    
    /**
     * Get the list of Consultation that is assigned to a Patient <br/>
     * HTTP Method: GET <br/>
     * Example URL: /patients/3/consultations
     * @param patientId
     * @return List of ConsultationEntity
     */
    @GET
    @Path("{id}/consultations")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ConsultationEntity> getConsultations(@PathParam("id") Long patientId) {
        PatientEntity patient = patientService.find(patientId);
        return consultationService.findConsultationsByPatient(patient);
    }
    
    /**
     * Assign an existing Consultation to an existing Patient <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /patients/3/consultations/8
     * @param patientId
     * @param consultationId
     */
    @PUT
    @Path("{id}/consultations/{consultationId}")
    public void assignConsultation(@PathParam("id") Long patientId, @PathParam("consultationId") Long consultationId) {
        PatientEntity patient = patientService.find(patientId);
        ConsultationEntity consultation = consultationService.find(consultationId);
        consultation.setPatient(patient);
        consultationService.update(consultation);
    }
    
    /**
     * Remove a Patient-to-Consultation Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /patients/3/consultations/8
     * @param patientId
     * @param consultationId
     */
    @DELETE
    @Path("{id}/consultations/{consultationId}")
    public void unassignConsultation(@PathParam("id") Long patientId, @PathParam("consultationId") Long consultationId) {
        ConsultationEntity consultation = consultationService.find(consultationId);
        consultation.setPatient(null);
        consultationService.update(consultation);
    }
    
    /**
     * Get the list of Labo that is assigned to a Patient <br/>
     * HTTP Method: GET <br/>
     * Example URL: /patients/3/labos
     * @param patientId
     * @return List of LaboEntity
     */
    @GET
    @Path("{id}/labos")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LaboEntity> getLabos(@PathParam("id") Long patientId) {
        PatientEntity patient = patientService.find(patientId);
        return laboService.findLabosByPatient(patient);
    }
    
    /**
     * Assign an existing Labo to an existing Patient <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /patients/3/labos/8
     * @param patientId
     * @param laboId
     */
    @PUT
    @Path("{id}/labos/{laboId}")
    public void assignLabo(@PathParam("id") Long patientId, @PathParam("laboId") Long laboId) {
        PatientEntity patient = patientService.find(patientId);
        LaboEntity labo = laboService.find(laboId);
        labo.setPatient(patient);
        laboService.update(labo);
    }
    
    /**
     * Remove a Patient-to-Labo Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /patients/3/labos/8
     * @param patientId
     * @param laboId
     */
    @DELETE
    @Path("{id}/labos/{laboId}")
    public void unassignLabo(@PathParam("id") Long patientId, @PathParam("laboId") Long laboId) {
        LaboEntity labo = laboService.find(laboId);
        labo.setPatient(null);
        laboService.update(labo);
    }
    
    /**
     * Get the list of Abonnement that is assigned to a Patient <br/>
     * HTTP Method: GET <br/>
     * Example URL: /patients/3/abonnements
     * @param patientId
     * @return List of AbonnementEntity
     */
    @GET
    @Path("{id}/abonnements")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AbonnementEntity> getAbonnements(@PathParam("id") Long patientId) {
        PatientEntity patient = patientService.find(patientId);
        return abonnementService.findAbonnementsByPatient(patient);
    }
    
    /**
     * Assign an existing Abonnement to an existing Patient <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /patients/3/abonnements/8
     * @param patientId
     * @param abonnementId
     */
    @PUT
    @Path("{id}/abonnements/{abonnementId}")
    public void assignAbonnement(@PathParam("id") Long patientId, @PathParam("abonnementId") Long abonnementId) {
        PatientEntity patient = patientService.find(patientId);
        AbonnementEntity abonnement = abonnementService.find(abonnementId);
        abonnement.setPatient(patient);
        abonnementService.update(abonnement);
    }
    
    /**
     * Remove a Patient-to-Abonnement Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /patients/3/abonnements/8
     * @param patientId
     * @param abonnementId
     */
    @DELETE
    @Path("{id}/abonnements/{abonnementId}")
    public void unassignAbonnement(@PathParam("id") Long patientId, @PathParam("abonnementId") Long abonnementId) {
        AbonnementEntity abonnement = abonnementService.find(abonnementId);
        abonnement.setPatient(null);
        abonnementService.update(abonnement);
    }
    
    /**
     * Get the list of Hospitalisation that is assigned to a Patient <br/>
     * HTTP Method: GET <br/>
     * Example URL: /patients/3/hospitalisations
     * @param patientId
     * @return List of HospitalisationEntity
     */
    @GET
    @Path("{id}/hospitalisations")
    @Produces(MediaType.APPLICATION_JSON)
    public List<HospitalisationEntity> getHospitalisations(@PathParam("id") Long patientId) {
        PatientEntity patient = patientService.find(patientId);
        return hospitalisationService.findHospitalisationsByPatient(patient);
    }
    
    /**
     * Assign an existing Hospitalisation to an existing Patient <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /patients/3/hospitalisations/8
     * @param patientId
     * @param hospitalisationId
     */
    @PUT
    @Path("{id}/hospitalisations/{hospitalisationId}")
    public void assignHospitalisation(@PathParam("id") Long patientId, @PathParam("hospitalisationId") Long hospitalisationId) {
        PatientEntity patient = patientService.find(patientId);
        HospitalisationEntity hospitalisation = hospitalisationService.find(hospitalisationId);
        hospitalisation.setPatient(patient);
        hospitalisationService.update(hospitalisation);
    }
    
    /**
     * Remove a Patient-to-Hospitalisation Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /patients/3/hospitalisations/8
     * @param patientId
     * @param hospitalisationId
     */
    @DELETE
    @Path("{id}/hospitalisations/{hospitalisationId}")
    public void unassignHospitalisation(@PathParam("id") Long patientId, @PathParam("hospitalisationId") Long hospitalisationId) {
        HospitalisationEntity hospitalisation = hospitalisationService.find(hospitalisationId);
        hospitalisation.setPatient(null);
        hospitalisationService.update(hospitalisation);
    }
    
}
