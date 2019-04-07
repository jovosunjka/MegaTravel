package com.bsep.SIEMCenter.service;

import com.bsep.SIEMCenter.service.interfaces.IJsonServiceWrapper;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class JsonServiceWrapper implements IJsonServiceWrapper {

    private ObjectMapper objectMapper;

    @Override
    public ObjectMapper get() {
        if(objectMapper == null) {
            objectMapper = new ObjectMapper();
        }

        return objectMapper;
    }
}
