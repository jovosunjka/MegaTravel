package com.bsep_sbz.WindowsAgent.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/bez-veze")
public class BezVezeController {


    @RequestMapping(value = "/poruka", method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> poruka(@RequestBody String poruka) {
        System.out.println(poruka);
        return new ResponseEntity<String>("Cao, cao", HttpStatus.OK);
    }
}
