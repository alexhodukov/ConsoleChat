package com.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class UserThreadHandler implements Runnable {
	private static Logger log = Logger.getLogger(UserThreadHandler.class.getName());
	
	private ServiceManager manager;
	private boolean done;
	private boolean isExit;
	
	private static final String REGISTER = "/register";
	private static final String AGENT = "agent";
	private static final String CLIENT = "client";
	
	private Socket socket;
	
	public UserThreadHandler(ServiceManager manager, Socket socket) {
		this.manager = manager;
		this.socket = socket;
	}
	
	@Override
	public void run() {		
		try (InputStream input = socket.getInputStream();
			OutputStream output = socket.getOutputStream()) {
			
			Scanner reader = new Scanner(input, "UTF-8");
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
			writer.println("Please, register as agent or client: /register agent(client)");
			String name = "";
			while (!done && reader.hasNextLine()) {
				String line = reader.nextLine();
				String[] tokens = line.split(" ");

				if (tokens.length == 3 && REGISTER.equals(tokens[0])) {
					switch (tokens[1]) {
					case AGENT : {
						name = tokens[2];
						manager.createAgent(this, name, writer, reader);
						log.info("Created agent with name is " + name);
						done = true;
					} break;
					case CLIENT : {
						name = tokens[2];
						manager.createClient(this, name, writer, reader);
						log.info("Created client with name is " + name);
						done = true;
					} break;
					default : {
						sendErrorRegistration(writer);
					} break;
					}					
				} else {
					sendErrorRegistration(writer);
				}
			}
			waitThread();
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
	
	private void sendErrorRegistration(PrintWriter writer) {
		writer.println("Unsupported command, please, write the command correctly!");
	}
	
	public ServiceManager getManager() {
		return manager;
	}

	public Socket getSocket() {
		return socket;
	}

	
}
