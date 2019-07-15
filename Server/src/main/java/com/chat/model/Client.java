package com.chat.model;

import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Client extends User {	
	@JsonIgnore
	private Queue<Message> listUnreadMsg;
	
	public Client() {
	}
	
	public Client(Socket socket, int id, String name) {
		super(socket, id, name);
		this.listUnreadMsg = new LinkedList<>();
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

	@Override
	public String toString() {
		return "Client " + super.toString() + "[listUnreadMsg=" + listUnreadMsg + "]";
	}
}
