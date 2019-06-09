package com.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Client {
	private int id;
	private int idAgent;
	private Socket socket; 
	private String name;
	private Queue<String> listUnreadMsg;
	
	public Client(Socket socket, int id, String name) {
		this.socket = socket;
		this.id = id;
		this.name = name;
		this.listUnreadMsg = new LinkedList<>();
	}
	
	public void sendMessage(Message msg) {
		try {
			BufferedOutputStream bufOut = new BufferedOutputStream(socket.getOutputStream());
			System.out.println("Client.sendMessage msg " + msg.getMessage());
			bufOut.write(msg.getMessageBytes());
			bufOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Socket getSocket() {
		return socket;
	}

	public String getName() {
		return name;
	}

	public Queue<String> getListUnreadMsg() {
		return listUnreadMsg;
	}
	
	public boolean isEmptyListMsg() {
		return listUnreadMsg.isEmpty();
	}
	
	public String nextUnreadMsg() {
		return listUnreadMsg.poll();
	}
	
	public void addMsg(String msg) {
		listUnreadMsg.add(msg);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(int idAgent) {
		this.idAgent = idAgent;
	}

	@Override
	public String toString() {
		return "Client [name=" + name + "]";
	}
	
	

	
}
