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

import org.shekinah.domain.MedicamentEntity;
import org.shekinah.domain.VenteEntity;
import org.shekinah.service.MedicamentService;
import org.shekinah.service.VenteService;

@Path("/medicaments")
@Named
public class MedicamentResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private MedicamentService medicamentService;
    
    @Inject
    private VenteService venteService;
    
    /**
     * Get the complete list of Medicament Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /medicaments
     * @return List of MedicamentEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<MedicamentEntity> getAllMedicaments() {
        return medicamentService.findAllMedicamentEntities();
    }
    
    /**
     * Get the number of Medicament Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /medicaments/count
     * @return Number of MedicamentEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return medicamentService.countAllEntries();
    }
    
    /**
     * Get a Medicament Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /medicaments/3
     * @param id
     * @return A Medicament Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public MedicamentEntity getMedicamentById(@PathParam("id") Long id) {
        return medicamentService.find(id);
    }
    
    /**
     * Create a Medicament Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New MedicamentEntity (JSON) <br/>
     * Example URL: /medicaments
     * @param medicament
     * @return A MedicamentEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MedicamentEntity addMedicament(MedicamentEntity medicament) {
        return medicamentService.save(medicament);
    }
    
    /**
     * Update an existing Medicament Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated MedicamentEntity (JSON) <br/>
     * Example URL: /medicaments
     * @param medicament
     * @return A MedicamentEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MedicamentEntity updateMedicament(MedicamentEntity medicament) {
        return medicamentService.update(medicament);
    }
    
    /**
     * Delete an existing Medicament Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /medicaments/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deleteMedicament(@PathParam("id") Long id) {
        MedicamentEntity medicament = medicamentService.find(id);
        medicamentService.delete(medicament);
    }
    
    /**
     * Get the list of Vente that is assigned to a Medicament <br/>
     * HTTP Method: GET <br/>
     * Example URL: /medicaments/3/ventes
     * @param medicamentId
     * @return List of VenteEntity
     */
    @GET
    @Path("{id}/ventes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<VenteEntity> getVentes(@PathParam("id") Long medicamentId) {
        MedicamentEntity medicament = medicamentService.find(medicamentId);
        return venteService.findVentesByMedicament(medicament);
    }
    
    /**
     * Assign an existing Vente to an existing Medicament <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /medicaments/3/ventes/8
     * @param medicamentId
     * @param venteId
     */
    @PUT
    @Path("{id}/ventes/{venteId}")
    public void assignVente(@PathParam("id") Long medicamentId, @PathParam("venteId") Long venteId) {
        MedicamentEntity medicament = medicamentService.find(medicamentId);
        VenteEntity vente = venteService.find(venteId);
        vente.setMedicament(medicament);
        venteService.update(vente);
    }
    
    /**
     * Remove a Medicament-to-Vente Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /medicaments/3/ventes/8
     * @param medicamentId
     * @param venteId
     */
    @DELETE
    @Path("{id}/ventes/{venteId}")
    public void unassignVente(@PathParam("id") Long medicamentId, @PathParam("venteId") Long venteId) {
        VenteEntity vente = venteService.find(venteId);
        vente.setMedicament(null);
        venteService.update(vente);
    }
    
}
