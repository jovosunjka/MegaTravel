package com.bsep.WindowsAgent.service;

import com.bsep.WindowsAgent.service.interfaces.IModelMapperWrapper;
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
