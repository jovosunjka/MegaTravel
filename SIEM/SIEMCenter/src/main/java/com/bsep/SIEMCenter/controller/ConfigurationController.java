package com.bsep.SIEMCenter.controller;

import com.bsep.SIEMCenter.controller.dto.AddressDto;
import com.bsep.SIEMCenter.controller.dto.ErrorDto;
import com.bsep.SIEMCenter.model.Address;
import com.bsep.SIEMCenter.model.Addresses;
import com.bsep.SIEMCenter.service.interfaces.ICommunicationConfigurationService;
import com.bsep.SIEMCenter.service.interfaces.IGlobalProperties;
import com.bsep.SIEMCenter.service.interfaces.IModelMapperWrapper;
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
    private IGlobalProperties _globalProperties;
    private ICommunicationConfigurationService _communicationConfigurationService;
    private IModelMapperWrapper _modelMapperWrapper;

    @Autowired
    public ConfigurationController(
            IGlobalProperties globalProperties,
            ICommunicationConfigurationService communicationConfigurationService,
            IModelMapperWrapper modelMapperWrapper)
    {
        _globalProperties = globalProperties;
        _communicationConfigurationService = communicationConfigurationService;
        _modelMapperWrapper = modelMapperWrapper;
    }

    @RequestMapping(value = "/address", method = RequestMethod.GET)
    public ResponseEntity getAddress()
    {
        String address = _globalProperties.getAddress();
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @RequestMapping(value = "/subscribers", method = RequestMethod.GET)
    public ResponseEntity getSubscribers() throws  IOException
    {
        Addresses addresses = _communicationConfigurationService.readFromFile(_globalProperties.getSubscribersPath());
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @RequestMapping(value = "/publishers", method = RequestMethod.GET)
    public ResponseEntity getPublishers() throws  IOException
    {
        Addresses addresses = _communicationConfigurationService.readFromFile(_globalProperties.getPublishersPath());
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
        return new ResponseEntity<>(HttpStatus.OK);
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
        return new ResponseEntity<>(HttpStatus.OK);
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
