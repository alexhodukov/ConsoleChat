package com.chat.model;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

public class Client {
	private static Logger log = Logger.getLogger(Client.class.getName());
	
	private int id;
	private Socket socket; 
	private String name;
	private Queue<Message> listUnreadMsg;
	
	public Client(Socket socket, int id, String name) {
		this.socket = socket;
		this.id = id;
		this.name = name;
		this.listUnreadMsg = new LinkedList<>();
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
	
	public Socket getSocket() {
		return socket;
	}

	public String getName() {
		return name;
	}

	public Queue<Message> getListUnreadMsg() {
		return listUnreadMsg;
	}
	
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
		return "Client [name=" + name + "]";
	}
	
	

	
}
