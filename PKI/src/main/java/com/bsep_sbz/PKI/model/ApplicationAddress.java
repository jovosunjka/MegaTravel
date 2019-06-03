package com.bsep_sbz.PKI.model;

import javax.persistence.*;

@Entity
public class ApplicationAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Long id;

    @Column(name="organizational_unit_name", unique=false, nullable=false)
    private String organizationalUnitName;

    @Column(name="url", unique=false, nullable=false)
    private String url;


    public ApplicationAddress() {

    }

    public ApplicationAddress(String organizationalUnitName, String url) {
        this.organizationalUnitName = organizationalUnitName;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
