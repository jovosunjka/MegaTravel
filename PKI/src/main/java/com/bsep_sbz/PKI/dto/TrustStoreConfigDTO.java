package com.bsep_sbz.PKI.dto;

import java.util.List;

public class TrustStoreConfigDTO {
    private String organizationalUnitName;
    private List<String> trustStoreCertificateOrganizationalUnitNames;


    public TrustStoreConfigDTO() {

    }

    public TrustStoreConfigDTO(String organizationalUnitName, List<String> trustStoreCertificateOrganizationalUnitNames) {
        this.organizationalUnitName = organizationalUnitName;
        this.trustStoreCertificateOrganizationalUnitNames = trustStoreCertificateOrganizationalUnitNames;
    }

    public String getOrganizationalUnitName() {
        return organizationalUnitName;
    }

    public void setOrganizationalUnitName(String organizationalUnitName) {
        this.organizationalUnitName = organizationalUnitName;
    }

    public List<String> getTrustStoreCertificateOrganizationalUnitNames() {
        return trustStoreCertificateOrganizationalUnitNames;
    }

    public void setTrustStoreCertificateOrganizationalUnitNames(List<String> trustStoreCertificateOrganizationalUnitNames) {
        this.trustStoreCertificateOrganizationalUnitNames = trustStoreCertificateOrganizationalUnitNames;
    }
}
