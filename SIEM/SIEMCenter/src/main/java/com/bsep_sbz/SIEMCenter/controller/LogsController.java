package com.bsep_sbz.SIEMCenter.controller;

import com.bsep_sbz.SIEMCenter.controller.dto.MessageDto;
import com.bsep_sbz.SIEMCenter.model.sbz.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bsep_sbz.SIEMCenter.service.interfaces.IRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/logs")
public class LogsController
{
    @Autowired
    private IRuleService ruleService;

/*
    @PreAuthorize("hasAuthority('WRITE_LOG')")
    @RequestMapping(value = "/process", method = RequestMethod.POST)
    public ResponseEntity processLogs(@RequestBody MessageDto messageDto) {
        System.out.println(messageDto.getMessage());
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
    */

    @RequestMapping(value = "/process", consumes = MediaType.APPLICATION_JSON_VALUE,  method = RequestMethod.POST)
    public ResponseEntity processLogs(@RequestBody List<String> logs) {
        List<Log> logRet;
        for (String log: logs) System.out.println(log);
        logRet = ruleService.makeLogs(logs);

        if(logRet == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        logRet.stream()
                .forEach(log -> System.out.println(log));

        return new ResponseEntity(HttpStatus.OK);
    }


}
