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
import org.shekinah.service.AbonnementService;

@Path("/abonnements")
@Named
public class AbonnementResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private AbonnementService abonnementService;
    
    /**
     * Get the complete list of Abonnement Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /abonnements
     * @return List of AbonnementEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<AbonnementEntity> getAllAbonnements() {
        return abonnementService.findAllAbonnementEntities();
    }
    
    /**
     * Get the number of Abonnement Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /abonnements/count
     * @return Number of AbonnementEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return abonnementService.countAllEntries();
    }
    
    /**
     * Get a Abonnement Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /abonnements/3
     * @param id
     * @return A Abonnement Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AbonnementEntity getAbonnementById(@PathParam("id") Long id) {
        return abonnementService.find(id);
    }
    
    /**
     * Create a Abonnement Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New AbonnementEntity (JSON) <br/>
     * Example URL: /abonnements
     * @param abonnement
     * @return A AbonnementEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AbonnementEntity addAbonnement(AbonnementEntity abonnement) {
        return abonnementService.save(abonnement);
    }
    
    /**
     * Update an existing Abonnement Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated AbonnementEntity (JSON) <br/>
     * Example URL: /abonnements
     * @param abonnement
     * @return A AbonnementEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AbonnementEntity updateAbonnement(AbonnementEntity abonnement) {
        return abonnementService.update(abonnement);
    }
    
    /**
     * Delete an existing Abonnement Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /abonnements/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deleteAbonnement(@PathParam("id") Long id) {
        AbonnementEntity abonnement = abonnementService.find(id);
        abonnementService.delete(abonnement);
    }
    
}
