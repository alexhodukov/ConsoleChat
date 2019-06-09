package com.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Agent {
	private int id;
	private int idClient;
	private Socket socket; 
	private String name;
	
	public Agent(Socket socket, int id, String name) {
		this.socket = socket;
		this.id = id;
		this.name = name;
	}
	
	public void sendMessage(Message msg) {
		try {	
			BufferedOutputStream bufOut = new BufferedOutputStream(socket.getOutputStream());
			bufOut.write(msg.getMessage());
			bufOut.flush();
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

	public int getIdClient() {
		return idClient;
	}

	public void setIdClient(int idClient) {
		this.idClient = idClient;
	}

	public String getName() {
		return name;
	}
	
	public Socket getSocket() {
		return socket;
	}

	@Override
	public String toString() {
		return name;
	}

	
	
}
