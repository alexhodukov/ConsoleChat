package com.server;

import java.net.Socket;
import java.util.Stack;

public class Client {
	private Socket socket; 
	private String name;
	private Stack<String> listUnreadMsg;
	
	public Client(Socket socket, String name) {
		this.socket = socket;
		this.name = name;
		this.listUnreadMsg = new Stack<>();
	}
	
	public Socket getSocket() {
		return socket;
	}

	public String getName() {
		return name;
	}

	public Stack<String> getListUnreadMsg() {
		return listUnreadMsg;
	}
	
	public boolean isEmptyListMsg() {
		return listUnreadMsg.isEmpty();
	}
	
	public String getNextUnreadMsg() {
		return listUnreadMsg.peek();
	}
	
	public void pushUnreadMsg(String msg) {
		listUnreadMsg.push(msg);
	}
}
