package com.bsep_sbz.WindowsAgent.controller;

import com.bsep_sbz.WindowsAgent.controller.dto.MessageDto;
import com.bsep_sbz.WindowsAgent.model.Address;
import com.bsep_sbz.WindowsAgent.model.Addresses;
import com.bsep_sbz.WindowsAgent.service.LogsService;
import com.bsep_sbz.WindowsAgent.service.interfaces.ICommunicationConfigurationService;
import com.bsep_sbz.WindowsAgent.service.interfaces.IConfigurationPropertiesService;
import com.bsep_sbz.WindowsAgent.service.interfaces.IModelMapperWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/logs")
public class LogsController
{
    private IConfigurationPropertiesService _globalProperties;
    private ICommunicationConfigurationService _communicationConfigurationService;
    private IModelMapperWrapper _modelMapperWrapper;
    private RestTemplate _restTemplate;
    private LogsService _logService;

    @Autowired
    public LogsController(
            IConfigurationPropertiesService globalProperties,
            ICommunicationConfigurationService communicationConfigurationService,
            IModelMapperWrapper modelMapperWrapper,
            RestTemplate restTemplate,
            LogsService logsService)
    {
        _globalProperties = globalProperties;
        _communicationConfigurationService = communicationConfigurationService;
        _modelMapperWrapper = modelMapperWrapper;
        _restTemplate = restTemplate;
        _logService = logsService;
    }
    /*

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity sendLogs(@RequestBody MessageDto message) throws IOException
    {
        System.out.println("entered");
        Addresses subscribersAddresses =
                _communicationConfigurationService.readFromFile(_globalProperties.getSubscribersPath());
        for(Address address : subscribersAddresses.getAddresses())
        {
            try {
                _restTemplate
                        .postForEntity(address.getAddress() + "/logs/process", message, String.class);
            }
            catch (Exception exception) {
                return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_GATEWAY);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/send-logs", method = RequestMethod.POST)
    public ResponseEntity<List<String>> sendLog(@RequestBody List<String> logs) throws IOException
    {
    	logs = _logService.logFilter(logs);
    	
        try {
            _restTemplate
                    .postForEntity("http://localhost:8081/logs/process", logs, List.class);
        }
        catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    */
}
