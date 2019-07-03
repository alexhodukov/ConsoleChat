package com.chat.model;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import com.chat.enums.CommunMethod;

public class Client {
	private static Logger log = Logger.getLogger(Client.class.getName());
	
	private int id;
	private Socket socket; 
	private String name;
	private Queue<Message> listUnreadMsg;
	private CommunMethod comMethod;
	
	public Client(Socket socket, int id, String name) {
		this(id, name);
		this.socket = socket;
	}
	
	public Client(int id, String name) {
		this.id = id;
		this.name = name;
		this.listUnreadMsg = new LinkedList<>();
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
