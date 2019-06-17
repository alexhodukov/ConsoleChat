package com.chat.model;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class Agent {
	private static Logger log = Logger.getLogger(Agent.class.getName());
	
	private int id;
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
