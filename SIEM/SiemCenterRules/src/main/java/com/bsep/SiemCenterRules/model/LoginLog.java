package com.bsep.SiemCenterRules.model;

import com.bsep.SiemCenterRules.model.enums.HostType;
import org.kie.api.definition.type.Role;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

//@org.kie.api.definition.type.Role(Role.Type.EVENT)
//@org.kie.api.definition.type.Timestamp("timestamp")
public class LoginLog extends Log implements Serializable {
    private HostType hostType;
    private UserAccount userAccount;
    private String ipAddress;
    private boolean isSuccessful;

    public long getDaysOfInactivity() {
        return ChronoUnit.DAYS.between(timestamp, LocalDateTime.now());
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
