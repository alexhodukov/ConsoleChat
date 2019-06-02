package com.server;

import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Logger;

public class ThreadClient {
	private static Logger log = Logger.getLogger(ThreadClient.class.getName());
	
	private UserThreadHandler thHandler;
	private Client client;
	private ThreadAgent thAgent;
	private ServiceManager manager;
	private boolean isExit;
	private boolean isLeave;
	
	public ThreadClient(UserThreadHandler thHandler, Client client, ServiceManager manager) {
		this.thHandler = thHandler;
		this.client = client;
		this.manager = manager;
	}
	
	public void waitAgent() {
		try {			
			Scanner reader = client.getReader();
			PrintWriter writerClient = client.getWriter();
				
			while (!isExit && reader.hasNextLine()) {
				String line = reader.nextLine();
				if ("/exit".equals(line)) {
					isExit = true;
					log.info("Client " + client.getName() + " without waiting exited conversation.");
				} else {
					client.addMsg((client.getName() + " : " + line));
					sendMesage(writerClient, "Searching free agent...");
					manager.connectAgent(this);
					startConversation();
					if (isLeave) {
						isLeave = false;
						manager.disconnectAgent(this);
					}
				}
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void startConversation() {
		try {
			Scanner reader = client.getReader();

			while (!isExit && !isLeave && reader.hasNextLine()) {
				String line = reader.nextLine();
	
				switch (line) {
				case "/exit" : {
					isExit = true;
					log.info("Client " + client.getName() + " exited conversation.");
				} break;
				case "/leave" : {
					isLeave = true;
					log.info("Client " + client.getName() + " left conversation.");
				} break;
				default : {
					if (thAgent != null) {
						sendMesage(thAgent.getAgent().getWriter(), client.getName() + " : " + line);	
					} else {
						client.addMsg((client.getName() + " : " + line));
					}
				} break;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void sendMesage(PrintWriter writer, String msg) {
		writer.println(msg);
	}
	
	public void setThreadAgent(ThreadAgent thAgent) {
		this.thAgent = thAgent;
	}
	
	public ThreadAgent getThreadAgent() {
		return thAgent;
	}

	public Client getClient() {
		return client;
	}

	public boolean isExit() {
		return isExit;
	}

	public void setExit(boolean isExit) {
		this.isExit = isExit;
	}

	public boolean isLeave() {
		return isLeave;
	}

	public void setLeave(boolean isLeave) {
		this.isLeave = isLeave;
	}
	
	
}
