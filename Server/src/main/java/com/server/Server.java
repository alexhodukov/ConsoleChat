package com.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Server {
	
	private Queue<Agent> listAgents;
	
	public Agent agent;
	public Client client;
	
	public Server() {
		this.listAgents = new LinkedList<>();
	}

	public void start() {
		try (ServerSocket s = new ServerSocket(8282)) {
			while (true) {
				Socket incoming = s.accept();
				Runnable r = new UserThreadHandler(this, incoming);
				Thread t = new Thread(r);
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void registerAgent(Agent agent) {
		listAgents.add(agent);
	}
	
	public Agent getFreeAgent() {
		return listAgents.poll();
	}
	
	public boolean isExistFreeAgents() {
		return listAgents.size() > 0;
	}

}
