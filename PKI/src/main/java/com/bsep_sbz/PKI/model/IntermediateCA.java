package com.bsep_sbz.PKI.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
// ovom anotacijom se navodi vrednost diskriminatorske kolone koja vazi za
// objekte ove klase
@DiscriminatorValue("IM")
public class IntermediateCA extends CA {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RootCA rootCA;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Application> applications;

    public IntermediateCA(String userId, String commonName, String organizationName, String organizationalUnitName, String countryCode, String url, RootCA rootCA, List<Application> applications) {
        super(commonName, organizationName, organizationalUnitName, countryCode, url);
        this.rootCA = rootCA;
        this.applications = applications;
    }

    public IntermediateCA(String userId, String commonName, String organizationName, String organizationalUnitName, String countryCode, String url) {
        super(commonName, organizationName, organizationalUnitName, countryCode, url);
        this.applications = new ArrayList<>();
    }

    public RootCA getRootCA() {
        return rootCA;
    }

    public void setRootCA(RootCA rootCA) {
        this.rootCA = rootCA;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }
}
