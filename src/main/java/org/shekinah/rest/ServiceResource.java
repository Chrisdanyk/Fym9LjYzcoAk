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

import org.shekinah.domain.ServiceEntity;
import org.shekinah.domain.security.UserEntity;
import org.shekinah.service.ServiceService;
import org.shekinah.service.security.UserService;

@Path("/services")
@Named
public class ServiceResource implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private ServiceService serviceService;
    
    @Inject
    private UserService userService;
    
    /**
     * Get the complete list of Service Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /services
     * @return List of ServiceEntity (JSON)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceEntity> getAllServices() {
        return serviceService.findAllServiceEntities();
    }
    
    /**
     * Get the number of Service Entries <br/>
     * HTTP Method: GET <br/>
     * Example URL: /services/count
     * @return Number of ServiceEntity
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long getCount() {
        return serviceService.countAllEntries();
    }
    
    /**
     * Get a Service Entity <br/>
     * HTTP Method: GET <br/>
     * Example URL: /services/3
     * @param id
     * @return A Service Entity (JSON)
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ServiceEntity getServiceById(@PathParam("id") Long id) {
        return serviceService.find(id);
    }
    
    /**
     * Create a Service Entity <br/>
     * HTTP Method: POST <br/>
     * POST Request Body: New ServiceEntity (JSON) <br/>
     * Example URL: /services
     * @param service
     * @return A ServiceEntity (JSON)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ServiceEntity addService(ServiceEntity service) {
        return serviceService.save(service);
    }
    
    /**
     * Update an existing Service Entity <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: Updated ServiceEntity (JSON) <br/>
     * Example URL: /services
     * @param service
     * @return A ServiceEntity (JSON)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ServiceEntity updateService(ServiceEntity service) {
        return serviceService.update(service);
    }
    
    /**
     * Delete an existing Service Entity <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /services/3
     * @param id
     */
    @Path("{id}")
    @DELETE
    public void deleteService(@PathParam("id") Long id) {
        ServiceEntity service = serviceService.find(id);
        serviceService.delete(service);
    }
    
    /**
     * Get the list of User that is assigned to a Service <br/>
     * HTTP Method: GET <br/>
     * Example URL: /services/3/users
     * @param serviceId
     * @return List of UserEntity
     */
    @GET
    @Path("{id}/users")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserEntity> getUsers(@PathParam("id") Long serviceId) {
        ServiceEntity service = serviceService.find(serviceId);
        return userService.findUsersByService(service);
    }
    
    /**
     * Assign an existing User to an existing Service <br/>
     * HTTP Method: PUT <br/>
     * PUT Request Body: empty <br/>
     * Example URL: /services/3/users/8
     * @param serviceId
     * @param userId
     */
    @PUT
    @Path("{id}/users/{userId}")
    public void assignUser(@PathParam("id") Long serviceId, @PathParam("userId") Long userId) {
        ServiceEntity service = serviceService.find(serviceId);
        UserEntity user = userService.find(userId);
        user.setService(service);
        userService.update(user);
    }
    
    /**
     * Remove a Service-to-User Assignment <br/>
     * HTTP Method: DELETE <br/>
     * Example URL: /services/3/users/8
     * @param serviceId
     * @param userId
     */
    @DELETE
    @Path("{id}/users/{userId}")
    public void unassignUser(@PathParam("id") Long serviceId, @PathParam("userId") Long userId) {
        UserEntity user = userService.find(userId);
        user.setService(null);
        userService.update(user);
    }
    
}
