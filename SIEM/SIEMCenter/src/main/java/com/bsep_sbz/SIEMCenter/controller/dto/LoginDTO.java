package com.bsep_sbz.SIEMCenter.controller.dto;

import com.bsep_sbz.SIEMCenter.model.User;

public class LoginDTO {

    private String username;
    private String password;

    public LoginDTO() {

    }

    public LoginDTO(User user) {
        this(user.getUsername(), user.getPassword());
    }

    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}