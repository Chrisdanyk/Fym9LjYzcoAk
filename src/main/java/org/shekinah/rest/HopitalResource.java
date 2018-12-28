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

import org.shekinah.domain.HopitalEntity;
import org.shekinah.domain.PatientEntity;
import org.shekinah.domain.security.UserEntity;
import org.shekinah.service.HopitalService;
import org.shekinah.service.PatientService;
import org.shekinah.service.security.UserService;

@Path("/hopitals")
@Named
public class HopitalResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private HopitalService hopitalService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private PatientService patientService;
    
    /**
     * Get the complete list of Hopital Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /hopitals
     * @return List of HopitalEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<HopitalEntity> getAllHopitals() {
        return hopitalService.findAllHopitalEntities();
    }
    
    /**
     * Get the number of Hopital Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /hopitals/count
     * @return Number of HopitalEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return hopitalService.countAllEntries();
    }
    
    /**
     * Get a Hopital Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /hopitals/3
     * @param id
     * @return A Hopital Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HopitalEntity getHopitalById(@PathParam("id") Long id) {
        return hopitalService.find(id);
    }
    
    /**
     * Create a Hopital Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New HopitalEntity (JSON) <br/>
     * Example URL: /hopitals
     * @param hopital
     * @return A HopitalEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HopitalEntity addHopital(HopitalEntity hopital) {
        return hopitalService.save(hopital);
    }
    
    /**
     * Update an existing Hopital Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated HopitalEntity (JSON) <br/>
     * Example URL: /hopitals
     * @param hopital
     * @return A HopitalEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HopitalEntity updateHopital(HopitalEntity hopital) {
        return hopitalService.update(hopital);
    }
    
    /**
     * Delete an existing Hopital Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /hopitals/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deleteHopital(@PathParam("id") Long id) {
        HopitalEntity hopital = hopitalService.find(id);
        hopitalService.delete(hopital);
    }
    
    /**
     * Get the list of User that is assigned to a Hopital <br/>
     * HTTP Method: GET <br/>
     * Example URL: /hopitals/3/users
     * @param hopitalId
     * @return List of UserEntity
     */
    @GET
    @Path("{id}/users")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserEntity> getUsers(@PathParam("id") Long hopitalId) {
        HopitalEntity hopital = hopitalService.find(hopitalId);
        return userService.findUsersByHopital(hopital);
    }
    
    /**
     * Assign an existing User to an existing Hopital <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /hopitals/3/users/8
     * @param hopitalId
     * @param userId
     */
    @PUT
    @Path("{id}/users/{userId}")
    public void assignUser(@PathParam("id") Long hopitalId, @PathParam("userId") Long userId) {
        HopitalEntity hopital = hopitalService.find(hopitalId);
        UserEntity user = userService.find(userId);
        user.setHopital(hopital);
        userService.update(user);
    }
    
    /**
     * Remove a Hopital-to-User Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /hopitals/3/users/8
     * @param hopitalId
     * @param userId
     */
    @DELETE
    @Path("{id}/users/{userId}")
    public void unassignUser(@PathParam("id") Long hopitalId, @PathParam("userId") Long userId) {
        UserEntity user = userService.find(userId);
        user.setHopital(null);
        userService.update(user);
    }
    
    /**
     * Get the list of Patient that is assigned to a Hopital <br/>
     * HTTP Method: GET <br/>
     * Example URL: /hopitals/3/patients
     * @param hopitalId
     * @return List of PatientEntity
     */
    @GET
    @Path("{id}/patients")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PatientEntity> getPatients(@PathParam("id") Long hopitalId) {
        HopitalEntity hopital = hopitalService.find(hopitalId);
        return patientService.findPatientsByHopital(hopital);
    }
    
    /**
     * Assign an existing Patient to an existing Hopital <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /hopitals/3/patients/8
     * @param hopitalId
     * @param patientId
     */
    @PUT
    @Path("{id}/patients/{patientId}")
    public void assignPatient(@PathParam("id") Long hopitalId, @PathParam("patientId") Long patientId) {
        HopitalEntity hopital = hopitalService.find(hopitalId);
        PatientEntity patient = patientService.find(patientId);
        patient.setHopital(hopital);
        patientService.update(patient);
    }
    
    /**
     * Remove a Hopital-to-Patient Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /hopitals/3/patients/8
     * @param hopitalId
     * @param patientId
     */
    @DELETE
    @Path("{id}/patients/{patientId}")
    public void unassignPatient(@PathParam("id") Long hopitalId, @PathParam("patientId") Long patientId) {
        PatientEntity patient = patientService.find(patientId);
        patient.setHopital(null);
        patientService.update(patient);
    }
    
}
