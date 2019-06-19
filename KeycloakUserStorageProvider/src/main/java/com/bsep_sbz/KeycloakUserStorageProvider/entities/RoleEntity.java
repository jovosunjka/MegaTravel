package com.bsep_sbz.KeycloakUserStorageProvider.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({ @NamedQuery(name = "getAllRoles", query = "select u from RoleEntity u"),
        @NamedQuery(name = "getRolyByName", query = "select u from RoleEntity u where name = :name")
})
@Entity
@Table(name = "role")
public class RoleEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_members", joinColumns = {
            @JoinColumn(name = "role_id", nullable = false, updatable = false) }, inverseJoinColumns = {
            @JoinColumn(name = "user_id", nullable = false, updatable = false) })
    private Set<UserEntity> user = new HashSet<UserEntity>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id"))
    private Set<PermissionEntity> permissions = new HashSet<PermissionEntity>();


    public RoleEntity() {

    }

    public RoleEntity(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserEntity> getUser() {
        return user;
    }

    public void setUser(Set<UserEntity> user) {
        this.user = user;
    }

    public Set<PermissionEntity> getPermissions() { return permissions; }

    public void setPermissions(Set<PermissionEntity> permissions) { this.permissions = permissions; }
}
