package com.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class IncomingCall implements Runnable {
	private static Logger log = Logger.getLogger(IncomingCall.class.getName());
	private Server server;
	private Role role;
	private boolean done;
	private boolean isExit;
	private int id;
	
	private Socket socket;
	
	public IncomingCall(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		this.role = Role.GUEST;
		this.id = socket.getPort();
	}
	
	@Override
	public void run() {		
		try (InputStream input = socket.getInputStream();
			OutputStream output = socket.getOutputStream()) {
			
			Scanner reader = new Scanner(new InputStreamReader(input, "UTF-8"));
			
			while (reader.hasNextLine()) {				
				String line = reader.nextLine();
//				System.out.println("Line " + line);
				
				switch (role) {
				case AGENT :
				case CLIENT : {
					server.getChat().processMessage(line, id, role);
				} break;
				case GUEST : {
					Message msg = server.getMsgHandler().registerUser(id, line);
					if (!msg.isErrorMessage()) {
						role = msg.getRoleReceiver();
						server.getChat().createUser(socket, msg);
					}
					if (role == Role.CLIENT) {
						server.getChat().registerMessage(msg);	
					}
				} break;
				}
				
			}
			
			waitThread();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private synchronized void waitThread() {
		while (!isExit) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public synchronized void terminatedChat() {
		isExit = true;
		notify();
	}

	public Socket getSocket() {
		return socket;
	}

	
}
