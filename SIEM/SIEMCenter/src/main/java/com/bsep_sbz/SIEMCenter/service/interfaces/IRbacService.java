package com.bsep_sbz.SIEMCenter.service.interfaces;

import com.bsep_sbz.SIEMCenter.model.authentication_and_authorization_entities.PermissionEntity;
import com.bsep_sbz.SIEMCenter.model.authentication_and_authorization_entities.RoleEntity;

import java.util.List;

public interface IRbacService {

	boolean addRole(String role);
	boolean addPermission(String permissionName);
	boolean addPermissionToRole(Long roleId, Long permissionId);
	boolean addRoleToUser(String userName, String roleName);
	public boolean addUser(String username, String password, String name);


	List<RoleEntity> getRolesWithPermissions();

	boolean removePermissionFromRole(Long roleId, Long permissionId);

	List<PermissionEntity> getPermissions();
}
