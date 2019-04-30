package com.bsep_sbz.PKI.model;


import javax.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Long id;

    @Column(name="username", unique=true, nullable=false)

    private String username;
    @Column(name="password", unique=false, nullable=false)
    private String password;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CA ca;

    public User() {

    }

    public User(String username, String password, CA ca) {
        this.username = username;
        this.password = password;
        this.ca = ca;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CA getCa() {
        return ca;
    }

    public void setCa(CA ca) {
        this.ca = ca;
    }
}
