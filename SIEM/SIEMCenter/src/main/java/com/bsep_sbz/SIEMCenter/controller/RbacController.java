package com.bsep_sbz.SIEMCenter.controller;

import com.bsep_sbz.SIEMCenter.controller.dto.AddPermissonToRoleDTO;
import com.bsep_sbz.SIEMCenter.controller.dto.UserDTO;
import com.bsep_sbz.SIEMCenter.model.authentication_and_authorization_entities.PermissionEntity;
import com.bsep_sbz.SIEMCenter.model.authentication_and_authorization_entities.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bsep_sbz.SIEMCenter.service.interfaces.IRbacService;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/rbac")
public class RbacController {

	@Autowired
	IRbacService iRbacService;

	@PreAuthorize("hasAuthority('ADD_ROLE_TO_USER')")
	@RequestMapping(value = "/add-role-to-user", method = RequestMethod.POST)
	public ResponseEntity addRoleToUser(@RequestParam("username") String username,
			@RequestParam("roleName") String roleName) {
		try {
			boolean retValue = iRbacService.addRoleToUser(username, roleName);

			if (retValue)
				return new ResponseEntity<Boolean>(retValue, HttpStatus.CREATED);

			return new ResponseEntity<Boolean>(retValue, HttpStatus.OK);

		}

		catch (Exception e) {

			return new ResponseEntity<Boolean>(HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAuthority('ADD_PERMISSION_TO_ROLE')")
	@RequestMapping(value = "/add-permission-to-role", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity addPermissionToRole(@RequestBody AddPermissonToRoleDTO dto) {
		try {
			boolean retValue = iRbacService.addPermissionToRole(dto.getRoleId(), dto.getPermissionId());

			if (retValue)
				return new ResponseEntity(HttpStatus.CREATED);

			return new ResponseEntity(HttpStatus.BAD_REQUEST);

		}

		catch (Exception e) {

			return new ResponseEntity<Boolean>(HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAuthority('ADD_REMOVE_PERMISSION_TO_ROLE')")
	@RequestMapping(value = "/remove-permission-to-role/{roleId}/{permissionId}", method = RequestMethod.DELETE)
	public ResponseEntity addPermissionToRole(@PathVariable("roleId") Long roleId, @PathVariable("permissionId") Long permissionId) {
		try {
			boolean retValue = iRbacService.removePermissionFromRole(roleId, permissionId);

			if (retValue)
				return new ResponseEntity(HttpStatus.OK);

			return new ResponseEntity(HttpStatus.BAD_REQUEST);

		}

		catch (Exception e) {

			return new ResponseEntity<Boolean>(HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAuthority('ADD_ROLE_AND_PERMISSION')")
	@RequestMapping(value = "/add-role", method = RequestMethod.POST)
	public ResponseEntity<Boolean> addRole(@RequestParam("roleName") String roleName) {
		try {
			boolean retValue = iRbacService.addRole(roleName);

			if (retValue)
				return new ResponseEntity<Boolean>(retValue, HttpStatus.CREATED);

			return new ResponseEntity<Boolean>(retValue, HttpStatus.OK);

		}

		catch (Exception e) {

			return new ResponseEntity<Boolean>(HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAuthority('ADD_ROLE_AND_PERMISSION')")
	@RequestMapping(value = "/add-permission", method = RequestMethod.POST)
	public ResponseEntity<Boolean> addPermission(@RequestParam("permissionName") String permissionName) {
		try {
			boolean retValue = iRbacService.addRole(permissionName);

			if (retValue)
				return new ResponseEntity<Boolean>(retValue, HttpStatus.CREATED);

			return new ResponseEntity<Boolean>(retValue, HttpStatus.OK);

		}

		catch (Exception e) {

			return new ResponseEntity<Boolean>(HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAuthority('CREATE_ADMINISTRATOR')")
	@RequestMapping(value = "/add_admin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> addAdmin(@RequestBody UserDTO userDTO) {
		try {
			boolean retValue = iRbacService.addUser(userDTO.getUsername(), userDTO.getPassword(), userDTO.getName());
			if (retValue)
				return new ResponseEntity<Boolean>(retValue, HttpStatus.CREATED);
			return new ResponseEntity<Boolean>(retValue, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Boolean>(HttpStatus.BAD_REQUEST);
		}

	}


	@PreAuthorize("hasAuthority('READ_ROLES_AND_PERMISSIONS')")
	@RequestMapping(value = "/get_roles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<RoleEntity>> getRoles() {
		try {
			List<RoleEntity> roles = iRbacService.getRolesWithPermissions();
			return new ResponseEntity<List<RoleEntity>>(roles, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<List<RoleEntity>>(HttpStatus.BAD_REQUEST);
		}

	}

	@PreAuthorize("hasAuthority('READ_ROLES_AND_PERMISSIONS')")
	@RequestMapping(value = "/get_permissions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PermissionEntity>> getPermissions() {
		try {
			List<PermissionEntity> permissions = iRbacService.getPermissions();
			return new ResponseEntity<List<PermissionEntity>>(permissions, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<List<PermissionEntity>>(HttpStatus.BAD_REQUEST);
		}

	}


}
