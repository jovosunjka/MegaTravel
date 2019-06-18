package com.bsep_sbz.PKI.model.authentication_and_authorization_entities;

import javax.persistence.*;


@Entity
@Table(name = "permission")
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "permission_name", unique = true, nullable = false)
    private String permissionName;

    public PermissionEntity() {

    }

    public PermissionEntity(String permissionName) {
        this.permissionName = permissionName;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

}



