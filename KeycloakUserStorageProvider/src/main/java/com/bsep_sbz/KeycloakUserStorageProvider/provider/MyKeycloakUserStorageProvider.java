package com.bsep_sbz.KeycloakUserStorageProvider.provider;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.cache.OnUserCache;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import com.bsep_sbz.KeycloakUserStorageProvider.adapter.UserAdapter;
import com.bsep_sbz.KeycloakUserStorageProvider.entities.RoleEntity;
import com.bsep_sbz.KeycloakUserStorageProvider.entities.RoleMemberEntity;
import com.bsep_sbz.KeycloakUserStorageProvider.entities.UserEntity;

@Stateful
@Local(MyKeycloakUserStorageProvider.class)
public class MyKeycloakUserStorageProvider implements UserStorageProvider, UserLookupProvider, UserRegistrationProvider,
                                        UserQueryProvider, CredentialInputUpdater, CredentialInputValidator, OnUserCache {

    private static final Logger LOGGER = Logger.getLogger(MyKeycloakUserStorageProvider.class);
    private static final String PASSWORD_CACHE_KEY = UserAdapter.class.getName() + ".password";
    private static final String RELAM = "demo";

    @PersistenceContext
    protected EntityManager entityManager;

    protected ComponentModel componentModel;
    protected KeycloakSession keycloakSession;

    public void setModel(ComponentModel componentModel) {
        this.componentModel = componentModel;
    }

    public void setSession(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

    @Remove
    @Override
    public void close() {
    }

    @Override
    public UserModel getUserById(String id, RealmModel realmModel) {
        LOGGER.info("getUserById: " + id);
        String persistenceId = StorageId.externalId(id);
        UserEntity userEntity = entityManager.getReference(UserEntity.class, Long.valueOf(persistenceId));

        if (userEntity == null) {
            LOGGER.info("could not find user by id: " + id);
            return null;
        }

        return new UserAdapter(keycloakSession, realmModel, componentModel, userEntity, entityManager);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realmModel) {
        LOGGER.info("getUserByUsername: " + username);
        TypedQuery<UserEntity> query = entityManager.createNamedQuery("getUserByUsername", UserEntity.class);
        query.setParameter("username", username);
        List<UserEntity> result = query.getResultList();

        if (result.isEmpty()) {
            LOGGER.info("could not find username: " + username);
            return null;
        }

        return new UserAdapter(keycloakSession, realmModel, componentModel, result.get(0), entityManager);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realmModel) {
        TypedQuery<UserEntity> query = entityManager.createNamedQuery("getUserByEmail", UserEntity.class);
        query.setParameter("email", email);
        List<UserEntity> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        }

        return new UserAdapter(keycloakSession, realmModel, componentModel, result.get(0), entityManager);
    }

    @Override
    public UserModel addUser(RealmModel realmModel, String username) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        entityManager.persist(userEntity);
        LOGGER.info("added user: " + username);
//        realmModel.addRole(userEntity.getRoles().iterator().next().getName(), userEntity.getRoles().iterator().next().getName());
//        realmModel.addRole(userEntity.getRoles().iterator().next().getName());

        return new UserAdapter(keycloakSession, realmModel, componentModel, userEntity, entityManager);
    }

    @Override
    public boolean removeUser(RealmModel realmModel, UserModel userModel) {
        String persistenceId = StorageId.externalId(userModel.getId());
        UserEntity userEntity = entityManager.find(UserEntity.class, Long.valueOf(persistenceId));

        if (userEntity == null) {
            return false;
        }

        entityManager.remove(userEntity);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCache(RealmModel realmModel, CachedUserModel cachedUserModel, UserModel delegateUserModel) {
        String password = ((UserAdapter)delegateUserModel).getPassword();

        if (password != null) {
            cachedUserModel.getCachedWith().put(PASSWORD_CACHE_KEY, password);
        }
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return CredentialModel.PASSWORD.equals(credentialType);
    }

    @Override
    public boolean updateCredential(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        if (!supportsCredentialType(credentialInput.getType())|| !(credentialInput instanceof UserCredentialModel)) {
            return false;
        }

        UserCredentialModel userCredentialModel = (UserCredentialModel) credentialInput;
        UserAdapter userAdapter = getUserAdapter(userModel);
        userAdapter.setPassword(userCredentialModel.getValue());

        return true;
    }

    public UserAdapter getUserAdapter(UserModel userModel) {
        UserAdapter userAdapter = null;

        if (userModel instanceof CachedUserModel) {
            userAdapter = (UserAdapter) ((CachedUserModel)userModel).getDelegateForUpdate();
        } else {
            userAdapter = (UserAdapter) userModel;
        }

        return userAdapter;
    }

    @Override
    public void disableCredentialType(RealmModel realmModel, UserModel userModel, String credentialType) {
        if (!supportsCredentialType(credentialType)) return;

        getUserAdapter(userModel).setPassword(null);
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realmModel, UserModel userModel) {
        if (getUserAdapter(userModel).getPassword() != null) {
            Set<String> set = new HashSet<String>();
            set.add(CredentialModel.PASSWORD);

            return set;
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
        return supportsCredentialType(credentialType) && getPassword(userModel) != null;
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        if (!supportsCredentialType(credentialInput.getType()) || !(credentialInput instanceof UserCredentialModel)) {
            return false;
        }

        UserCredentialModel userCredentialModel = (UserCredentialModel) credentialInput;
        String password = getPassword(userModel);

        return password != null && password.equals(userCredentialModel.getValue());
    }

    public String getPassword(UserModel userModel) {
        String password = null;

        if (userModel instanceof CachedUserModel) {
            password = String.valueOf(((CachedUserModel) userModel).getCachedWith().get(PASSWORD_CACHE_KEY));
        } else if (userModel instanceof UserAdapter) {
            password = ((UserAdapter) userModel).getPassword();
        }
        return password;
    }

    @Override
    public int getUsersCount(RealmModel realmModel) {
        Object count = entityManager.createNamedQuery("getUserCount").getSingleResult();

        return ((Number) count).intValue();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realmModel) {
        return getUsers(realmModel, -1, -1);
    }

    @Override
    public List<UserModel> getUsers(RealmModel realmModel, int firstResult, int maxResults) {
        TypedQuery<UserEntity> query = entityManager.createNamedQuery("getAllUsers", UserEntity.class);
        List<UserEntity> results = query.getResultList();
        List<UserModel> users = new LinkedList<UserModel>();
        for (UserEntity userEntity : results) {
            users.add(new UserAdapter(keycloakSession, realmModel, componentModel, userEntity, entityManager));
        }

        return users;
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realmModel) {
        return searchForUser(search, realmModel, -1, -1);
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realmModel, int firstResult, int maxResults) {
        TypedQuery<UserEntity> query = entityManager.createNamedQuery("searchForUser", UserEntity.class);
        query.setParameter("search", "%" + search.toLowerCase() + "%");
        List<UserEntity> results = query.getResultList();
        List<UserModel> users = new LinkedList<UserModel>();
        for (UserEntity entity : results) {
            users.add(new UserAdapter(keycloakSession, realmModel, componentModel, entity, entityManager));
        }

        return users;
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realmModel) {
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realmModel, int firstResult, int maxResults) {
        return getUsers(realmModel, firstResult, maxResults);
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realmModel, GroupModel groupModel, int firstResult, int maxResults) {
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realmModel, GroupModel groupModel) {
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attributeName, String attributeValue, RealmModel realmModel) {
        return Collections.emptyList();
    }

    @Override
    public void preRemove(RealmModel realmModel, RoleModel roleModel) {
        removeRoleEntity(roleModel.getName());
    }

    private boolean removeRoleEntity(String roleName) {
        TypedQuery<RoleEntity> query = entityManager.createNamedQuery("getRolyByName", RoleEntity.class);
        query.setParameter("name", roleName);
        List<RoleEntity> results = query.getResultList();

        for (RoleEntity tempRole : results) {
            removeRoleMemberEntities(tempRole.getId().longValue());

            entityManager.flush();
            entityManager.remove(tempRole);
        }

        return true;
    }

    private boolean removeRoleMemberEntities(long roleMemberEntityId) {
        TypedQuery<RoleMemberEntity> queryRoles = entityManager.createNamedQuery("searchRoleForRole", RoleMemberEntity.class);
        queryRoles.setParameter("roleId", roleMemberEntityId);
        List<RoleMemberEntity> resultRoleMembers = queryRoles.getResultList();

        for (RoleMemberEntity tempRoleMember : resultRoleMembers) {
            entityManager.flush();
            entityManager.remove(entityManager.contains(tempRoleMember) ? tempRoleMember : entityManager.merge(tempRoleMember));
        }

        return true;
    }

    public void loadRoles() {
        TypedQuery<RoleEntity> query = entityManager.createNamedQuery("getAllRoles", RoleEntity.class);
        List<RoleEntity> roles = query.getResultList();
        LOGGER.info("getAllRoles:");
        for (RoleEntity role : roles) {
            LOGGER.info("Role: " + role.getName());
            /*if (keycloakSession.realms().getRealmByName("IDA").getRole(role.getName()) == null) {
                keycloakSession.realms().addRealmRole(keycloakSession.realms().getRealmByName("IDA"), KeycloakModelUtils.generateId(), role.getName());
            }*/
            if (keycloakSession.realms().getRealmByName(RELAM).getRole(role.getName()) == null) {
                keycloakSession.realms().addRealmRole(keycloakSession.realms().getRealmByName(RELAM), KeycloakModelUtils.generateId(), role.getName());
            }
        }
    }
}
