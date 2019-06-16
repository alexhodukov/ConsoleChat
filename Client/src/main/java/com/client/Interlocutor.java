package com.client;

public class Interlocutor {
	private int id;
	private Role role;
	private String name;
	
	public void disconnect() {
		id = 0;
		role = Role.GUEST;
		name = "";
	}
	public synchronized int getId() {
		return id;
	}
	public synchronized void setId(int id) {
		this.id = id;
	}
	public synchronized Role getRole() {
		return role;
	}
	public synchronized void setRole(Role role) {
		this.role = role;
	}
	public synchronized String getName() {
		return name;
	}
	public synchronized void setName(String name) {
		this.name = name;
	}
	
	
}
