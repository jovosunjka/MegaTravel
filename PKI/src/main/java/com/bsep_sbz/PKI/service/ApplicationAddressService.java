package com.bsep_sbz.PKI.service;

import com.bsep_sbz.PKI.model.ApplicationAddress;

public interface ApplicationAddressService {

    ApplicationAddress getApplicationAddress(String organizationalUnitName);

    void setApplicationAddress(String organizationalUnitName, String url);
}
