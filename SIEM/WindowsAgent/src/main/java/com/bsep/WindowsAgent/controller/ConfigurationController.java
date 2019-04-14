package com.bsep.WindowsAgent.controller;

import com.bsep.WindowsAgent.controller.dto.AddressDto;
import com.bsep.WindowsAgent.controller.dto.AppInfoDto;
import com.bsep.WindowsAgent.controller.dto.ErrorDto;
import com.bsep.WindowsAgent.model.Address;
import com.bsep.WindowsAgent.model.Addresses;
import com.bsep.WindowsAgent.service.interfaces.ICommunicationConfigurationService;
import com.bsep.WindowsAgent.service.interfaces.IConfigurationPropertiesService;
import com.bsep.WindowsAgent.service.interfaces.IModelMapperWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/configuration")
public class ConfigurationController
{
    private IConfigurationPropertiesService _globalProperties;
    private ICommunicationConfigurationService _communicationConfigurationService;
    private IModelMapperWrapper _modelMapperWrapper;

    @Autowired
    public ConfigurationController(
            IConfigurationPropertiesService globalProperties,
            ICommunicationConfigurationService communicationConfigurationService,
            IModelMapperWrapper modelMapperWrapper)
    {
        _globalProperties = globalProperties;
        _communicationConfigurationService = communicationConfigurationService;
        _modelMapperWrapper = modelMapperWrapper;
    }

    @RequestMapping(value = "/app-info", method = RequestMethod.GET)
    public ResponseEntity<AppInfoDto> getAppInfo()
    {
        AppInfoDto appInfoDto = new AppInfoDto()
        {{
            setAddress(_globalProperties.getAddress());
            setCanPublishToApp(_globalProperties.canPublishToApp());
            setCanSubscribeToApp(_globalProperties.canSubscribeToApp());
        }};
        return new ResponseEntity<>(appInfoDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/subscribers", method = RequestMethod.GET)
    public ResponseEntity getSubscribers() throws  IOException
    {
        Addresses addresses = _communicationConfigurationService.readFromFile(_globalProperties.getSubscribersPath());
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @RequestMapping(value = "/subscribers/add", method = RequestMethod.POST)
    public ResponseEntity addSubscriber(@RequestBody AddressDto addressDto) throws  IOException
    {
        if(addressDto.getAddress().equals(_globalProperties.getAddress())){
            return new ResponseEntity<>(new ErrorDto("Subscriber address can not be added"), HttpStatus.BAD_REQUEST);
        }
        Addresses addresses = _communicationConfigurationService.readFromFile(_globalProperties.getSubscribersPath());
        boolean addressExists = addresses.getAddresses().stream()
            .anyMatch(obj -> obj.getAddress().equals(addressDto.getAddress()));
        if(addressExists) {
            return new ResponseEntity<>(new ErrorDto("Subscriber address already added"), HttpStatus.BAD_REQUEST);
        }

        Address address = _modelMapperWrapper.get().map(addressDto, Address.class);
        addresses.getAddresses().add(address);
        _communicationConfigurationService.writeToFile(addresses, _globalProperties.getSubscribersPath());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/subscribers/remove", method = RequestMethod.POST)
    public ResponseEntity removeSubscriber(@RequestBody AddressDto addressDto) throws  IOException
    {
        Addresses addresses = _communicationConfigurationService.readFromFile(_globalProperties.getSubscribersPath());
        boolean removed = addresses.getAddresses().removeIf(obj -> obj.getAddress().equals(addressDto.getAddress()));
        if(!removed){
            return new ResponseEntity<>(new ErrorDto("Subscriber address does not exist"), HttpStatus.BAD_REQUEST);
        }
        _communicationConfigurationService.writeToFile(addresses, _globalProperties.getSubscribersPath());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
