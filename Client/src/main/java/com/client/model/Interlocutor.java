package com.client.model;

import com.client.enums.Role;

public class Interlocutor {
	private int id;
	private Role role;
	private String name;
	
	public void disconnect() {
		id = 0;
		role = Role.GUEST;
		name = "";
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
