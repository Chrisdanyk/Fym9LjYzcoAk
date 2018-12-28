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

import org.shekinah.domain.HospitalisationEntity;
import org.shekinah.service.HospitalisationService;

@Path("/hospitalisations")
@Named
public class HospitalisationResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private HospitalisationService hospitalisationService;
    
    /**
     * Get the complete list of Hospitalisation Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /hospitalisations
     * @return List of HospitalisationEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<HospitalisationEntity> getAllHospitalisations() {
        return hospitalisationService.findAllHospitalisationEntities();
    }
    
    /**
     * Get the number of Hospitalisation Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /hospitalisations/count
     * @return Number of HospitalisationEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return hospitalisationService.countAllEntries();
    }
    
    /**
     * Get a Hospitalisation Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /hospitalisations/3
     * @param id
     * @return A Hospitalisation Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HospitalisationEntity getHospitalisationById(@PathParam("id") Long id) {
        return hospitalisationService.find(id);
    }
    
    /**
     * Create a Hospitalisation Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New HospitalisationEntity (JSON) <br/>
     * Example URL: /hospitalisations
     * @param hospitalisation
     * @return A HospitalisationEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HospitalisationEntity addHospitalisation(HospitalisationEntity hospitalisation) {
        return hospitalisationService.save(hospitalisation);
    }
    
    /**
     * Update an existing Hospitalisation Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated HospitalisationEntity (JSON) <br/>
     * Example URL: /hospitalisations
     * @param hospitalisation
     * @return A HospitalisationEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HospitalisationEntity updateHospitalisation(HospitalisationEntity hospitalisation) {
        return hospitalisationService.update(hospitalisation);
    }
    
    /**
     * Delete an existing Hospitalisation Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /hospitalisations/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deleteHospitalisation(@PathParam("id") Long id) {
        HospitalisationEntity hospitalisation = hospitalisationService.find(id);
        hospitalisationService.delete(hospitalisation);
    }
    
}
