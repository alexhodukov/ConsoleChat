package com.chat.model;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import com.chat.enums.CommunicationMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Agent {
	private static Logger log = Logger.getLogger(Agent.class.getName());
	
	private int id;
	
	@JsonIgnore
	private Socket socket; 
	private String name;
	
	private CommunicationMethod comMethod;
	
	public Agent(Socket socket, int id, String name) {
		this.id = id;
		this.name = name;
		this.socket = socket;
		if (socket == null) {
			this.comMethod = CommunicationMethod.WEB;
		} else {
			this.comMethod = CommunicationMethod.CONSOLE;	
		}
	}
	
	public Agent(int id, String name) {
		this(null, id, name);
	}

	public CommunicationMethod getComMethod() {
		return comMethod;
	}


	public void setComMethod(CommunicationMethod comMethod) {
		this.comMethod = comMethod;
	}


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
			log.info("Message " + msg + " sent to agent, id " + id + ", name " + name);
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

	public String getName() {
		return name;
	}
	
	public Socket getSocket() {
		return socket;
	}

	@Override
	public String toString() {
		return "Agent [id=" + id + ", name=" + name + ", comMethod=" + comMethod + "]";
	}

}
