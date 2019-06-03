package com.bsep_sbz.PKI.service;


import com.bsep_sbz.PKI.model.ApplicationAddress;
import com.bsep_sbz.PKI.repository.ApplicationAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationAddressServiceImpl implements ApplicationAddressService {

    @Autowired
    private ApplicationAddressRepository applicationAddressRepository;

    @Override
    public ApplicationAddress getApplicationAddress(String organizationalUnitName) {
        return applicationAddressRepository.findByOrganizationalUnitName(organizationalUnitName);
    }

    @Override
    public void setApplicationAddress(String organizationalUnitName, String url) {
        ApplicationAddress applicationAddress = applicationAddressRepository.findByOrganizationalUnitName(organizationalUnitName);

        boolean changed = false;

        if(applicationAddress == null) {
            applicationAddress = new ApplicationAddress(organizationalUnitName, url);
            changed = true;
        }
        else {
            if(!applicationAddress.getUrl().equals(url)) {
                applicationAddress.setUrl(url);
                changed = true;
            }
        }

        if(changed) applicationAddressRepository.save(applicationAddress);
    }
}
