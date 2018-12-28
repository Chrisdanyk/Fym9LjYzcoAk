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
import org.shekinah.service.ConsultationService;

@Path("/consultations")
@Named
public class ConsultationResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private ConsultationService consultationService;
    
    /**
     * Get the complete list of Consultation Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /consultations
     * @return List of ConsultationEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ConsultationEntity> getAllConsultations() {
        return consultationService.findAllConsultationEntities();
    }
    
    /**
     * Get the number of Consultation Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /consultations/count
     * @return Number of ConsultationEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return consultationService.countAllEntries();
    }
    
    /**
     * Get a Consultation Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /consultations/3
     * @param id
     * @return A Consultation Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ConsultationEntity getConsultationById(@PathParam("id") Long id) {
        return consultationService.find(id);
    }
    
    /**
     * Create a Consultation Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New ConsultationEntity (JSON) <br/>
     * Example URL: /consultations
     * @param consultation
     * @return A ConsultationEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConsultationEntity addConsultation(ConsultationEntity consultation) {
        return consultationService.save(consultation);
    }
    
    /**
     * Update an existing Consultation Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated ConsultationEntity (JSON) <br/>
     * Example URL: /consultations
     * @param consultation
     * @return A ConsultationEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConsultationEntity updateConsultation(ConsultationEntity consultation) {
        return consultationService.update(consultation);
    }
    
    /**
     * Delete an existing Consultation Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /consultations/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deleteConsultation(@PathParam("id") Long id) {
        ConsultationEntity consultation = consultationService.find(id);
        consultationService.delete(consultation);
    }
    
}
