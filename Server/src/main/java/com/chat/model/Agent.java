package com.chat.model;

import java.net.Socket;

public class Agent extends User {

	public Agent() {
	}
	
	public Agent(Socket socket, int id, String name) {
		super(socket, id, name);
	}

	@Override
	public String toString() {
		return "Agent " + super.toString();
	}
}
