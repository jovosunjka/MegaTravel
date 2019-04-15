package com.bsep.SIEMCenter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsep.SIEMCentar.service.repository.PermissionRepository;
import com.bsep.SIEMCentar.service.repository.RoleRepository;
import com.bsep.SIEMCentar.service.repository.UserRepository;
import com.bsep.SIEMCenter.model.Permission;
import com.bsep.SIEMCenter.model.Role;
import com.bsep.SIEMCenter.model.User;
import com.bsep.SIEMCenter.service.interfaces.IRbacService;

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
		role = roleRepository.saveRole(role);
		if (role!=null) return true;
		return false;
		
	}

	@Override
	public boolean addPermission(String permissionName) {
		Permission permission = new Permission(permissionName);
		permission = permissionRepository.savePermission(permission);
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


	public boolean existsUsername(String username) {

		User user = userRepository.findByUsername(username);

		return user != null;
}

	@Override
	public boolean addPermissionToRole(String roleName, String permissionName) {
		Role role = roleRepository.findByName(roleName);
		if (role == null) return false;
		Permission permission = permissionRepository.findBypermissionName(permissionName);
		if (permission == null) return false;
		role.getPermissions().add(permission);
		roleRepository.saveRole(role);
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
