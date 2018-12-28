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

import org.shekinah.domain.VenteEntity;
import org.shekinah.service.VenteService;

@Path("/ventes")
@Named
public class VenteResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private VenteService venteService;
    
    /**
     * Get the complete list of Vente Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /ventes
     * @return List of VenteEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<VenteEntity> getAllVentes() {
        return venteService.findAllVenteEntities();
    }
    
    /**
     * Get the number of Vente Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /ventes/count
     * @return Number of VenteEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return venteService.countAllEntries();
    }
    
    /**
     * Get a Vente Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /ventes/3
     * @param id
     * @return A Vente Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public VenteEntity getVenteById(@PathParam("id") Long id) {
        return venteService.find(id);
    }
    
    /**
     * Create a Vente Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New VenteEntity (JSON) <br/>
     * Example URL: /ventes
     * @param vente
     * @return A VenteEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public VenteEntity addVente(VenteEntity vente) {
        return venteService.save(vente);
    }
    
    /**
     * Update an existing Vente Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated VenteEntity (JSON) <br/>
     * Example URL: /ventes
     * @param vente
     * @return A VenteEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public VenteEntity updateVente(VenteEntity vente) {
        return venteService.update(vente);
    }
    
    /**
     * Delete an existing Vente Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /ventes/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deleteVente(@PathParam("id") Long id) {
        VenteEntity vente = venteService.find(id);
        venteService.delete(vente);
    }
    
}
