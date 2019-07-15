package com.chat.model;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.chat.enums.CommunicationMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {
	private static final Logger logger = Logger.getLogger(User.class);
	
	private int id;
	private String name;
	
	@JsonIgnore
	private Socket socket; 
	private CommunicationMethod comMethod;
	
	public User() {
	}
	
	public User(Socket socket, int id, String name) {
		this.id = id;
		this.name = name;
		this.socket = socket;
		if (socket == null) {
			this.comMethod = CommunicationMethod.WEB;
		} else {
			this.comMethod = CommunicationMethod.CONSOLE;	
		}
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public CommunicationMethod getComMethod() {
		return comMethod;
	}
	public void setComMethod(CommunicationMethod comMethod) {
		this.comMethod = comMethod;
	}

	@JsonIgnore
	public void sendMessage(Message msg) {
		try {
			if (msg.getComMethod() == CommunicationMethod.CONSOLE && comMethod == CommunicationMethod.WEB) {
				msg.convertToWeb();
			}
			if (msg.getComMethod() == CommunicationMethod.WEB && comMethod == CommunicationMethod.CONSOLE) {
				msg.convertToConsole();
			}
			
			BufferedOutputStream bufOut = new BufferedOutputStream(socket.getOutputStream());
			bufOut.write(msg.getMessageBytes());
			bufOut.flush();
			logger.info("Message " + msg.getMessage() + " sent to id " + id + ", name " + name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", comMethod=" + comMethod + "]";
	}
}
