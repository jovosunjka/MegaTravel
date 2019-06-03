package com.bsep_sbz.SIEMCenter.controller;

import com.bsep_sbz.SIEMCenter.controller.dto.FilterDto;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;

import java.util.*;
import com.bsep_sbz.SIEMCenter.service.interfaces.ILogsService;
import com.bsep_sbz.SIEMCenter.service.interfaces.IRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/filter", consumes = MediaType.APPLICATION_JSON_VALUE,  method = RequestMethod.POST)
    public ResponseEntity<Page<Log>> getFilteredLogs(@RequestBody FilterDto filterDto, Pageable pageable) {
        Page<Log> logs = logsService.getFilteredLogs(filterDto.getColumn(), filterDto.getRegExp(), pageable);
        if(logs == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }


}
