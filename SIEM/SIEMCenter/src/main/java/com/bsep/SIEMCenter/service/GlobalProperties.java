package com.bsep.SIEMCenter.service;

import com.bsep.SIEMCenter.service.interfaces.IGlobalProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:application.properties")
public class GlobalProperties implements IGlobalProperties {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${server.port}")
    private String port;

    @Value("${base-url}")
    private String baseUrl;

    @Value("${publishers-path}")
    private String publishersPath;

    @Value("${subscribers-path}")
    private String subscribersPath;

    public String getAddress() {
        return baseUrl + port + contextPath;
    }

    public String getPublishersPath() {
        return publishersPath;
    }

    public String getSubscribersPath() {
        return subscribersPath;
    }

}

