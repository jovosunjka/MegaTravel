package com.bsep_sbz.WindowsAgent.controller.dto;

public class AppInfoDto
{
    private String address;
    private boolean canSubscribeToApp;
    private boolean canPublishToApp;

    public AppInfoDto() { }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isCanSubscribeToApp() {
        return canSubscribeToApp;
    }

    public void setCanSubscribeToApp(boolean canSubscribeToApp) {
        this.canSubscribeToApp = canSubscribeToApp;
    }

    public boolean isCanPublishToApp() {
        return canPublishToApp;
    }

    public void setCanPublishToApp(boolean canPublishToApp) {
        this.canPublishToApp = canPublishToApp;
    }
}
