package com.bsep.SiemCenterRules.model;


import com.bsep.SiemCenterRules.model.enums.HostType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


public class LoginLog extends Log {
    private HostType hostType;
    private UserAccount userAccount;
    private String ipAddress;
    private boolean isSuccessful;

    public long getDaysOfInactivity() {
        long difference = ChronoUnit.DAYS.between(timestamp, LocalDateTime.now());
        System.out.println(difference);
        return difference;
    }

    public HostType getHostType() {
        return hostType;
    }

    public void setHostType(HostType hostType) {
        this.hostType = hostType;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }
}
