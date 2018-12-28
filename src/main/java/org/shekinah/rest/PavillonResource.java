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

import org.shekinah.domain.PavillonEntity;
import org.shekinah.domain.SalleEntity;
import org.shekinah.service.PavillonService;
import org.shekinah.service.SalleService;

@Path("/pavillons")
@Named
public class PavillonResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private PavillonService pavillonService;
    
    @Inject
    private SalleService salleService;
    
    /**
     * Get the complete list of Pavillon Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /pavillons
     * @return List of PavillonEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PavillonEntity> getAllPavillons() {
        return pavillonService.findAllPavillonEntities();
    }
    
    /**
     * Get the number of Pavillon Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /pavillons/count
     * @return Number of PavillonEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return pavillonService.countAllEntries();
    }
    
    /**
     * Get a Pavillon Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /pavillons/3
     * @param id
     * @return A Pavillon Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PavillonEntity getPavillonById(@PathParam("id") Long id) {
        return pavillonService.find(id);
    }
    
    /**
     * Create a Pavillon Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New PavillonEntity (JSON) <br/>
     * Example URL: /pavillons
     * @param pavillon
     * @return A PavillonEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PavillonEntity addPavillon(PavillonEntity pavillon) {
        return pavillonService.save(pavillon);
    }
    
    /**
     * Update an existing Pavillon Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated PavillonEntity (JSON) <br/>
     * Example URL: /pavillons
     * @param pavillon
     * @return A PavillonEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PavillonEntity updatePavillon(PavillonEntity pavillon) {
        return pavillonService.update(pavillon);
    }
    
    /**
     * Delete an existing Pavillon Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /pavillons/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deletePavillon(@PathParam("id") Long id) {
        PavillonEntity pavillon = pavillonService.find(id);
        pavillonService.delete(pavillon);
    }
    
    /**
     * Get the list of Salle that is assigned to a Pavillon <br/>
     * HTTP Method: GET <br/>
     * Example URL: /pavillons/3/salles
     * @param pavillonId
     * @return List of SalleEntity
     */
    @GET
    @Path("{id}/salles")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SalleEntity> getSalles(@PathParam("id") Long pavillonId) {
        PavillonEntity pavillon = pavillonService.find(pavillonId);
        return salleService.findSallesByPavillon(pavillon);
    }
    
    /**
     * Assign an existing Salle to an existing Pavillon <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /pavillons/3/salles/8
     * @param pavillonId
     * @param salleId
     */
    @PUT
    @Path("{id}/salles/{salleId}")
    public void assignSalle(@PathParam("id") Long pavillonId, @PathParam("salleId") Long salleId) {
        PavillonEntity pavillon = pavillonService.find(pavillonId);
        SalleEntity salle = salleService.find(salleId);
        salle.setPavillon(pavillon);
        salleService.update(salle);
    }
    
    /**
     * Remove a Pavillon-to-Salle Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /pavillons/3/salles/8
     * @param pavillonId
     * @param salleId
     */
    @DELETE
    @Path("{id}/salles/{salleId}")
    public void unassignSalle(@PathParam("id") Long pavillonId, @PathParam("salleId") Long salleId) {
        SalleEntity salle = salleService.find(salleId);
        salle.setPavillon(null);
        salleService.update(salle);
    }
    
}
