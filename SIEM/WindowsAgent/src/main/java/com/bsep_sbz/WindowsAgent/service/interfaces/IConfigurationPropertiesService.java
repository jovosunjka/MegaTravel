package com.bsep_sbz.WindowsAgent.service.interfaces;

public interface IConfigurationPropertiesService
{
    String getAddress();

    String getSubscribersPath();

    boolean canSubscribeToApp();

    boolean canPublishToApp();
}
