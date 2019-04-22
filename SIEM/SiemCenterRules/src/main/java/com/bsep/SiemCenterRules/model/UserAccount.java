package com.bsep.SiemCenterRules.model;


import com.bsep.SiemCenterRules.model.enums.AccountType;
import com.bsep.SiemCenterRules.model.enums.RiskLevel;

import java.util.Date;

public class UserAccount {
    private String username;
    private Date lastLoginTimestamp;
    private RiskLevel riskLevel;
    private AccountType accountType;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getLastLoginTimestamp() {
        return lastLoginTimestamp;
    }

    public void setLastLoginTimestamp(Date lastLoginTimestamp) {
        this.lastLoginTimestamp = lastLoginTimestamp;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
