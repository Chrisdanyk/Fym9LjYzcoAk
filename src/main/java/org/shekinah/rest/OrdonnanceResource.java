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

import org.shekinah.domain.OrdonnanceEntity;
import org.shekinah.service.OrdonnanceService;

@Path("/ordonnances")
@Named
public class OrdonnanceResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private OrdonnanceService ordonnanceService;
    
    /**
     * Get the complete list of Ordonnance Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /ordonnances
     * @return List of OrdonnanceEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<OrdonnanceEntity> getAllOrdonnances() {
        return ordonnanceService.findAllOrdonnanceEntities();
    }
    
    /**
     * Get the number of Ordonnance Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /ordonnances/count
     * @return Number of OrdonnanceEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return ordonnanceService.countAllEntries();
    }
    
    /**
     * Get a Ordonnance Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /ordonnances/3
     * @param id
     * @return A Ordonnance Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public OrdonnanceEntity getOrdonnanceById(@PathParam("id") Long id) {
        return ordonnanceService.find(id);
    }
    
    /**
     * Create a Ordonnance Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New OrdonnanceEntity (JSON) <br/>
     * Example URL: /ordonnances
     * @param ordonnance
     * @return A OrdonnanceEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OrdonnanceEntity addOrdonnance(OrdonnanceEntity ordonnance) {
        return ordonnanceService.save(ordonnance);
    }
    
    /**
     * Update an existing Ordonnance Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated OrdonnanceEntity (JSON) <br/>
     * Example URL: /ordonnances
     * @param ordonnance
     * @return A OrdonnanceEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OrdonnanceEntity updateOrdonnance(OrdonnanceEntity ordonnance) {
        return ordonnanceService.update(ordonnance);
    }
    
    /**
     * Delete an existing Ordonnance Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /ordonnances/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deleteOrdonnance(@PathParam("id") Long id) {
        OrdonnanceEntity ordonnance = ordonnanceService.find(id);
        ordonnanceService.delete(ordonnance);
    }
    
}
