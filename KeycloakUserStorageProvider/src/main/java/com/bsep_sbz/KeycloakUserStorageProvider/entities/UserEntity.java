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

@NamedQueries({
        @NamedQuery(name = "getUserByUsername", query = "select u from UserEntity u where u.username = :username"),
        @NamedQuery(name = "getUserByEmail", query = "select u from UserEntity u where u.email = :email"),
        @NamedQuery(name = "getUserCount", query = "select count(u) from UserEntity u"),
        @NamedQuery(name = "getAllUsers", query = "select u from UserEntity u"),
        @NamedQuery(name = "searchForUser", query = "select u from UserEntity u where "
                + "( lower(u.username) like :search or u.email like :search ) order by u.username"),
        @NamedQuery(name = "getUserById", query = "select u from UserEntity u where u.id = :id") })
@Entity
@Table(name = "user")
public class UserEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstname;

    @Column(name = "last_name")
    private String lastname;

    @Column(name = "email")
    private String email;

    /*@Column(name = "phone")
    private String phone;

    @Column(name = "representative_id")
    private int representativeId;

    @Column(name = "headquarter_id")
    private int headquarterId;

    @Column(name = "chief_Id")
    private int chiefId;*/

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_member", joinColumns = {
            @JoinColumn(name = "user_id", nullable = false, updatable = false) }, inverseJoinColumns = {
            @JoinColumn(name = "role_id", nullable = false, updatable = false) })
    private Set<RoleEntity> roles = new HashSet<RoleEntity>();


    public UserEntity() {

    }

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
        this.firstname = "";
        this.lastname = "";
        this.email = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /*
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRepresentativeId() {
        return representativeId;
    }

    public void setRepresentativeId(int representativeId) {
        this.representativeId = representativeId;
    }

    public int getHeadquarterId() {
        return headquarterId;
    }

    public void setHeadquarterId(int headquarterId) {
        this.headquarterId = headquarterId;
    }

    public int getChiefId() {
        return chiefId;
    }

    public void setChiefId(int chiefId) {
        this.chiefId = chiefId;
    }
    */

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }
}