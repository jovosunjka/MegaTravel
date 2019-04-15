package com.bsep.SIEMCenter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bsep.SIEMCenter.controller.dto.UserDTO;
import com.bsep.SIEMCenter.service.interfaces.IRbacService;

@RestController
@RequestMapping(value = "/rbac")
public class RbacController {

	@Autowired
	IRbacService iRbacService;

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

	@RequestMapping(value = "/add-permission-to-role", method = RequestMethod.POST)
	public ResponseEntity addPermissionToRole(@RequestParam("roleName") String roleName,
			@RequestParam("permissionName") String permissionName) {
		try {
			boolean retValue = iRbacService.addPermissionToRole(roleName, permissionName);

			if (retValue)
				return new ResponseEntity<Boolean>(retValue, HttpStatus.CREATED);

			return new ResponseEntity<Boolean>(retValue, HttpStatus.OK);

		}

		catch (Exception e) {

			return new ResponseEntity<Boolean>(HttpStatus.BAD_REQUEST);
		}
	}

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
	
	
	

}
