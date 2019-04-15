package com.bsep.SIEMCenter.service.interfaces;

public interface IRbacService {

	boolean addRole(String role);
	boolean addPermission(String permissionName);
	boolean addPermissionToRole(String roleName, String permissionName);
	boolean addRoleToUser(String userName, String roleName);
	public boolean addUser(String username, String password, String name);


}
