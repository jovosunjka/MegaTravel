package com.bsep.WindowsAgent.service.interfaces;

public interface IGlobalProperties
{
    String getAddress();

    String getSubscribersPath();

    boolean canSubscribeToApp();

    boolean canPublishToApp();
}
