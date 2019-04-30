package com.bsep_sbz.PKI.model;


import javax.persistence.*;


@Entity
//ovom anotacijom se naglasava tip mapiranja "jedna tabela po hijerarhiji"
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
//ovom anotacijom se navodi diskriminatorska kolona
@DiscriminatorColumn(name="type", discriminatorType= DiscriminatorType.STRING)
public abstract class CA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    protected Long id;

    @Column(name="common_name", unique=true, nullable=false)
    protected String commonName;

    @Column(name="organization_name", unique=false, nullable=false)
    protected String organizationName;

    @Column(name="organizational_unit_name", unique=true, nullable=false)
    protected String organizationalUnitName;

    @Column(name="country_code", unique=false, nullable=false)
    protected String countryCode;

    @Column(name="url", unique=false, nullable=false)
    protected String url;

    public CA() {

    }

    public CA(String commonName, String organizationName, String organizationalUnitName, String countryCode, String url) {
        this.commonName = commonName;
        this.organizationName = organizationName;
        this.organizationalUnitName = organizationalUnitName;
        this.countryCode = countryCode;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationalUnitName() {
        return organizationalUnitName;
    }

    public void setOrganizationalUnitName(String organizationalUnitName) {
        this.organizationalUnitName = organizationalUnitName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
