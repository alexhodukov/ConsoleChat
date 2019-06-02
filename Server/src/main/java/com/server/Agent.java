package com.server;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Agent {
	private int id;
	private Socket socket; 
	private String name;
	private boolean isFree;
	private PrintWriter writer;
	private Scanner reader;
	
	public Agent(Socket socket, String name, PrintWriter writer, Scanner reader) {
		this.socket = socket;
		this.name = name;
		this.isFree = true;
		this.writer = writer;
		this.reader = reader;
	}
	

	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	public String getName() {
		return name;
	}
	
	public Socket getSocket() {
		return socket;
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


	@Override
	public String toString() {
		return name;
	}

	
	
}
