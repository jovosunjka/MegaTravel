package com.bsep_sbz.SIEMCenter.controller;

import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.bsep_sbz.SIEMCenter.service.interfaces.ILogsService;
import com.bsep_sbz.SIEMCenter.service.interfaces.IRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private ILogsService logsService;

    @RequestMapping(value = "/process", consumes = MediaType.APPLICATION_JSON_VALUE,  method = RequestMethod.POST)
    public ResponseEntity processLogs(@RequestBody List<String> logs){
        List<Log> logRet = ruleService.makeLogs(logs);
        if(logRet == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(logRet.size() == 0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        logRet.forEach(System.out::println);
        logsService.save(logRet);
        return new ResponseEntity(HttpStatus.OK);
    }


}
