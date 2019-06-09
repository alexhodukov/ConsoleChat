package com.client;

import java.io.IOException;
import java.net.Socket;

public class ManagerClient {
	private Socket socket;
	private int id;
	private Role role;
	
	public ManagerClient(Socket socket) {
		this.socket = socket;
		this.role = Role.GUEST;
	}
	
	public boolean isGuest() {
		return role == Role.GUEST;
	}
	
	public void terminateChat() {
		try {
			if (!socket.isClosed()) {
				socket.close();
			}
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	
}
