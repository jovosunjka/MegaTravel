package com.bsep_sbz.SIEMCenter.controller;

import com.bsep_sbz.SIEMCenter.controller.dto.AddressDto;
import com.bsep_sbz.SIEMCenter.controller.dto.AppInfoDto;
import com.bsep_sbz.SIEMCenter.controller.dto.ErrorDto;
import com.bsep_sbz.SIEMCenter.model.Address;
import com.bsep_sbz.SIEMCenter.model.Addresses;
import com.bsep_sbz.SIEMCenter.service.interfaces.ICommunicationConfigurationService;
import com.bsep_sbz.SIEMCenter.service.interfaces.IConfigurationPropertiesService;
import com.bsep_sbz.SIEMCenter.service.interfaces.IModelMapperWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/publishers", method = RequestMethod.GET)
    public ResponseEntity getPublishers() throws  IOException
    {
        Addresses addresses = _communicationConfigurationService.readFromFile(_globalProperties.getPublishersPath());
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @RequestMapping(value = "/publishers/add", method = RequestMethod.POST)
    public ResponseEntity addPublisher(@RequestBody AddressDto addressDto) throws  IOException
    {
        if(addressDto.getAddress().equals(_globalProperties.getAddress())){
            return new ResponseEntity<>(new ErrorDto("Publisher address can not be added"), HttpStatus.BAD_REQUEST);
        }
        Addresses addresses = _communicationConfigurationService.readFromFile(_globalProperties.getPublishersPath());
        boolean addressExists = addresses.getAddresses().stream()
                .anyMatch(obj -> obj.getAddress().equals(addressDto.getAddress()));
        if(addressExists) {
            return new ResponseEntity<>(new ErrorDto("Publisher address already added"), HttpStatus.BAD_REQUEST);
        }

        Address address = _modelMapperWrapper.get().map(addressDto, Address.class);
        addresses.getAddresses().add(address);
        _communicationConfigurationService.writeToFile(addresses, _globalProperties.getPublishersPath());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/publishers/remove", method = RequestMethod.POST)
    public ResponseEntity removePublisher(@RequestBody AddressDto addressDto) throws  IOException
    {
        Addresses addresses = _communicationConfigurationService.readFromFile(_globalProperties.getPublishersPath());
        boolean removed = addresses.getAddresses().removeIf(obj -> obj.getAddress().equals(addressDto.getAddress()));
        if(!removed){
            return new ResponseEntity<>(new ErrorDto("Publisher address does not exist"), HttpStatus.BAD_REQUEST);
        }
        _communicationConfigurationService.writeToFile(addresses, _globalProperties.getPublishersPath());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
