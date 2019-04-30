package com.bsep_sbz.WindowsAgent.service.interfaces;

import com.bsep_sbz.WindowsAgent.model.Addresses;

import java.io.IOException;

public interface ICommunicationConfigurationService
{
    Addresses readFromFile(String path) throws IOException;

    void writeToFile(Addresses addresses, String path) throws IOException;
}
