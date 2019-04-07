package com.bsep.WindowsAgent.controller;

import com.bsep.WindowsAgent.service.interfaces.IRestTemplateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/logs")
public class LogsController {

    private IRestTemplateWrapper _restTemplateWrapper;

    @Autowired
    public  LogsController(IRestTemplateWrapper restTemplateWrapper) {
        _restTemplateWrapper = restTemplateWrapper;
    }

    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    public ResponseEntity receiveLogs() {
        String fooResourceUrl = "http://localhost:8080/spring-rest/foos";
        ResponseEntity<String> response
                = _restTemplateWrapper.get().getForEntity(fooResourceUrl + "/1", String.class);
        HttpStatus status = response.getStatusCode();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
