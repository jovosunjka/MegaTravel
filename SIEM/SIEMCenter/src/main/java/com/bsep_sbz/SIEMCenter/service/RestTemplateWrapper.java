package com.bsep_sbz.SIEMCenter.service;

import com.bsep_sbz.SIEMCenter.service.interfaces.IRestTemplateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestTemplateWrapper implements IRestTemplateWrapper {

    private RestTemplate restTemplate;

    @Override
    public RestTemplate get() {
        if(restTemplate == null) {
            restTemplate = new RestTemplate();
        }

        return  restTemplate;
    }
}
