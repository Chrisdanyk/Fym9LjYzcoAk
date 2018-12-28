package org.shekinah.service.security;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.shekinah.domain.security.UserEntity;
import org.shekinah.domain.security.UserRole;
import org.shekinah.domain.security.UserStatus;

/**
 * Creates some test users in fresh database.
 * 
 * TODO This class is temporary for test, only. Just delete this class
 * if you do not need the test users to be created automatically.
 *
 */
@Singleton
@Startup
public class TestUsersCreator {

    private static final Logger logger = Logger.getLogger(TestUsersCreator.class.getName());
    
    @Inject
    private UserService userService;
    
    @PostConstruct
    public void postConstruct() {
        
       if(userService.countAllEntries() == 0) {
           
            logger.log(Level.WARNING, "Creating test user 'admin' with password 'admin'.");
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setRoles(Arrays.asList(new UserRole[]{UserRole.Administrator}));
            admin.setStatus(UserStatus.Active);
            admin.setEmail("admin@domain.test");
            
            userService.save(admin);
            
            logger.log(Level.WARNING, "Creating test user 'medecin' with password 'medecin'.");
            UserEntity medecinUser = new UserEntity();
            medecinUser.setUsername("medecin");
            medecinUser.setPassword("medecin");
            medecinUser.setRoles(Arrays.asList(new UserRole[]{UserRole.Medecin}));
            medecinUser.setStatus(UserStatus.Active);
            medecinUser.setEmail("medecin@domain.test");
            
            userService.save(medecinUser);
            
            logger.log(Level.WARNING, "Creating test user 'infirmier' with password 'infirmier'.");
            UserEntity infirmierUser = new UserEntity();
            infirmierUser.setUsername("infirmier");
            infirmierUser.setPassword("infirmier");
            infirmierUser.setRoles(Arrays.asList(new UserRole[]{UserRole.Infirmier}));
            infirmierUser.setStatus(UserStatus.Active);
            infirmierUser.setEmail("infirmier@domain.test");
            
            userService.save(infirmierUser);
            
            logger.log(Level.WARNING, "Creating test user 'laborantin' with password 'laborantin'.");
            UserEntity laborantinUser = new UserEntity();
            laborantinUser.setUsername("laborantin");
            laborantinUser.setPassword("laborantin");
            laborantinUser.setRoles(Arrays.asList(new UserRole[]{UserRole.Laborantin}));
            laborantinUser.setStatus(UserStatus.Active);
            laborantinUser.setEmail("laborantin@domain.test");
            
            userService.save(laborantinUser);
            
            logger.log(Level.WARNING, "Creating test user 'pharmacien' with password 'pharmacien'.");
            UserEntity pharmacienUser = new UserEntity();
            pharmacienUser.setUsername("pharmacien");
            pharmacienUser.setPassword("pharmacien");
            pharmacienUser.setRoles(Arrays.asList(new UserRole[]{UserRole.Pharmacien}));
            pharmacienUser.setStatus(UserStatus.Active);
            pharmacienUser.setEmail("pharmacien@domain.test");
            
            userService.save(pharmacienUser);
            
            logger.log(Level.WARNING, "Creating test user 'secretaire' with password 'secretaire'.");
            UserEntity secretaireUser = new UserEntity();
            secretaireUser.setUsername("secretaire");
            secretaireUser.setPassword("secretaire");
            secretaireUser.setRoles(Arrays.asList(new UserRole[]{UserRole.Secretaire}));
            secretaireUser.setStatus(UserStatus.Active);
            secretaireUser.setEmail("secretaire@domain.test");
            
            userService.save(secretaireUser);
            
            logger.log(Level.WARNING, "Creating test user 'comptable' with password 'comptable'.");
            UserEntity comptableUser = new UserEntity();
            comptableUser.setUsername("comptable");
            comptableUser.setPassword("comptable");
            comptableUser.setRoles(Arrays.asList(new UserRole[]{UserRole.Comptable}));
            comptableUser.setStatus(UserStatus.Active);
            comptableUser.setEmail("comptable@domain.test");
            
            userService.save(comptableUser);
            
            logger.log(Level.WARNING, "Creating test user 'registered' with password 'registered'.");
            UserEntity registeredUser = new UserEntity();
            registeredUser.setUsername("registered");
            registeredUser.setPassword("registered");
            registeredUser.setRoles(Arrays.asList(new UserRole[]{UserRole.Registered}));
            registeredUser.setStatus(UserStatus.Active);
            registeredUser.setEmail("registered@domain.test");
            
            userService.save(registeredUser);
            
        }
    }
}
