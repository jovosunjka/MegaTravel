package com.bsep_sbz.SIEMCenter.service;

import com.bsep_sbz.SIEMCenter.model.authentication_and_authorization_entities.PermissionEntity;
import com.bsep_sbz.SIEMCenter.model.authentication_and_authorization_entities.RoleEntity;
import com.bsep_sbz.SIEMCenter.model.authentication_and_authorization_entities.UserEntity;
import com.bsep_sbz.SIEMCenter.repository.PermissionRepository;
import com.bsep_sbz.SIEMCenter.repository.RoleRepository;
import com.bsep_sbz.SIEMCenter.repository.UserRepository;
import com.bsep_sbz.SIEMCenter.service.interfaces.IRbacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RbacService implements IRbacService {
	
	@Autowired
    RoleRepository roleRepository;
	@Autowired
    PermissionRepository permissionRepository;
	@Autowired
    UserRepository userRepository;

	public boolean addRole(String roleName) {
		RoleEntity role = new RoleEntity(roleName);
		role = roleRepository.save(role);
		if (role!=null) return true;
		return false;
		
	}

	@Override
	public boolean addPermission(String permissionName) {
		PermissionEntity permission = new PermissionEntity(permissionName);
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
			UserEntity user = new UserEntity(username,password);
			try {
				userRepository.save(user);
			} catch (Exception e) {
				return false;
			}
			return true;
	}

	@Override
	public List<RoleEntity> getRolesWithPermissions() {
		return roleRepository.findAll();
	}

	@Override
	public boolean removePermissionFromRole(Long roleId, Long permissionId) {
		try {
			RoleEntity role = roleRepository.findById(roleId);
			PermissionEntity permission = null;
			for(PermissionEntity p : role.getPermissions()) {
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
	public List<PermissionEntity> getPermissions() {
		return permissionRepository.findAll();
	}


	public boolean existsUsername(String username) {

		UserEntity user = userRepository.findByUsername(username);

		return user != null;
}

	@Override
	public boolean addPermissionToRole(Long roleId, Long permissionId) {
		RoleEntity role = roleRepository.findById(roleId);
		if (role == null) return false;
		PermissionEntity permission = permissionRepository.findById(permissionId);
		if (permission == null) return false;
		role.getPermissions().add(permission);
		roleRepository.save(role);
		return true;
	}

	@Override
	public boolean addRoleToUser(String userName, String roleName) {
		RoleEntity role = roleRepository.findByName(roleName);
		if (role == null) return false;
		UserEntity user = userRepository.findByUsername(userName);
		if (user == null) return false;
		user.getRoles().add(role);
		userRepository.save(user);
		return true;
	}	

}
