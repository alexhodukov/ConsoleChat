package com.client.model;

import java.io.IOException;
import java.net.Socket;

import com.client.enums.Role;

public class ManagerClient {
	private Socket socket;
	private int id;
	private int idChat;
	private String name;
	private Role role;
	private Interlocutor interlocutor;
	
	public ManagerClient(Socket socket) {
		this.socket = socket;
		this.role = Role.GUEST;
		this.interlocutor = new Interlocutor();
	}

	public void disconnectInterlucutor() {
		interlocutor.disconnect();
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

	public int getIdChat() {
		return idChat;
	}

	public void setIdChat(int idChat) {
		this.idChat = idChat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Interlocutor getInterlocutor() {
		return interlocutor;
	}
}
