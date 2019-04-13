package com.bsep.PKI.model;

public class OrganizationalUnit {
    private String userId;
    private String name;
    private String organizationName;
    private String countryCode;




    public OrganizationalUnit() {

    }

    public OrganizationalUnit(String userId, String name, String organizationName, String countryCode) {
        this.userId = userId;
        this.name = name;
        this.organizationName = organizationName;
        this.countryCode = countryCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
