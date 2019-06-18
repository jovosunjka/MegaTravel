package com.bsep_sbz.SIEMCenter.model.authentication_and_authorization_entities;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

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