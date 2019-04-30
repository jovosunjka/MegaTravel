package com.bsep_sbz.SIEMCenter.model;

import javax.persistence.*;


@Entity
public class Permission {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "permission_name", unique = true, nullable = false)
	private String permissionName;

	public Permission() {

	}

	public Permission(String permissionName) {
		this.permissionName = permissionName;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	
	
	
	
}
