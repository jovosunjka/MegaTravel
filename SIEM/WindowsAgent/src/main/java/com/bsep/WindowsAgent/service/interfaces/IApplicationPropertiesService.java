package com.bsep.WindowsAgent.service.interfaces;

public interface IApplicationPropertiesService
{
    String getKeyStorePath();

    String getKeyStorePassword();

    String getTrustStorePath();

    String getTrustStorePassword();
}
