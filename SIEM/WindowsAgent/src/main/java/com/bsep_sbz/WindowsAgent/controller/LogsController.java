package com.bsep_sbz.WindowsAgent.controller;

import com.bsep_sbz.WindowsAgent.controller.dto.MessageDto;
import com.bsep_sbz.WindowsAgent.model.Address;
import com.bsep_sbz.WindowsAgent.model.Addresses;
import com.bsep_sbz.WindowsAgent.service.interfaces.ICommunicationConfigurationService;
import com.bsep_sbz.WindowsAgent.service.interfaces.IConfigurationPropertiesService;
import com.bsep_sbz.WindowsAgent.service.interfaces.IModelMapperWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RestController
@RequestMapping(value = "/logs")
public class LogsController
{
    private IConfigurationPropertiesService _globalProperties;
    private ICommunicationConfigurationService _communicationConfigurationService;
    private IModelMapperWrapper _modelMapperWrapper;
    private RestTemplate _restTemplate;

    @Autowired
    public LogsController(
            IConfigurationPropertiesService globalProperties,
            ICommunicationConfigurationService communicationConfigurationService,
            IModelMapperWrapper modelMapperWrapper,
            RestTemplate restTemplate)
    {
        _globalProperties = globalProperties;
        _communicationConfigurationService = communicationConfigurationService;
        _modelMapperWrapper = modelMapperWrapper;
        _restTemplate = restTemplate;
    }

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
}
