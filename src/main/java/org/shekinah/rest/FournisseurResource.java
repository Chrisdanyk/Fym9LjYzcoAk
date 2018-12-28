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

import org.shekinah.domain.FournisseurEntity;
import org.shekinah.domain.MedicamentEntity;
import org.shekinah.service.FournisseurService;
import org.shekinah.service.MedicamentService;

@Path("/fournisseurs")
@Named
public class FournisseurResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private FournisseurService fournisseurService;
    
    @Inject
    private MedicamentService medicamentService;
    
    /**
     * Get the complete list of Fournisseur Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /fournisseurs
     * @return List of FournisseurEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<FournisseurEntity> getAllFournisseurs() {
        return fournisseurService.findAllFournisseurEntities();
    }
    
    /**
     * Get the number of Fournisseur Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /fournisseurs/count
     * @return Number of FournisseurEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return fournisseurService.countAllEntries();
    }
    
    /**
     * Get a Fournisseur Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /fournisseurs/3
     * @param id
     * @return A Fournisseur Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public FournisseurEntity getFournisseurById(@PathParam("id") Long id) {
        return fournisseurService.find(id);
    }
    
    /**
     * Create a Fournisseur Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New FournisseurEntity (JSON) <br/>
     * Example URL: /fournisseurs
     * @param fournisseur
     * @return A FournisseurEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public FournisseurEntity addFournisseur(FournisseurEntity fournisseur) {
        return fournisseurService.save(fournisseur);
    }
    
    /**
     * Update an existing Fournisseur Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated FournisseurEntity (JSON) <br/>
     * Example URL: /fournisseurs
     * @param fournisseur
     * @return A FournisseurEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public FournisseurEntity updateFournisseur(FournisseurEntity fournisseur) {
        return fournisseurService.update(fournisseur);
    }
    
    /**
     * Delete an existing Fournisseur Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /fournisseurs/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deleteFournisseur(@PathParam("id") Long id) {
        FournisseurEntity fournisseur = fournisseurService.find(id);
        fournisseurService.delete(fournisseur);
    }
    
    /**
     * Get the list of Medicament that is assigned to a Fournisseur <br/>
     * HTTP Method: GET <br/>
     * Example URL: /fournisseurs/3/medicaments
     * @param fournisseurId
     * @return List of MedicamentEntity
     */
    @GET
    @Path("{id}/medicaments")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MedicamentEntity> getMedicaments(@PathParam("id") Long fournisseurId) {
        FournisseurEntity fournisseur = fournisseurService.find(fournisseurId);
        return medicamentService.findMedicamentsByFournisseurs(fournisseur);
    }
    
    /**
     * Assign an existing Medicament to an existing Fournisseur <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /fournisseurs/3/medicaments/8
     * @param fournisseurId
     * @param medicamentId
     */
    @PUT
    @Path("{id}/medicaments/{medicamentId}")
    public void assignMedicament(@PathParam("id") Long fournisseurId, @PathParam("medicamentId") Long medicamentId) {
        FournisseurEntity fournisseur = fournisseurService.find(fournisseurId);
        MedicamentEntity medicament = medicamentService.find(medicamentId);
        medicament.setFournisseurs(fournisseur);
        medicamentService.update(medicament);
    }
    
    /**
     * Remove a Fournisseur-to-Medicament Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /fournisseurs/3/medicaments/8
     * @param fournisseurId
     * @param medicamentId
     */
    @DELETE
    @Path("{id}/medicaments/{medicamentId}")
    public void unassignMedicament(@PathParam("id") Long fournisseurId, @PathParam("medicamentId") Long medicamentId) {
        MedicamentEntity medicament = medicamentService.find(medicamentId);
        medicament.setFournisseurs(null);
        medicamentService.update(medicament);
    }
    
}
