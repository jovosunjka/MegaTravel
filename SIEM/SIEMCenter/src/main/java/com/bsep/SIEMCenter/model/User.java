package com.bsep.SIEMCenter.model;

import java.util.List;

public class User {
	
	private long id;
	private String name;
	private String username;
	private String password;
	private List<Role> roles;
	
	public User(long id, String username) {
		this.id = id;
		this.username = username;
	}

	public User(String name, String username, String password) {
		this.username = username;
		this.name = name;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	
	
	
}
