package com.bsep_sbz.PKI.model;

import java.util.List;

public class ChangedTrustStoreConfig {
    private Certificate certificate; // zapravo se menjaju samo elementi trustStoreCertificates liste
    private boolean changed;

    public ChangedTrustStoreConfig() {

    }

    public ChangedTrustStoreConfig(Certificate certificate, boolean changed) {
        this.certificate = certificate;
        this.changed = changed;
    }


    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
