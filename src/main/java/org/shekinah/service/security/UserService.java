package org.shekinah.service.security;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.primefaces.model.SortOrder;
import org.shekinah.domain.HopitalEntity;
import org.shekinah.domain.ServiceEntity;
import org.shekinah.domain.security.UserEntity;
import org.shekinah.domain.security.UserStatus;
import org.shekinah.service.BaseService;
import org.shekinah.service.security.SecurityWrapper;
import org.shekinah.web.util.MessageFactory;

@Named
public class UserService extends BaseService<UserEntity> implements Serializable {
    
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private static final long serialVersionUID = 1L;
    
    public UserService(){
        super(UserEntity.class);
    }

    @Named("users")
    @Transactional
    public List<UserEntity> findAllUserEntities() {
        return entityManager.createQuery("SELECT o FROM User o", UserEntity.class).getResultList();
    }

    @Transactional
    public UserEntity findUserByUsername(String username) {
        UserEntity user;
        try {
            user = entityManager.createQuery("SELECT o FROM User o WHERE o.username = :p", UserEntity.class)
                    .setParameter("p", username).getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.INFO, "User with user name ''{0}'' does not exist.", username);
            return null;
        }
        return user;
    }
    
    @Transactional
    public UserEntity findUserByEmail(String email) {
        UserEntity user;
        try {
            user = entityManager.createQuery("SELECT o FROM User o WHERE o.email = :p", UserEntity.class)
                    .setParameter("p", email).getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.INFO, "User with email ''{0}'' does not exist.", email);
            return null;
        }
        return user;
    }
    
    @Transactional
    public UserEntity findUserByEmailResetPasswordKey(String emailResetPasswordKey) {
        UserEntity user;
        try {
            user = entityManager.createQuery("SELECT o FROM User o WHERE o.emailResetPasswordKey = :p", UserEntity.class)
                    .setParameter("p", emailResetPasswordKey).getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.INFO, "User with passwort reset key ''{0}'' not found.", emailResetPasswordKey);
            return null;
        }
        return user;
    }
    
    @Transactional
    public UserEntity findUserByEmailConfirmationKey(String emailConfirmationKey) {
        UserEntity user;
        try {
            user = entityManager.createQuery("SELECT o FROM User o WHERE o.emailConfirmationKey = :p", UserEntity.class)
                    .setParameter("p", emailConfirmationKey).getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.INFO, "User with activation key ''{0}'' not found.", emailConfirmationKey);
            return null;
        }
        return user;
    }
    
    @Override
    @Transactional
    public UserEntity save(UserEntity user){
        
        String salt = SecurityWrapper.generateSalt();
        user.setSalt(salt);
        
        user.setPassword(SecurityWrapper.hashPassword(user.getPassword(), salt));
        
        user.setCreatedAt(new Date());
        return super.save(user);
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM User o", Long.class).getSingleResult();
    }

    @Override
    protected void handleDependenciesBeforeDelete(UserEntity user) {
        
        /* This is called before a User is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.cutAllPharmacienVentesAssignments(user);
        
        this.cutAllMedecinCreneausAssignments(user);
        
        this.cutAllMedecinConsultationsAssignments(user);
        
        this.cutAllInfirmierConsultation2sAssignments(user);
        
        this.cutAllLaborantinConsultation3sAssignments(user);
        
        this.cutAllLaborantinLabosAssignments(user);
        
    }
    
    // Remove all assignments from all vente a user. Called before delete a user.
    @Transactional
    private void cutAllPharmacienVentesAssignments(UserEntity user) {
        entityManager
                .createQuery("UPDATE Vente c SET c.pharmacien = NULL WHERE c.pharmacien = :p")
                .setParameter("p", user).executeUpdate();
    }
    
    // Remove all assignments from all creneau a user. Called before delete a user.
    @Transactional
    private void cutAllMedecinCreneausAssignments(UserEntity user) {
        entityManager
                .createQuery("UPDATE Creneau c SET c.medecin = NULL WHERE c.medecin = :p")
                .setParameter("p", user).executeUpdate();
    }
    
    // Remove all assignments from all consultation a user. Called before delete a user.
    @Transactional
    private void cutAllMedecinConsultationsAssignments(UserEntity user) {
        entityManager
                .createQuery("UPDATE Consultation c SET c.medecin = NULL WHERE c.medecin = :p")
                .setParameter("p", user).executeUpdate();
    }
    
    // Remove all assignments from all consultation2 a user. Called before delete a user.
    @Transactional
    private void cutAllInfirmierConsultation2sAssignments(UserEntity user) {
        entityManager
                .createQuery("UPDATE Consultation c SET c.infirmier = NULL WHERE c.infirmier = :p")
                .setParameter("p", user).executeUpdate();
    }
    
    // Remove all assignments from all consultation3 a user. Called before delete a user.
    @Transactional
    private void cutAllLaborantinConsultation3sAssignments(UserEntity user) {
        entityManager
                .createQuery("UPDATE Consultation c SET c.laborantin = NULL WHERE c.laborantin = :p")
                .setParameter("p", user).executeUpdate();
    }
    
    // Remove all assignments from all labo a user. Called before delete a user.
    @Transactional
    private void cutAllLaborantinLabosAssignments(UserEntity user) {
        entityManager
                .createQuery("UPDATE Labo c SET c.laborantin = NULL WHERE c.laborantin = :p")
                .setParameter("p", user).executeUpdate();
    }
    
    @Transactional
    public List<UserEntity> findAvailableUsers(HopitalEntity hopital) {
        return entityManager.createQuery("SELECT o FROM User o WHERE o.hopital IS NULL", UserEntity.class).getResultList();
    }

    @Transactional
    public List<UserEntity> findUsersByHopital(HopitalEntity hopital) {
        return entityManager.createQuery("SELECT o FROM User o WHERE o.hopital = :hopital", UserEntity.class).setParameter("hopital", hopital).getResultList();
    }

    @Transactional
    public List<UserEntity> findAvailableUsers(ServiceEntity service) {
        return entityManager.createQuery("SELECT o FROM User o WHERE o.service IS NULL", UserEntity.class).getResultList();
    }

    @Transactional
    public List<UserEntity> findUsersByService(ServiceEntity service) {
        return entityManager.createQuery("SELECT o FROM User o WHERE o.service = :service", UserEntity.class).setParameter("service", service).getResultList();
    }

    @Override
    @Transactional
    public List<UserEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();
        query.append("SELECT o FROM User o");

        // Can be optimized: We need this join only when hopital filter is set
        query.append(" LEFT OUTER JOIN o.hopital hopital");
        
        // Can be optimized: We need this join only when service filter is set
        query.append(" LEFT OUTER JOIN o.service service");
        
        String nextConnective = " WHERE";
        
        Map<String, Object> queryParameters = new HashMap<>();
        
        if (filters != null && !filters.isEmpty()) {
            
            nextConnective += " ( ";
        
            for(String filterProperty : filters.keySet()) {
                
                switch (filterProperty) {
                
                case "username":
                    query.append(nextConnective).append(" o.username LIKE :username");
                    queryParameters.put("username", "%" + filters.get(filterProperty) + "%");
                    break;
                
                case "email":
                    query.append(nextConnective).append(" o.email LIKE :email");
                    queryParameters.put("email", "%" + filters.get(filterProperty) + "%");
                    break;
                    
                case "status":
                    query.append(nextConnective).append(" o.status = :status");
                    queryParameters.put("status", UserStatus.valueOf(filters.get(filterProperty).toString()));
                    break;
                
                case "matricule":
                    query.append(nextConnective).append(" o.matricule LIKE :matricule");
                    queryParameters.put("matricule", "%" + filters.get(filterProperty) + "%");
                    break;

                case "hopital":
                    query.append(nextConnective).append(" o.hopital = :hopital");
                    queryParameters.put("hopital", filters.get(filterProperty));
                    break;
                
                case "service":
                    query.append(nextConnective).append(" o.service = :service");
                    queryParameters.put("service", filters.get(filterProperty));
                    break;
                
                }
                
                nextConnective = " AND";
            }
            
            query.append(" ) ");
        }
        
        if (sortField != null && !sortField.isEmpty()) {
            query.append(" ORDER BY o.").append(sortField);
            query.append(SortOrder.DESCENDING.equals(sortOrder) ? " DESC" : " ASC");
        }
        
        TypedQuery<UserEntity> q = this.entityManager.createQuery(query.toString(), UserEntity.class);
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    /**
     * Method change user's password if oldPassword is correct and newPassword equals newPasswordRepeat
     * @param user
     * @param newPassword
     * @param newPasswordRepeat
     * @param oldPassword
     * @return 
     * */
    @Transactional
    public FacesMessage changePassword(UserEntity user, String newPassword, String newPasswordRepeat, String oldPassword){
        try {
            FacesMessage facesMessage;

            if (user.getPassword().equals(SecurityWrapper.hashPassword(oldPassword, user.getSalt()))) {
                facesMessage = this.changePassword(user, newPassword, newPasswordRepeat);

            } else {
                String message = "incorrect_password";
                facesMessage = MessageFactory.getMessage(message);
                facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            }

            return facesMessage;
        } catch (Throwable e){
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Method change user's password if newPassword equals newPasswordRepeat.
     * This is less safety method since it doesn't check old password, we use it in forgot password handler only.
     * @param user
     * @param newPassword
     * @param newPasswordRepeat
     * @return 
     * */
    @Transactional
    public FacesMessage changePassword(UserEntity user, String newPassword, String newPasswordRepeat){
        try {
            FacesMessage facesMessage;

            if (!newPassword.equals("") && newPassword.equals(newPasswordRepeat)) {
                user.setPassword(SecurityWrapper.hashPassword(newPassword, user.getSalt()));

                this.update(user);
                facesMessage = MessageFactory.getMessage("password_successfully_changed");

            } else {
                String message = "error_while_changing_password";

                facesMessage = MessageFactory.getMessage(message);
                facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            }

            return facesMessage;
        } catch (Throwable e){
            throw new RuntimeException(e);
        }
    }
}
