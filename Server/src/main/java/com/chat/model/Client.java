package com.chat.model;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import com.chat.enums.CommunicationMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Client {
	private static Logger log = Logger.getLogger(Client.class.getName());
	
	private int id;
	
	@JsonIgnore
	private Socket socket; 
	private String name;
	
	@JsonIgnore
	private Queue<Message> listUnreadMsg;
	private CommunicationMethod comMethod;
	
	public Client(Socket socket, int id, String name) {
		this.id = id;
		this.name = name;
		this.listUnreadMsg = new LinkedList<>();
		this.socket = socket;
		if (socket == null) {
			this.comMethod = CommunicationMethod.WEB;
		} else {
			this.comMethod = CommunicationMethod.CONSOLE;	
		}
	}
	
	public Client(int id, String name) {
		this(null, id, name);
	}
	
	public Client() {
		
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
			log.info("Message " + msg.getMessage() + " sent to client, id " + id + ", name " + name);
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

	public Queue<Message> getListUnreadMsg() {
		return listUnreadMsg;
	}
	
	@JsonIgnore
	public boolean isEmptyListMsg() {
		return listUnreadMsg.isEmpty();
	}
	
	public Message nextUnreadMsg() {
		return listUnreadMsg.poll();
	}
	
	public void addMsg(Message msg) {
		listUnreadMsg.add(msg);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Client [id=" + id + ", name=" + name + ", listUnreadMsg=" + listUnreadMsg + ", comMethod=" + comMethod
				+ "]";
	}
	
}
