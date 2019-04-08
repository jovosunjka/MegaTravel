package com.bsep.SIEMCenter.service.interfaces;

public interface IGlobalProperties
{
    String getAddress();

    String getPublishersPath();

    boolean canSubscribeToApp();

    boolean canPublishToApp();
}
