package com.bsep.SIEMCenter.service.interfaces;

import com.bsep.SIEMCenter.model.Permission;
import com.bsep.SIEMCenter.model.Role;

import java.util.List;

public interface IRbacService {

	boolean addRole(String role);
	boolean addPermission(String permissionName);
	boolean addPermissionToRole(Long roleId, Long permissionId);
	boolean addRoleToUser(String userName, String roleName);
	public boolean addUser(String username, String password, String name);


	List<Role> getRolesWithPermissions();

	boolean removePermissionFromRole(Long roleId, Long permissionId);

	List<Permission> getPermissions();
}
