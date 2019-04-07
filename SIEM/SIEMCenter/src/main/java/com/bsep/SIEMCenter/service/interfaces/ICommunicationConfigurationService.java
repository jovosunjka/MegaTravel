package com.bsep.SIEMCenter.service.interfaces;

import com.bsep.SIEMCenter.model.Addresses;

import java.io.IOException;

public interface ICommunicationConfigurationService
{
    Addresses readFromFile(String path) throws IOException;

    void writeToFile(Addresses addresses, String path) throws IOException;
}
