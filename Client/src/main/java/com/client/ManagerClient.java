package com.client;

import java.io.IOException;
import java.net.Socket;

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
	
	public synchronized boolean isGuest() {
		return role == Role.GUEST;
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

	public synchronized int getIdChat() {
		return idChat;
	}

	public synchronized void setIdChat(int idChat) {
		this.idChat = idChat;
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized void  setName(String name) {
		this.name = name;
	}

	public synchronized Interlocutor getInterlocutor() {
		return interlocutor;
	}
}
