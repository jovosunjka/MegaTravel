package com.bsep_sbz.WindowsAgent.service.interfaces;

public interface IApplicationPropertiesService
{
    String getKeyStorePath();

    String getKeyStorePassword();

    String getTrustStorePath();

    String getTrustStorePassword();
}
