package com.bsep_sbz.WindowsAgent.service;

import com.bsep_sbz.WindowsAgent.service.interfaces.IConfigurationPropertiesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:configuration.properties")
public class ConfigurationPropertiesService implements IConfigurationPropertiesService {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${server.port}")
    private String port;

    @Value("${base-url}")
    private String baseUrl;

    @Value("${subscribers-path}")
    private String subscribersPath;

    @Value("${canSubscribeToApp}")
    private boolean canSubscribeToApp;

    @Value("${canPublishToApp}")
    private boolean canPublishToApp;

    @Override
    public String getAddress() {
        return baseUrl + port + contextPath;
    }

    @Override
    public String getSubscribersPath() {
        return subscribersPath;
    }

    @Override
    public boolean canSubscribeToApp() {
        return canSubscribeToApp;
    }

    @Override
    public boolean canPublishToApp() {
        return canPublishToApp;
    }
}

