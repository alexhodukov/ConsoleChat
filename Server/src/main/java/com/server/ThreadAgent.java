package com.server;

import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Logger;

public class ThreadAgent {
	private static Logger log = Logger.getLogger(ThreadAgent.class.getName());
	
	private UserThreadHandler thHandler;
	private Agent agent;
	private ThreadClient thClient;
	private ServiceManager manager;
	private boolean isExit;
	private boolean isLeave;
	
	public ThreadAgent(UserThreadHandler thHandler, Agent agent, ServiceManager manager) {
		this.thHandler = thHandler;
		this.agent = agent;
		this.manager = manager;
	}
	
	public void startConversation() {
		try {
			log.info("Starting conversation between agent " + agent.getName() + " and client " + thClient.getClient().getName() + ".");
			isExit = false;
			isLeave = false;
			
			Scanner reader = agent.getReader();
			PrintWriter writerAgent = agent.getWriter();
			PrintWriter writerClient = thClient.getClient().getWriter();
			
			sendMesage(writerClient, "Found agent is " + agent.getName());
			
			while (!thClient.getClient().isEmptyListMsg()) {
				sendMesage(writerAgent, thClient.getClient().nextUnreadMsg());
			}
			
			while (!isExit && !isLeave && reader.hasNextLine()) {
				if (!isExit && !isLeave) {
					String line = reader.nextLine();
					switch (line) {
					case "/exit" : {
						isExit = true;
						log.info("Agent " + agent.getName() + " exited conversation.");
					} break;
					case "/leave" : {
						isLeave = true;
						log.info("Agent " + agent.getName() + " left conversation.");
					} break;
					default : {
						sendMesage(thClient.getClient().getWriter(), "agent " + agent.getName() + " : " + line);
					} break;
					}
				}
				
			}
			manager.disconnectClient(this);
			if (isExit) {
				thHandler.terminatedChat();
			}
			if (isLeave) {
				manager.registerAgent(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 		
	}
	
	private void sendMesage(PrintWriter writer, String msg) {
		writer.println(msg);
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public ThreadClient getThreadClient() {
		return thClient;
	}

	public void setThreadClient(ThreadClient thClient) {
		this.thClient = thClient;
	}

	public boolean isLeave() {
		return isLeave;
	}

	public void setLeave(boolean isLeave) {
		this.isLeave = isLeave;
	}
	
	
}
