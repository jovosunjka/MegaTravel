package com.bsep_sbz.PKI.model;

import javax.persistence.*;

@Entity
public class FtpApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Long id;

    @Column(name="organizational_unit_name", unique=true, nullable=false)
    private String organizationalUnitName;

    @Column(name="host", unique=false, nullable=true)
    private String host;

    @Column(name="port", unique=false, nullable=true)
    private int port;

    @Column(name="username", unique=false, nullable=true)
    private String username;

    @Column(name="password", unique=false, nullable=true)
    private String password;


    public FtpApplication() {

    }

    public FtpApplication(String organizationalUnitName, String host, int port, String username, String password) {
        this.organizationalUnitName = organizationalUnitName;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganizationalUnitName() {
        return organizationalUnitName;
    }

    public void setOrganizationalUnitName(String organizationalUnitName) {
        this.organizationalUnitName = organizationalUnitName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

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
}
