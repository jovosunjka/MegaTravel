package com.bsep.WindowsAgent.service.interfaces;

public interface IConfigurationPropertiesService
{
    String getAddress();

    String getSubscribersPath();

    boolean canSubscribeToApp();

    boolean canPublishToApp();
}
