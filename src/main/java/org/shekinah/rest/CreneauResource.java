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

import org.shekinah.domain.CreneauEntity;
import org.shekinah.domain.RendezvousEntity;
import org.shekinah.service.CreneauService;
import org.shekinah.service.RendezvousService;

@Path("/creneaus")
@Named
public class CreneauResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private CreneauService creneauService;
    
    @Inject
    private RendezvousService rendezvousService;
    
    /**
     * Get the complete list of Creneau Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /creneaus
     * @return List of CreneauEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CreneauEntity> getAllCreneaus() {
        return creneauService.findAllCreneauEntities();
    }
    
    /**
     * Get the number of Creneau Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /creneaus/count
     * @return Number of CreneauEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return creneauService.countAllEntries();
    }
    
    /**
     * Get a Creneau Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /creneaus/3
     * @param id
     * @return A Creneau Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CreneauEntity getCreneauById(@PathParam("id") Long id) {
        return creneauService.find(id);
    }
    
    /**
     * Create a Creneau Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New CreneauEntity (JSON) <br/>
     * Example URL: /creneaus
     * @param creneau
     * @return A CreneauEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CreneauEntity addCreneau(CreneauEntity creneau) {
        return creneauService.save(creneau);
    }
    
    /**
     * Update an existing Creneau Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated CreneauEntity (JSON) <br/>
     * Example URL: /creneaus
     * @param creneau
     * @return A CreneauEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CreneauEntity updateCreneau(CreneauEntity creneau) {
        return creneauService.update(creneau);
    }
    
    /**
     * Delete an existing Creneau Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /creneaus/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deleteCreneau(@PathParam("id") Long id) {
        CreneauEntity creneau = creneauService.find(id);
        creneauService.delete(creneau);
    }
    
    /**
     * Get the list of Rendezvous that is assigned to a Creneau <br/>
     * HTTP Method: GET <br/>
     * Example URL: /creneaus/3/rendezvouss
     * @param creneauId
     * @return List of RendezvousEntity
     */
    @GET
    @Path("{id}/rendezvouss")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RendezvousEntity> getRendezvouss(@PathParam("id") Long creneauId) {
        CreneauEntity creneau = creneauService.find(creneauId);
        return rendezvousService.findRendezvoussByCreneau(creneau);
    }
    
    /**
     * Assign an existing Rendezvous to an existing Creneau <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /creneaus/3/rendezvouss/8
     * @param creneauId
     * @param rendezvousId
     */
    @PUT
    @Path("{id}/rendezvouss/{rendezvousId}")
    public void assignRendezvous(@PathParam("id") Long creneauId, @PathParam("rendezvousId") Long rendezvousId) {
        CreneauEntity creneau = creneauService.find(creneauId);
        RendezvousEntity rendezvous = rendezvousService.find(rendezvousId);
        rendezvous.setCreneau(creneau);
        rendezvousService.update(rendezvous);
    }
    
    /**
     * Remove a Creneau-to-Rendezvous Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /creneaus/3/rendezvouss/8
     * @param creneauId
     * @param rendezvousId
     */
    @DELETE
    @Path("{id}/rendezvouss/{rendezvousId}")
    public void unassignRendezvous(@PathParam("id") Long creneauId, @PathParam("rendezvousId") Long rendezvousId) {
        RendezvousEntity rendezvous = rendezvousService.find(rendezvousId);
        rendezvous.setCreneau(null);
        rendezvousService.update(rendezvous);
    }
    
}
