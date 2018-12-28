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
import org.shekinah.domain.SocieteAbonnementEntity;
import org.shekinah.service.AbonnementService;
import org.shekinah.service.SocieteAbonnementService;

@Path("/societeAbonnements")
@Named
public class SocieteAbonnementResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private SocieteAbonnementService societeAbonnementService;
    
    @Inject
    private AbonnementService abonnementService;
    
    /**
     * Get the complete list of SocieteAbonnement Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /societeAbonnements
     * @return List of SocieteAbonnementEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SocieteAbonnementEntity> getAllSocieteAbonnements() {
        return societeAbonnementService.findAllSocieteAbonnementEntities();
    }
    
    /**
     * Get the number of SocieteAbonnement Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /societeAbonnements/count
     * @return Number of SocieteAbonnementEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return societeAbonnementService.countAllEntries();
    }
    
    /**
     * Get a SocieteAbonnement Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /societeAbonnements/3
     * @param id
     * @return A SocieteAbonnement Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SocieteAbonnementEntity getSocieteAbonnementById(@PathParam("id") Long id) {
        return societeAbonnementService.find(id);
    }
    
    /**
     * Create a SocieteAbonnement Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New SocieteAbonnementEntity (JSON) <br/>
     * Example URL: /societeAbonnements
     * @param societeAbonnement
     * @return A SocieteAbonnementEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SocieteAbonnementEntity addSocieteAbonnement(SocieteAbonnementEntity societeAbonnement) {
        return societeAbonnementService.save(societeAbonnement);
    }
    
    /**
     * Update an existing SocieteAbonnement Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated SocieteAbonnementEntity (JSON) <br/>
     * Example URL: /societeAbonnements
     * @param societeAbonnement
     * @return A SocieteAbonnementEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SocieteAbonnementEntity updateSocieteAbonnement(SocieteAbonnementEntity societeAbonnement) {
        return societeAbonnementService.update(societeAbonnement);
    }
    
    /**
     * Delete an existing SocieteAbonnement Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /societeAbonnements/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deleteSocieteAbonnement(@PathParam("id") Long id) {
        SocieteAbonnementEntity societeAbonnement = societeAbonnementService.find(id);
        societeAbonnementService.delete(societeAbonnement);
    }
    
    /**
     * Get the list of Abonnement that is assigned to a SocieteAbonnement <br/>
     * HTTP Method: GET <br/>
     * Example URL: /societeAbonnements/3/abonnements
     * @param societeAbonnementId
     * @return List of AbonnementEntity
     */
    @GET
    @Path("{id}/abonnements")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AbonnementEntity> getAbonnements(@PathParam("id") Long societeAbonnementId) {
        SocieteAbonnementEntity societeAbonnement = societeAbonnementService.find(societeAbonnementId);
        return abonnementService.findAbonnementsBySociete(societeAbonnement);
    }
    
    /**
     * Assign an existing Abonnement to an existing SocieteAbonnement <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /societeAbonnements/3/abonnements/8
     * @param societeAbonnementId
     * @param abonnementId
     */
    @PUT
    @Path("{id}/abonnements/{abonnementId}")
    public void assignAbonnement(@PathParam("id") Long societeAbonnementId, @PathParam("abonnementId") Long abonnementId) {
        SocieteAbonnementEntity societeAbonnement = societeAbonnementService.find(societeAbonnementId);
        AbonnementEntity abonnement = abonnementService.find(abonnementId);
        abonnement.setSociete(societeAbonnement);
        abonnementService.update(abonnement);
    }
    
    /**
     * Remove a SocieteAbonnement-to-Abonnement Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /societeAbonnements/3/abonnements/8
     * @param societeAbonnementId
     * @param abonnementId
     */
    @DELETE
    @Path("{id}/abonnements/{abonnementId}")
    public void unassignAbonnement(@PathParam("id") Long societeAbonnementId, @PathParam("abonnementId") Long abonnementId) {
        AbonnementEntity abonnement = abonnementService.find(abonnementId);
        abonnement.setSociete(null);
        abonnementService.update(abonnement);
    }
    
}
