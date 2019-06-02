package com.server;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Logger;

public class ServiceManager {
	private static Logger log = Logger.getLogger(ServiceManager.class.getName());
	
	private Queue<ThreadAgent> listAgents;
	
	public ServiceManager() {
		this.listAgents = new LinkedList<>();
	}
	
	public synchronized void registerAgent(ThreadAgent thAgent) {
		listAgents.add(thAgent);
		notifyAll();
	}
	
	public synchronized ThreadAgent getFreeAgent() {
		try {
			while (!isExistFreeAgents()) {
				wait();	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return listAgents.poll();
	}
	
	public synchronized boolean isExistFreeAgents() {
		return listAgents.size() > 0;
	}
	
	public void connectAgent(ThreadClient thClient) {
		Runnable r = () -> {
			ThreadAgent thAgent = getFreeAgent();
			thClient.setThreadAgent(thAgent);
			thAgent.setThreadClient(thClient);
			thAgent.startConversation();
		};
		Thread t = new Thread(r);
		t.start();
	}
	
	public void disconnectAgent(ThreadClient thClient) {
		ThreadAgent thAgent = thClient.getThreadAgent();
		thAgent.setLeave(true);
		listAgents.add(thAgent);
		thClient.setThreadAgent(null);
	}
	
	public void disconnectClient(ThreadAgent thAgent) {
		ThreadClient thClient = thAgent.getThreadClient();
		thClient.setLeave(true);
	}
	
	public void createAgent(UserThreadHandler thHandler, String name, PrintWriter writer, Scanner reader) {
		Agent agent = new Agent(thHandler.getSocket(), name, writer, reader);
		registerAgent(new ThreadAgent(thHandler, agent, this));
		log.info("Registration agent " + name);
	}
	
	public void createClient(UserThreadHandler thHandler, String name, PrintWriter writer, Scanner reader) {
		Client client = new Client(thHandler.getSocket(), name, writer, reader);
		ThreadClient thClient = new ThreadClient(thHandler, client, this);
		log.info("Registration client " + name);
		thClient.waitAgent();
	}
}
