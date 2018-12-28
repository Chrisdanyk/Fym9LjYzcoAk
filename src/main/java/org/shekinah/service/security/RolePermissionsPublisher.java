package org.shekinah.service.security;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.shekinah.domain.security.RolePermission;
import org.shekinah.domain.security.UserRole;

@Singleton
@Startup
public class RolePermissionsPublisher {

    private static final Logger logger = Logger.getLogger(RolePermissionsPublisher.class.getName());
    
    @Inject
    private RolePermissionsService rolePermissionService;
    
    @PostConstruct
    public void postConstruct() {

        if (rolePermissionService.countAllEntries() == 0) {

            rolePermissionService.save(new RolePermission(UserRole.Administrator, "infos:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "infos:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "infos:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "infos:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "infos:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Infirmier, "infos:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "infos:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Pharmacien, "infos:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "hopital:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "hopital:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "hopital:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "hopital:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "hopital:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Infirmier, "hopital:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "hopital:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Pharmacien, "hopital:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "service:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "service:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "service:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "service:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "service:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Infirmier, "service:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "service:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Pharmacien, "service:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "medicament:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "medicament:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "medicament:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "medicament:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "medicament:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Infirmier, "medicament:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "medicament:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Pharmacien, "medicament:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "famille:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "famille:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "famille:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "famille:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "famille:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Infirmier, "famille:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "famille:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Pharmacien, "famille:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "fournisseur:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "fournisseur:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "fournisseur:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "fournisseur:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "vente:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "vente:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "vente:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "vente:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Pharmacien, "vente:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Pharmacien, "vente:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Pharmacien, "vente:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Comptable, "vente:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Comptable, "vente:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Comptable, "vente:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "patient:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "patient:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "patient:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "patient:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "patient:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Infirmier, "patient:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "patient:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Pharmacien, "patient:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "creneau:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "creneau:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "creneau:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "creneau:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "creneau:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "creneau:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "creneau:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "rendezvous:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "rendezvous:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "rendezvous:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "rendezvous:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "rendezvous:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "rendezvous:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "rendezvous:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Secretaire, "rendezvous:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Secretaire, "rendezvous:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Secretaire, "rendezvous:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "consultation:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "consultation:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "consultation:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "consultation:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "consultation:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "consultation:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "consultation:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Infirmier, "consultation:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Infirmier, "consultation:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Infirmier, "consultation:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "consultation:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "consultation:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "consultation:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Comptable, "consultation:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Comptable, "consultation:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Comptable, "consultation:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "examen:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "examen:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "examen:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "examen:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "examen:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Infirmier, "examen:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "examen:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "labo:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "labo:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "labo:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "labo:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "labo:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "labo:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "labo:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Comptable, "labo:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Comptable, "labo:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Comptable, "labo:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "societeAbonnement:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "societeAbonnement:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "societeAbonnement:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "societeAbonnement:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "abonnement:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "abonnement:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "abonnement:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "abonnement:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "abonnement:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "salle:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "salle:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "salle:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "salle:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "salle:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Infirmier, "salle:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "salle:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Pharmacien, "salle:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "hospitalisation:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "hospitalisation:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "hospitalisation:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "hospitalisation:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "hospitalisation:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Infirmier, "hospitalisation:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Comptable, "hospitalisation:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Comptable, "hospitalisation:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Comptable, "hospitalisation:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "pavillon:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "pavillon:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "pavillon:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "pavillon:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "pavillon:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Infirmier, "pavillon:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Laborantin, "pavillon:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Pharmacien, "pavillon:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "ordonnance:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "ordonnance:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "ordonnance:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "ordonnance:delete"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "ordonnance:create"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "ordonnance:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Medecin, "ordonnance:update"));
            
            rolePermissionService.save(new RolePermission(UserRole.Pharmacien, "ordonnance:read"));
            
            rolePermissionService.save(new RolePermission(UserRole.Administrator, "user:*"));
            
            logger.info("Successfully created permissions for user roles.");
        }
    }
}
