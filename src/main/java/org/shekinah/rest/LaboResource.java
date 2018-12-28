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

import org.shekinah.domain.LaboEntity;
import org.shekinah.service.LaboService;

@Path("/labos")
@Named
public class LaboResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private LaboService laboService;
    
    /**
     * Get the complete list of Labo Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /labos
     * @return List of LaboEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<LaboEntity> getAllLabos() {
        return laboService.findAllLaboEntities();
    }
    
    /**
     * Get the number of Labo Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /labos/count
     * @return Number of LaboEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return laboService.countAllEntries();
    }
    
    /**
     * Get a Labo Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /labos/3
     * @param id
     * @return A Labo Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public LaboEntity getLaboById(@PathParam("id") Long id) {
        return laboService.find(id);
    }
    
    /**
     * Create a Labo Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New LaboEntity (JSON) <br/>
     * Example URL: /labos
     * @param labo
     * @return A LaboEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public LaboEntity addLabo(LaboEntity labo) {
        return laboService.save(labo);
    }
    
    /**
     * Update an existing Labo Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated LaboEntity (JSON) <br/>
     * Example URL: /labos
     * @param labo
     * @return A LaboEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public LaboEntity updateLabo(LaboEntity labo) {
        return laboService.update(labo);
    }
    
    /**
     * Delete an existing Labo Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /labos/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deleteLabo(@PathParam("id") Long id) {
        LaboEntity labo = laboService.find(id);
        laboService.delete(labo);
    }
    
}
