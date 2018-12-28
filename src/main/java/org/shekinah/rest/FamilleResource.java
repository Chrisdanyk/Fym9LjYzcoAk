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

import org.shekinah.domain.FamilleEntity;
import org.shekinah.domain.MedicamentEntity;
import org.shekinah.service.FamilleService;
import org.shekinah.service.MedicamentService;

@Path("/familles")
@Named
public class FamilleResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private FamilleService familleService;
    
    @Inject
    private MedicamentService medicamentService;
    
    /**
     * Get the complete list of Famille Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /familles
     * @return List of FamilleEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<FamilleEntity> getAllFamilles() {
        return familleService.findAllFamilleEntities();
    }
    
    /**
     * Get the number of Famille Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /familles/count
     * @return Number of FamilleEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return familleService.countAllEntries();
    }
    
    /**
     * Get a Famille Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /familles/3
     * @param id
     * @return A Famille Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public FamilleEntity getFamilleById(@PathParam("id") Long id) {
        return familleService.find(id);
    }
    
    /**
     * Create a Famille Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New FamilleEntity (JSON) <br/>
     * Example URL: /familles
     * @param famille
     * @return A FamilleEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public FamilleEntity addFamille(FamilleEntity famille) {
        return familleService.save(famille);
    }
    
    /**
     * Update an existing Famille Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated FamilleEntity (JSON) <br/>
     * Example URL: /familles
     * @param famille
     * @return A FamilleEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public FamilleEntity updateFamille(FamilleEntity famille) {
        return familleService.update(famille);
    }
    
    /**
     * Delete an existing Famille Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /familles/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deleteFamille(@PathParam("id") Long id) {
        FamilleEntity famille = familleService.find(id);
        familleService.delete(famille);
    }
    
    /**
     * Get the list of Medicament that is assigned to a Famille <br/>
     * HTTP Method: GET <br/>
     * Example URL: /familles/3/medicaments
     * @param familleId
     * @return List of MedicamentEntity
     */
    @GET
    @Path("{id}/medicaments")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MedicamentEntity> getMedicaments(@PathParam("id") Long familleId) {
        FamilleEntity famille = familleService.find(familleId);
        return medicamentService.findMedicamentsByFamille(famille);
    }
    
    /**
     * Assign an existing Medicament to an existing Famille <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /familles/3/medicaments/8
     * @param familleId
     * @param medicamentId
     */
    @PUT
    @Path("{id}/medicaments/{medicamentId}")
    public void assignMedicament(@PathParam("id") Long familleId, @PathParam("medicamentId") Long medicamentId) {
        FamilleEntity famille = familleService.find(familleId);
        MedicamentEntity medicament = medicamentService.find(medicamentId);
        medicament.setFamille(famille);
        medicamentService.update(medicament);
    }
    
    /**
     * Remove a Famille-to-Medicament Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /familles/3/medicaments/8
     * @param familleId
     * @param medicamentId
     */
    @DELETE
    @Path("{id}/medicaments/{medicamentId}")
    public void unassignMedicament(@PathParam("id") Long familleId, @PathParam("medicamentId") Long medicamentId) {
        MedicamentEntity medicament = medicamentService.find(medicamentId);
        medicament.setFamille(null);
        medicamentService.update(medicament);
    }
    
}
