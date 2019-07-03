package com.chat.model;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import com.chat.enums.CommunMethod;

public class Agent {
	private static Logger log = Logger.getLogger(Agent.class.getName());
	
	private int id;
	private Socket socket; 
	private String name;
	private CommunMethod comMethod;
	
	public Agent(Socket socket, int id, String name) {
		this(id, name);
		this.socket = socket;
	}
	
	public Agent(int id, String name) {
		this.id = id;
		this.name = name;
		if (socket == null) {
			this.comMethod = CommunMethod.WEB;
		} else {
			this.comMethod = CommunMethod.CONSOLE;	
		}
	}

	public CommunMethod getComMethod() {
		return comMethod;
	}


	public void setComMethod(CommunMethod comMethod) {
		this.comMethod = comMethod;
	}


	public void sendMessage(Message msg) {
		try {	
			BufferedOutputStream bufOut = new BufferedOutputStream(socket.getOutputStream());
			bufOut.write(msg.getMessageBytes());
			bufOut.flush();
			log.info("Msg " + msg + "---------------- sent");
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
		return name;
	}

	
	
}
