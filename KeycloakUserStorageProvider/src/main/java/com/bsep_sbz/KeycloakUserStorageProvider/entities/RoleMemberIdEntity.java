package com.bsep_sbz.KeycloakUserStorageProvider.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable // ova anotacija se stavlja kad neka klasa nema smisla bez druge klase u kojoj se nalazi kao referenca
public class RoleMemberIdEntity implements Serializable {

    private static final long serialVersionUID = 1176436230685269789L;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "user_id")
    private Long userId;

    public RoleMemberIdEntity() {
    }

    public RoleMemberIdEntity(Long roleId, Long userId) {
        this.roleId = roleId;
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        RoleMemberIdEntity roleMemberId = (RoleMemberIdEntity) obj;
        return roleId.longValue() == roleMemberId.roleId.longValue() && userId.longValue() == roleMemberId.userId.longValue();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + roleId.hashCode() + userId.hashCode();
        return result;
    }
}