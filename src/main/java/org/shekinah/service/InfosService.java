package org.shekinah.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.primefaces.model.SortOrder;
import org.shekinah.domain.InfosEntity;
import org.shekinah.domain.security.UserEntity;

@Named
public class InfosService extends BaseService<InfosEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public InfosService(){
        super(InfosEntity.class);
    }
    
    @Transactional
    public List<InfosEntity> findAllInfosEntities() {
        
        return entityManager.createQuery("SELECT o FROM Infos o ", InfosEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Infos o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(InfosEntity infos) {

        /* This is called before a Infos is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.cutAllInfosUsersAssignments(infos);
        
    }

    // Remove all assignments from all user a infos. Called before delete a infos.
    @Transactional
    private void cutAllInfosUsersAssignments(InfosEntity infos) {
        entityManager
                .createQuery("UPDATE User c SET c.infos = NULL WHERE c.infos = :p")
                .setParameter("p", infos).executeUpdate();
    }
    
    // Find all user which are not yet assigned to a infos
    @Transactional
    public List<InfosEntity> findAvailableInfos(UserEntity user) {
        Long id = -1L;
        if (user != null && user.getInfos() != null && user.getInfos().getId() != null) {
            id = user.getInfos().getId();
        }
        return entityManager.createQuery(
                "SELECT o FROM Infos o where o.id NOT IN (SELECT p.infos.id FROM User p where p.infos.id != :id)", InfosEntity.class)
                .setParameter("id", id).getResultList();    
    }

    @Transactional
    public InfosEntity lazilyLoadImageToInfos(InfosEntity infos) {
        PersistenceUnitUtil u = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        if (!u.isLoaded(infos, "image") && infos.getId() != null) {
            infos = find(infos.getId());
            infos.getImage().getId();
        }
        return infos;
    }
    
    // This is the central method called by the DataTable
    @Override
    @Transactional
    public List<InfosEntity> findEntriesPagedAndFilteredAndSorted(int firstResult, int maxResults, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        StringBuilder query = new StringBuilder();

        query.append("SELECT o FROM Infos o");
        
        query.append(" LEFT JOIN FETCH o.image");
        
        String nextConnective = " WHERE";
        
        Map<String, Object> queryParameters = new HashMap<>();
        
        if (filters != null && !filters.isEmpty()) {
            
            nextConnective += " ( ";
            
            for(String filterProperty : filters.keySet()) {
                
                if (filters.get(filterProperty) == null) {
                    continue;
                }
                
                switch (filterProperty) {
                
                case "nom":
                    query.append(nextConnective).append(" o.nom LIKE :nom");
                    queryParameters.put("nom", "%" + filters.get(filterProperty) + "%");
                    break;

                case "postnom":
                    query.append(nextConnective).append(" o.postnom LIKE :postnom");
                    queryParameters.put("postnom", "%" + filters.get(filterProperty) + "%");
                    break;

                case "prenom":
                    query.append(nextConnective).append(" o.prenom LIKE :prenom");
                    queryParameters.put("prenom", "%" + filters.get(filterProperty) + "%");
                    break;

                case "dateNaissance":
                    query.append(nextConnective).append(" o.dateNaissance = :dateNaissance");
                    queryParameters.put("dateNaissance", filters.get(filterProperty));
                    break;

                case "telephone":
                    query.append(nextConnective).append(" o.telephone LIKE :telephone");
                    queryParameters.put("telephone", "%" + filters.get(filterProperty) + "%");
                    break;

                case "adresse":
                    query.append(nextConnective).append(" o.adresse LIKE :adresse");
                    queryParameters.put("adresse", "%" + filters.get(filterProperty) + "%");
                    break;

                }
                
                nextConnective = " AND";
            }
            
            query.append(" ) ");
            nextConnective = " AND";
        }
        
        if (sortField != null && !sortField.isEmpty()) {
            query.append(" ORDER BY o.").append(sortField);
            query.append(SortOrder.DESCENDING.equals(sortOrder) ? " DESC" : " ASC");
        }
        
        TypedQuery<InfosEntity> q = this.entityManager.createQuery(query.toString(), this.getType());
        
        for(String queryParameter : queryParameters.keySet()) {
            q.setParameter(queryParameter, queryParameters.get(queryParameter));
        }

        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
