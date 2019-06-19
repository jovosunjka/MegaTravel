package com.bsep_sbz.KeycloakUserStorageProvider.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bsep_sbz.KeycloakUserStorageProvider.entities.RoleEntity;
import org.keycloak.models.RoleContainerModel;
import org.keycloak.models.RoleModel;



public class RoleAdapter implements RoleModel {
    protected RoleEntity entity;

    public RoleAdapter(RoleEntity entity) {
        this.entity = entity;
    }

    @Override
    public String getName() {
        return entity.getName();
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void setDescription(String description) {
    }

    @Override
    public String getId() {
        return String.valueOf(entity.getId());
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public void addCompositeRole(RoleModel role) {
    }

    @Override
    public void removeCompositeRole(RoleModel role) {
    }

    @Override
    public Set<RoleModel> getComposites() {
        return null;
    }

    @Override
    public boolean isClientRole() {
        return false;
    }

    @Override
    public String getContainerId() {
        return null;
    }

    @Override
    public RoleContainerModel getContainer() {
        return null;
    }

    @Override
    public boolean hasRole(RoleModel role) {
        return false;
    }

    @Override
    public void setSingleAttribute(String name, String value) {
    }

    @Override
    public void setAttribute(String name, Collection<String> values) {
    }

    @Override
    public void removeAttribute(String name) {
    }

    @Override
    public String getFirstAttribute(String name) {
        return null;
    }

    @Override
    public List<String> getAttribute(String name) {
        return null;
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        return null;
    }
}