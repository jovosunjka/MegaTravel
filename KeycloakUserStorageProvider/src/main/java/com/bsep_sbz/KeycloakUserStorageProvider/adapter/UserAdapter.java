package com.bsep_sbz.KeycloakUserStorageProvider.adapter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import org.keycloak.storage.federated.UserRoleMappingsFederatedStorage;

import com.bsep_sbz.KeycloakUserStorageProvider.entities.RoleEntity;
import com.bsep_sbz.KeycloakUserStorageProvider.entities.RoleMemberEntity;
import com.bsep_sbz.KeycloakUserStorageProvider.entities.RoleMemberIdEntity;
import com.bsep_sbz.KeycloakUserStorageProvider.entities.UserEntity;

public class UserAdapter extends AbstractUserAdapterFederatedStorage implements UserRoleMappingsFederatedStorage {

    protected UserEntity userEntity;
    protected String keycloakId;
    protected EntityManager entityManager;

    public UserAdapter(KeycloakSession keycloakSession, RealmModel realmModel, ComponentModel componentModel,
                       UserEntity userEntity, EntityManager entityManager) {
        super(keycloakSession, realmModel, componentModel);
        this.userEntity = userEntity;
        this.entityManager = entityManager;
        keycloakId = StorageId.keycloakId(componentModel, String.valueOf(userEntity.getId()));
    }

    public String getPassword() {
        return userEntity.getPassword();
    }

    public void setPassword(String password) {
        userEntity.setPassword(password);
    }


    public String getUsername() {
        return userEntity.getUsername();
    }

    public void setUsername(String username) {
        userEntity.setUsername(username);
    }

    @Override
    public void setLastName(String lastName) {
        userEntity.setLastname(lastName);
    }

    @Override
    public String getLastName() {
        return userEntity.getLastname();
    }

    @Override
    public void setFirstName(String firstName) {
        userEntity.setFirstname(firstName);
    }

    @Override
    public String getFirstName() {
        return userEntity.getFirstname();
    }

    @Override
    public void setEmail(String email) {
        userEntity.setEmail(email);
    }

    @Override
    public String getEmail() {
        return userEntity.getEmail();
    }

    @Override
    public String getId() {
        return keycloakId;
    }

    @Override
    public void setSingleAttribute(String name, String value) {
       /*
       if (name.equals("Phone")) {
            userEntity.setPhone(value);
        } else if (name.equals("Representative")) {
            userEntity.setRepresentativeId(Integer.valueOf(value));
        } else if (name.equals("Headquarter")) {
            userEntity.setHeadquarterId(Integer.valueOf(value));
        } else if (name.equals("Chief")) {
            userEntity.setChiefId(Integer.valueOf(value));
        }
        */

        super.setSingleAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        /*
        if (name.equals("Phone")) {
            userEntity.setPhone(null);
        } else if (name.equals("Representative")) {
            userEntity.setRepresentativeId(0);
        } else if (name.equals("Headquarter")) {
            userEntity.setHeadquarterId(0);
        } else if (name.equals("Chief")) {
            userEntity.setChiefId(0);
        }
         */

        super.removeAttribute(name);
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        /*
        if (name.equals("Phone")) {
            userEntity.setPhone(values.get(0));
        } else if (name.equals("Representative")) {
            userEntity.setRepresentativeId(Integer.valueOf(values.get(0)));
        } else if (name.equals("Headquarter")) {
            userEntity.setHeadquarterId(Integer.valueOf(values.get(0)));
        } else if (name.equals("Chief")) {
            userEntity.setChiefId(Integer.valueOf(values.get(0)));
        }
        */

        super.setAttribute(name, values);
    }

    @Override
    public String getFirstAttribute(String name) {
        /*
        if (name.equals("Phone")) {
            return userEntity.getPhone();
        } else if (name.equals("Representative")) {
            return String.valueOf(userEntity.getRepresentativeId());
        } else if (name.equals("Headquarter")) {
            return String.valueOf(userEntity.getHeadquarterId());
        } else if (name.equals("Chief")) {
            return String.valueOf(userEntity.getChiefId());
        }
        */

        return super.getFirstAttribute(name);
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        Map<String, List<String>> attributes = super.getAttributes();
        MultivaluedHashMap<String, String> all = new MultivaluedHashMap<String, String>();
        all.putAll(attributes);
        /*
        all.add("Phone", userEntity.getPhone());
        all.add("Representative", String.valueOf(userEntity.getRepresentativeId()));
        all.add("Headquarter", String.valueOf(userEntity.getHeadquarterId()));
        all.add("Chief", String.valueOf(userEntity.getChiefId()));
         */
        return all;
    }

    @Override
    public List<String> getAttribute(String name) {
        /*
        if (name.equals("Phone")) {
            List<String> phone = new LinkedList<String>();
            phone.add(userEntity.getPhone());
            return phone;
        } else if (name.equals("Representative")) {
            List<String> representative = new LinkedList<String>();
            representative.add(String.valueOf(userEntity.getRepresentativeId()));
            return representative;
        } else if (name.equals("Headquarter")) {
            List<String> headquarter = new LinkedList<String>();
            headquarter.add(String.valueOf(userEntity.getHeadquarterId()));
            return headquarter;
        } else if (name.equals("Chief")) {
            List<String> chief = new LinkedList<String>();
            chief.add(String.valueOf(userEntity.getChiefId()));
            return chief;
        }
        */

        return super.getAttribute(name);
    }

    @Override
    public Set<RoleModel> getRoleMappings() {
        Set<RoleModel> set = super.getRoleMappings();
        set.addAll(getRoleMappings(realm, keycloakId));
        return set;
    }

    @Override
    protected Set<RoleModel> getFederatedRoleMappings() {
        Set<RoleModel> set = super.getFederatedRoleMappings();
        set.addAll(getRoleMappings(realm, keycloakId));
        return getFederatedStorage().getRoleMappings(realm, this.getId());
    }

    @Override
    protected Set<RoleModel> getRoleMappingsInternal() {
        return getRoleMappings(realm, keycloakId);
    }

    @Override
    public void grantRole(RealmModel realm, String userId, RoleModel role) {
        RoleEntity entity = new RoleEntity();
        UserEntity user = new UserEntity();
        user.setId(Long.valueOf(keycloakId));
        entity.getUser().add(user);
        entityManager.persist(entity);
    }

    @Override
    public Set<RoleModel> getRoleMappings(RealmModel realm, String userId) {
        Set<RoleModel> set = new HashSet<RoleModel>();
        TypedQuery<RoleMemberEntity> query = entityManager.createNamedQuery("searchRoleForUser", RoleMemberEntity.class);
        query.setParameter("userId", Long.valueOf(StorageId.externalId(userId)));
        List<RoleMemberEntity> results = query.getResultList();
        if (results.isEmpty()) {
            return set;
        }

        for (RoleMemberEntity entity : results) {
            RoleEntity role = entityManager.find(RoleEntity.class, entity.getId().getRoleId());
            RoleModel roleModel = realm.getRole(role.getName());
            if (roleModel == null) {
                roleModel = new RoleAdapter(role);
                session.realms().addRealmRole(realm, role.getName());
            }

            set.add(roleModel);
        }

        return set;
    }

    @Override
    public void grantRole(RoleModel role) {
        super.grantRole(role);
        TypedQuery<RoleEntity> query = entityManager.createNamedQuery("getRolyByName", RoleEntity.class);
        query.setParameter("name", role.getName());
        List<RoleEntity> results = query.getResultList();
        if (results.isEmpty()) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(role.getName());
            entityManager.flush();
            entityManager.persist(roleEntity);
            results.add(roleEntity);
        }

        for (RoleEntity tmpRole : results) {
            RoleMemberEntity roleMemberEntity = new RoleMemberEntity();
            RoleMemberIdEntity roleMemberIdEntity = new RoleMemberIdEntity(tmpRole.getId(),
                    Long.valueOf(StorageId.externalId(keycloakId)));
            roleMemberEntity.setId(roleMemberIdEntity);
            entityManager.flush();
            entityManager.persist(roleMemberEntity);
        }
    }

    @Override
    public void deleteRoleMapping(RoleModel roleModel) {
        super.deleteRoleMapping(roleModel);
        UserEntity userEntity = entityManager.find(UserEntity.class, Long.valueOf(StorageId.externalId(keycloakId)));
        for (RoleEntity tmpRole : userEntity.getRoles()) {

            if (roleModel.getName().equals(tmpRole.getName())) {
                RoleMemberEntity roleMemberEntity = new RoleMemberEntity();
                RoleMemberIdEntity roleMemberIdEntity = new RoleMemberIdEntity(tmpRole.getId(),
                        Long.valueOf(StorageId.externalId(keycloakId)));
                roleMemberEntity.setId(roleMemberIdEntity);
                entityManager.remove(entityManager.contains(roleMemberEntity) ? roleMemberEntity : entityManager.merge(roleMemberEntity));
            }
        }
    }

    @Override
    public void deleteRoleMapping(RealmModel realm, String userId, RoleModel role) {
    }
}
