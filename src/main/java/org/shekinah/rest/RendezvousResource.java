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

import org.shekinah.domain.RendezvousEntity;
import org.shekinah.service.RendezvousService;

@Path("/rendezvouss")
@Named
public class RendezvousResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private RendezvousService rendezvousService;
    
    /**
     * Get the complete list of Rendezvous Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /rendezvouss
     * @return List of RendezvousEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<RendezvousEntity> getAllRendezvouss() {
        return rendezvousService.findAllRendezvousEntities();
    }
    
    /**
     * Get the number of Rendezvous Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /rendezvouss/count
     * @return Number of RendezvousEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return rendezvousService.countAllEntries();
    }
    
    /**
     * Get a Rendezvous Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /rendezvouss/3
     * @param id
     * @return A Rendezvous Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RendezvousEntity getRendezvousById(@PathParam("id") Long id) {
        return rendezvousService.find(id);
    }
    
    /**
     * Create a Rendezvous Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New RendezvousEntity (JSON) <br/>
     * Example URL: /rendezvouss
     * @param rendezvous
     * @return A RendezvousEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RendezvousEntity addRendezvous(RendezvousEntity rendezvous) {
        return rendezvousService.save(rendezvous);
    }
    
    /**
     * Update an existing Rendezvous Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated RendezvousEntity (JSON) <br/>
     * Example URL: /rendezvouss
     * @param rendezvous
     * @return A RendezvousEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RendezvousEntity updateRendezvous(RendezvousEntity rendezvous) {
        return rendezvousService.update(rendezvous);
    }
    
    /**
     * Delete an existing Rendezvous Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /rendezvouss/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deleteRendezvous(@PathParam("id") Long id) {
        RendezvousEntity rendezvous = rendezvousService.find(id);
        rendezvousService.delete(rendezvous);
    }
    
}
