package com.bsep.SIEMCenter.service.interfaces;

public interface IConfigurationPropertiesService
{
    String getAddress();

    String getPublishersPath();

    boolean canSubscribeToApp();

    boolean canPublishToApp();
}
