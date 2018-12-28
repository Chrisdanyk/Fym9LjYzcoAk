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

import org.shekinah.domain.ConsultationEntity;
import org.shekinah.domain.ExamenEntity;
import org.shekinah.domain.LaboEntity;
import org.shekinah.service.ConsultationService;
import org.shekinah.service.ExamenService;
import org.shekinah.service.LaboService;

@Path("/examens")
@Named
public class ExamenResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private ExamenService examenService;
    
    @Inject
    private ConsultationService consultationService;
    
    @Inject
    private LaboService laboService;
    
    /**
     * Get the complete list of Examen Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /examens
     * @return List of ExamenEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ExamenEntity> getAllExamens() {
        return examenService.findAllExamenEntities();
    }
    
    /**
     * Get the number of Examen Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /examens/count
     * @return Number of ExamenEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return examenService.countAllEntries();
    }
    
    /**
     * Get a Examen Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /examens/3
     * @param id
     * @return A Examen Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ExamenEntity getExamenById(@PathParam("id") Long id) {
        return examenService.find(id);
    }
    
    /**
     * Create a Examen Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New ExamenEntity (JSON) <br/>
     * Example URL: /examens
     * @param examen
     * @return A ExamenEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ExamenEntity addExamen(ExamenEntity examen) {
        return examenService.save(examen);
    }
    
    /**
     * Update an existing Examen Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated ExamenEntity (JSON) <br/>
     * Example URL: /examens
     * @param examen
     * @return A ExamenEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ExamenEntity updateExamen(ExamenEntity examen) {
        return examenService.update(examen);
    }
    
    /**
     * Delete an existing Examen Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /examens/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deleteExamen(@PathParam("id") Long id) {
        ExamenEntity examen = examenService.find(id);
        examenService.delete(examen);
    }
    
    /**
     * Get the list of Consultation that is assigned to a Examen <br/>
     * HTTP Method: GET <br/>
     * Example URL: /examens/3/consultations
     * @param examenId
     * @return List of ConsultationEntity
     */
    @GET
    @Path("{id}/consultations")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ConsultationEntity> getConsultations(@PathParam("id") Long examenId) {
        ExamenEntity examen = examenService.find(examenId);
        return consultationService.findConsultationsByExamens(examen);
    }
    
    /**
     * Assign an existing Consultation to an existing Examen <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /examens/3/consultations/8
     * @param examenId
     * @param consultationId
     */
    @PUT
    @Path("{id}/consultations/{consultationId}")
    public void assignConsultation(@PathParam("id") Long examenId, @PathParam("consultationId") Long consultationId) {
        ExamenEntity examen = examenService.find(examenId);
        ConsultationEntity consultation = consultationService.find(consultationId);
        consultation.setExamens(examen);
        consultationService.update(consultation);
    }
    
    /**
     * Remove a Examen-to-Consultation Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /examens/3/consultations/8
     * @param examenId
     * @param consultationId
     */
    @DELETE
    @Path("{id}/consultations/{consultationId}")
    public void unassignConsultation(@PathParam("id") Long examenId, @PathParam("consultationId") Long consultationId) {
        ConsultationEntity consultation = consultationService.find(consultationId);
        consultation.setExamens(null);
        consultationService.update(consultation);
    }
    
    /**
     * Get the list of Labo that is assigned to a Examen <br/>
     * HTTP Method: GET <br/>
     * Example URL: /examens/3/labos
     * @param examenId
     * @return List of LaboEntity
     */
    @GET
    @Path("{id}/labos")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LaboEntity> getLabos(@PathParam("id") Long examenId) {
        ExamenEntity examen = examenService.find(examenId);
        return laboService.findLabosByExamen(examen);
    }
    
    /**
     * Assign an existing Labo to an existing Examen <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /examens/3/labos/8
     * @param examenId
     * @param laboId
     */
    @PUT
    @Path("{id}/labos/{laboId}")
    public void assignLabo(@PathParam("id") Long examenId, @PathParam("laboId") Long laboId) {
        ExamenEntity examen = examenService.find(examenId);
        LaboEntity labo = laboService.find(laboId);
        labo.setExamen(examen);
        laboService.update(labo);
    }
    
    /**
     * Remove a Examen-to-Labo Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /examens/3/labos/8
     * @param examenId
     * @param laboId
     */
    @DELETE
    @Path("{id}/labos/{laboId}")
    public void unassignLabo(@PathParam("id") Long examenId, @PathParam("laboId") Long laboId) {
        LaboEntity labo = laboService.find(laboId);
        labo.setExamen(null);
        laboService.update(labo);
    }
    
}
