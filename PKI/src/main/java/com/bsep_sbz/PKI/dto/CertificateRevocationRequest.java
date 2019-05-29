package com.bsep_sbz.PKI.dto;

import java.util.Date;

public class CertificateRevocationRequest {
    private long serialNumber;
    private Date timestamp;

    public long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
