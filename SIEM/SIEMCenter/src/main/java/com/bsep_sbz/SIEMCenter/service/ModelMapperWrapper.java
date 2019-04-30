package com.bsep_sbz.SIEMCenter.service;

import com.bsep_sbz.SIEMCenter.service.interfaces.IModelMapperWrapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ModelMapperWrapper implements IModelMapperWrapper
{
    private ModelMapper modelMapper;

    public ModelMapper get() {
        if(modelMapper == null) {
            modelMapper = new ModelMapper();
        }

        return modelMapper;
    }
}
