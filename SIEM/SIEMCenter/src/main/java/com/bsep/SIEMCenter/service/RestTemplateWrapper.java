package com.bsep.SIEMCenter.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestTemplateWrapper implements IRestTemplateWrapper {

    private RestTemplate restTemplate;

    public RestTemplate get() {
        if(restTemplate == null) {
            restTemplate = new RestTemplate();
        }

        return  restTemplate;
    }
}
