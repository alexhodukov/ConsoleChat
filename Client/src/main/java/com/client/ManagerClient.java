package com.client;

import java.io.IOException;
import java.net.Socket;

public class ManagerClient {
	private Socket socket;
	private int id;
	private Type type;
	
	public ManagerClient(Socket socket) {
		this.socket = socket;
		this.type = Type.GUEST;
	}
	
	public boolean isGuest() {
		return type == Type.GUEST;
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
}
