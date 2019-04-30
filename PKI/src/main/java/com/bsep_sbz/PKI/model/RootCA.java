package com.bsep_sbz.PKI.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
// ovom anotacijom se navodi vrednost diskriminatorske kolone koja vazi za
// objekte ove klase
@DiscriminatorValue("ROOT")
public class RootCA extends CA {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<IntermediateCA> cas;

    public RootCA(String commonName, String organizationName, String organizationalUnitName, String countryCode, String url, List<IntermediateCA> cas) {
        super(commonName, organizationName, organizationalUnitName, countryCode, url);
        this.cas = cas;
    }

    public RootCA(String commonName, String organizationName, String organizationalUnitName, String countryCode, String url) {
        super(commonName, organizationName, organizationalUnitName, countryCode, url);
        this.cas = new ArrayList<>();
    }

    public List<IntermediateCA> getCas() {
        return cas;
    }

    public void setCas(List<IntermediateCA> cas) {
        this.cas = cas;
    }
}
