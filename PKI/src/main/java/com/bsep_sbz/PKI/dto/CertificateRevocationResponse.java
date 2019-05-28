package com.bsep_sbz.PKI.dto;

import java.util.Date;

public class CertificateRevocationResponse {
    private boolean isRevoked;
    private Date timestamp;

    public CertificateRevocationResponse(boolean isRevoked, Date timestamp) {
        this.isRevoked = isRevoked;
        this.timestamp = timestamp;
    }

    public boolean isRevoked() {
        return isRevoked;
    }

    public void setRevoked(boolean revoked) {
        isRevoked = revoked;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
