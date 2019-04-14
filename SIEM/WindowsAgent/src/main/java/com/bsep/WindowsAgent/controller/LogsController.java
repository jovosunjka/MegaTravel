package com.bsep.WindowsAgent.controller;

import com.bsep.WindowsAgent.controller.dto.MessageDto;
import com.bsep.WindowsAgent.model.Address;
import com.bsep.WindowsAgent.model.Addresses;
import com.bsep.WindowsAgent.service.interfaces.ICommunicationConfigurationService;
import com.bsep.WindowsAgent.service.interfaces.IConfigurationPropertiesService;
import com.bsep.WindowsAgent.service.interfaces.IModelMapperWrapper;
import com.bsep.WindowsAgent.service.interfaces.IRestTemplateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = "/logs")
public class LogsController
{
    private IConfigurationPropertiesService _globalProperties;
    private ICommunicationConfigurationService _communicationConfigurationService;
    private IModelMapperWrapper _modelMapperWrapper;
    private IRestTemplateWrapper _restTemplateWrapper;

    @Autowired
    public LogsController(
            IConfigurationPropertiesService globalProperties,
            ICommunicationConfigurationService communicationConfigurationService,
            IModelMapperWrapper modelMapperWrapper,
            IRestTemplateWrapper restTemplateWrapper)
    {
        _globalProperties = globalProperties;
        _communicationConfigurationService = communicationConfigurationService;
        _modelMapperWrapper = modelMapperWrapper;
        _restTemplateWrapper = restTemplateWrapper;
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity sendLogs(@RequestBody MessageDto message) throws IOException
    {
        Addresses subscribersAddresses =
                _communicationConfigurationService.readFromFile(_globalProperties.getSubscribersPath());
        for(Address address : subscribersAddresses.getAddresses())
        {
            try {
                _restTemplateWrapper.get()
                        .postForEntity(address.getAddress() + "/logs/process", message, String.class);
            }
            catch (Exception exception) {
                return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_GATEWAY);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
