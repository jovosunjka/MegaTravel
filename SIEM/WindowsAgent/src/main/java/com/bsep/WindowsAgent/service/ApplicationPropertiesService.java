package com.bsep.WindowsAgent.service;

import com.bsep.WindowsAgent.service.interfaces.IApplicationPropertiesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:application.properties")
public class ApplicationPropertiesService implements IApplicationPropertiesService
{
    @Value("${server.ssl.key-store}")
    private String keyStorePath;

    @Value("${server.ssl.key-store-password}")
    private String keyStorePassword;

    @Override
    public String getKeyStorePath() {
        return keyStorePath;
    }

    @Override
    public String getKeyStorePassword() {
        return keyStorePassword;
    }
}
