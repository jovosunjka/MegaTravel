package com.bsep_sbz.SIEMCenter.controller.dto;

import java.util.List;

public class RoleDTO {
    private String name;
    private List<String> permissions;



    public RoleDTO() {

    }

    public RoleDTO(String name, List<String> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
