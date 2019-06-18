package com.bsep_sbz.KeycloakUserStorageProvider.entities;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({
        @NamedQuery(name = "searchRoleForUser", query = "select r from RoleMemberEntity r where r.id.userId = :userId"),
        @NamedQuery(name = "searchRoleForRole", query = "select r from RoleMemberEntity r where r.id.roleId = :roleId") })
@Entity
@Table(name = "role_member")
public class RoleMemberEntity implements Serializable {

    private static final long serialVersionUID = -3885757303982864854L;

    @EmbeddedId
    private RoleMemberIdEntity id;

    public RoleMemberEntity() {
    }

    public RoleMemberEntity(RoleMemberIdEntity id) {
        this.id = id;
    }

    public RoleMemberIdEntity getId() {
        return id;
    }

    public void setId(RoleMemberIdEntity id) {
        this.id = id;
    }
}