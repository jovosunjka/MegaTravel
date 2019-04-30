package com.bsep_sbz.SIEMCenter.service.interfaces;

public interface IConfigurationPropertiesService
{
    String getAddress();

    String getPublishersPath();

    boolean canSubscribeToApp();

    boolean canPublishToApp();
}
