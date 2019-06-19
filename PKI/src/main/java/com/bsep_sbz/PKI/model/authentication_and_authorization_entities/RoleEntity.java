package com.bsep_sbz.PKI.model.authentication_and_authorization_entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


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
