package com.server;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Client {
	private int id;
	private Socket socket; 
	private String name;
	private Queue<String> listUnreadMsg;
	private PrintWriter writer;
	private Scanner reader;
	
	public Client(Socket socket, String name, PrintWriter writer, Scanner reader) {
		this.socket = socket;
		this.name = name;
		this.listUnreadMsg = new LinkedList<>();
		this.writer = writer;
		this.reader = reader;
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

	public PrintWriter getWriter() {
		return writer;
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}

	public Scanner getReader() {
		return reader;
	}

	public void setReader(Scanner reader) {
		this.reader = reader;
	}
}
