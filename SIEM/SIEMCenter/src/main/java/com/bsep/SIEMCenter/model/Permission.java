package com.bsep.SIEMCenter.model;

public class Permission {
	
	private long id;
	private String permissionName;
	
	public Permission(String permissionName) {
		super();
		this.permissionName = permissionName;
	}
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	
	
	
	
}
