package com.bsep.SIEMCenter.model;

import java.util.List;

public class Role {
	
	private long id;
	private String name;
	private List<Permission> permissions;
	
	public Role(String name, List<Permission> permissions) {
		super();
		this.name = name;
		this.permissions = permissions;
	}

	public Role(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	
	
	
	

}
