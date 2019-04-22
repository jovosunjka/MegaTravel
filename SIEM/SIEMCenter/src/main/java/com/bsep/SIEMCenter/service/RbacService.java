package com.bsep.SIEMCenter.service;

import com.bsep.SIEMCenter.controller.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsep.SIEMCenter.repository.PermissionRepository;
import com.bsep.SIEMCenter.repository.RoleRepository;
import com.bsep.SIEMCenter.repository.UserRepository;
import com.bsep.SIEMCenter.model.Permission;
import com.bsep.SIEMCenter.model.Role;
import com.bsep.SIEMCenter.model.User;
import com.bsep.SIEMCenter.service.interfaces.IRbacService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RbacService implements IRbacService{
	
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	PermissionRepository permissionRepository;
	@Autowired
	UserRepository userRepository;

	public boolean addRole(String roleName) {
		Role role = new Role(roleName);
		role = roleRepository.save(role);
		if (role!=null) return true;
		return false;
		
	}

	@Override
	public boolean addPermission(String permissionName) {
		Permission permission = new Permission(permissionName);
		permission = permissionRepository.save(permission);
		if (permission!=null) return true;
		return false;
	}

	@Override
		public boolean addUser(String username, String password, String name) {
			if (username == null || name == null || password == null)
				return false;
			if (username == "" || password == "")
				return false;
			if (existsUsername(username))
				return false;
			User user = new User(name, username,password);
			try {
				userRepository.save(user);
			} catch (Exception e) {
				return false;
			}
			return true;
	}

	@Override
	public List<Role> getRolesWithPermissions() {
		return roleRepository.findAll();
	}

	@Override
	public boolean removePermissionFromRole(Long roleId, Long permissionId) {
		try {
			Role role = roleRepository.findById(roleId);
			Permission permission = null;
			for(Permission p : role.getPermissions()) {
				if(p.getId().longValue() == roleId.longValue()) {
					permission = p;
					break;
				}
			}

			role.getPermissions().remove(permission);
			roleRepository.save(role);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	@Override
	public List<Permission> getPermissions() {
		return permissionRepository.findAll();
	}


	public boolean existsUsername(String username) {

		User user = userRepository.findByUsername(username);

		return user != null;
}

	@Override
	public boolean addPermissionToRole(Long roleId, Long permissionId) {
		Role role = roleRepository.findById(roleId);
		if (role == null) return false;
		Permission permission = permissionRepository.findById(permissionId);
		if (permission == null) return false;
		role.getPermissions().add(permission);
		roleRepository.save(role);
		return true;
	}

	@Override
	public boolean addRoleToUser(String userName, String roleName) {
		Role role = roleRepository.findByName(roleName);
		if (role == null) return false;
		User user = userRepository.findByUsername(userName);
		if (user == null) return false;
		user.getRoles().add(role);
		userRepository.save(user);
		return true;
	}	

}
