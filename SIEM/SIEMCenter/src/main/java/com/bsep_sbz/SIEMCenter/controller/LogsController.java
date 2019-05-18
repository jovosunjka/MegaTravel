package com.bsep_sbz.SIEMCenter.controller;

import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import java.util.List;
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

    @RequestMapping(value = "/process", consumes = MediaType.APPLICATION_JSON_VALUE,  method = RequestMethod.POST)
    public ResponseEntity processLogs(@RequestBody List<String> logs) {
        List<Log> logRet;
        for (String log: logs) System.out.println(log);
        logRet = ruleService.makeLogs(logs);

        if(logRet == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        logRet.forEach(System.out::println);

        return new ResponseEntity(HttpStatus.OK);
    }


}
