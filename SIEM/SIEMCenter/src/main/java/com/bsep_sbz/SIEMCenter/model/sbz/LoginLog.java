package com.bsep_sbz.SIEMCenter.model.sbz;


import com.bsep_sbz.SIEMCenter.model.sbz.enums.HostType;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


public class LoginLog extends Log {
    private HostType hostType;
    private UserAccount userAccount;
    private String ipAddress;
    private boolean successful;

    public LoginLog() {

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
        return successful;
    }

    public void setSuccessful(boolean successful) {
        successful = successful;
    }
}
