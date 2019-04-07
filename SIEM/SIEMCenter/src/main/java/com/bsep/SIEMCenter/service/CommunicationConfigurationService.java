package com.bsep.SIEMCenter.service;

import com.bsep.SIEMCenter.model.Addresses;
import com.bsep.SIEMCenter.service.interfaces.ICommunicationConfigurationService;
import com.bsep.SIEMCenter.service.interfaces.IJsonServiceWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class CommunicationConfigurationService implements ICommunicationConfigurationService
{
    private IJsonServiceWrapper _jsonServiceWrapper;

    @Autowired
    public CommunicationConfigurationService(IJsonServiceWrapper jsonServiceWrapper) {
        _jsonServiceWrapper = jsonServiceWrapper;
    }

    @Override
    public Addresses readFromFile(String path) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(path)));
        return _jsonServiceWrapper.get().readValue(content, Addresses.class);
    }

    @Override
    public void writeToFile(Addresses addresses, String path) throws IOException {
        String content = _jsonServiceWrapper.get().writeValueAsString(addresses);
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(content);
        writer.close();
    }
}
