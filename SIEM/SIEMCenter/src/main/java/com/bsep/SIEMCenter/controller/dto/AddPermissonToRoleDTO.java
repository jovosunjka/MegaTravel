package com.bsep.SIEMCenter.controller.dto;



public class AddPermissonToRoleDTO {
    private Long roleId;
    private Long permissionId;

    public AddPermissonToRoleDTO() {

    }

    public AddPermissonToRoleDTO(Long roleId, Long permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }
}
