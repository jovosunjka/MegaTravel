package com.bsep_sbz.SIEMCenter.controller;

import com.bsep_sbz.SIEMCenter.controller.dto.FilterDto;
import com.bsep_sbz.SIEMCenter.controller.dto.LoginTemplateDto;
import com.bsep_sbz.SIEMCenter.controller.dto.PageableDto;
import com.bsep_sbz.SIEMCenter.helper.ValidationException;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Alarm;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import java.io.IOException;
import java.util.*;
import com.bsep_sbz.SIEMCenter.service.interfaces.ILogsService;
import com.bsep_sbz.SIEMCenter.service.interfaces.IRuleService;
import org.apache.maven.shared.invoker.MavenInvocationException;
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

    @RequestMapping(value = "/alarms", method = RequestMethod.GET)
    public ResponseEntity getAlarms(Pageable pageable) {
        Page<Alarm> alarms = logsService.getAlarms(pageable);
        if(alarms == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(alarms, HttpStatus.OK);
    }

    @RequestMapping(value = "/process", consumes = MediaType.APPLICATION_JSON_VALUE,  method = RequestMethod.POST)
    public ResponseEntity processLogs(@RequestBody List<String> logs){
        List<Log> logRet = ruleService.makeLogs(logs);
        if(logRet == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if(logRet.size() == 0){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        logsService.save(logRet);
        logsService.insertInSession(logRet);

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

    @RequestMapping(value = "/template", consumes = MediaType.APPLICATION_JSON_VALUE,  method = RequestMethod.POST)
    public ResponseEntity createNewRule(@RequestBody LoginTemplateDto loginTemplateDto)
            throws IOException, MavenInvocationException {
        try {
            logsService.generateRule(loginTemplateDto);
        } catch (ValidationException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/session", consumes = MediaType.APPLICATION_JSON_VALUE,  method = RequestMethod.POST)
    public ResponseEntity getSessionLogs(@RequestBody FilterDto filterDto, Pageable pageable)
    {
        try {
            PageableDto<Log> logs = logsService.getSessionLogs(filterDto.getColumn(), filterDto.getRegExp(),
                    pageable.getPageNumber(), pageable.getPageSize());
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
