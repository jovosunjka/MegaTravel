package com.bsep.SIEMCenter.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;


@Entity
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@JoinTable(name = "role_permissions",
			joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id"))
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Permission> permissions;


	public Role() {

	}

	public Role(String name) {
		this.name = name;
		this.permissions = new ArrayList<Permission>();
	}

	public Role(String name, List<Permission> permissions) {
		this.name = name;
		this.permissions = permissions;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

}
