package com.server;

import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Chat {
	private static Logger log = Logger.getLogger(Chat.class.getName());
	private AtomicInteger incIdAgent;
	private AtomicInteger incIdClient;
	private MessageHandler msgHandler;
	private Map<Integer, Agent> listAgents;
	private Queue<Agent> freeAgents;
	private Map<Integer, Client> listClients;
	private Queue<Message> listMessages;
	private ExecutorService execMessage;
	private ExecutorService execConnAgent;
	
	public Chat(MessageHandler msgHandler) {
		this.msgHandler = msgHandler;
		this.listAgents = new HashMap<>();
		this.freeAgents = new LinkedList<>();
		this.listClients = new HashMap<>();
		this.listMessages = new LinkedList<>();
		this.execMessage = Executors.newFixedThreadPool(5);
		this.execMessage = Executors.newCachedThreadPool();
	}
	
	public synchronized void registerMessage(Message message) {
		listMessages.add(message);
		notifyAll();
	}
	
	public void processMessage(IncomingCall incoming, String src, int id) {
		Message msg = msgHandler.processMessage(id, src);

		registerMessage(msg);
	}
	
	public synchronized void sendMessage() {
		try {
			while (listMessages.isEmpty()) {
				wait();	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Runnable r = () -> {
			Message msg = listMessages.poll();
			System.out.println("msg " + msg);
			int id = msg.getIdReceiver();
			switch (msg.getRole()) {
			case AGENT : {
				listAgents.get(id).sendMessage(msg);
			} break;
			case CLIENT : {
				listClients.get(id).sendMessage(msg);
			} break;
			}
		};
		execMessage.execute(r);
	}
	
	public synchronized void registerAgent(Agent agent) {
		freeAgents.add(agent);
		notifyAll();
	}
	
	public synchronized void registerClient(Client client) {
		listClients.put(client.getId(), client);
	}
	
	public synchronized Agent getFreeAgent() {
		try {
			while (!isExistFreeAgents()) {
				wait();	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return freeAgents.poll();
	}
	
	public synchronized boolean isExistFreeAgents() {
		return freeAgents.size() > 0;
	}
	
	public void createUser(Socket socket, Message msg) {
		switch (msg.getRole()) {
		case CLIENT : {
			createClient(socket, msg.getIdReceiver(), msg.getName());
		} break;
		case AGENT : {
			createAgent(socket, msg.getIdReceiver(), msg.getName());
		} break;
		}
	}
	
	public void createAgent(Socket socket, int id, String name) {
		Agent agent = new Agent(socket, id, name);
//		agent.setId(incIdAgent.incrementAndGet());
		registerAgent(agent);
		log.info("Registration agent " + name);
	}
	
	public void createClient(Socket socket, int id, String name) {
		Client client = new Client(socket, id, name);
//		client.setId(incIdClient.incrementAndGet());
		registerClient(client);
		log.info("Registration client " + name);
	}
	
	public void connectAgent(int idClient, Message msg) {
		Runnable r = () -> {
			Agent agent = getFreeAgent();
			listAgents.put(agent.getId(), agent);
			Client client = listClients.get(idClient);
			client.setIdAgent(agent.getId());
			agent.setIdClient(client.getId());
			agent.sendMessage(msg);
			log.info("Starting conversation between agent " + agent.getName() + " and client " + client.getName() + ".");
		};
		execConnAgent.execute(r);
	}
	
	public void disconnectAgent(int idClient) {
		Client client = listClients.get(idClient);
		client.setIdAgent(0);
		int idAgent = client.getIdAgent();
		Agent agent = listAgents.get(idAgent);
		agent.setIdClient(0);
		registerAgent(agent);
		listAgents.remove(idAgent);
	}
	
	public void disconnectClient(int idClient) {
		Client client = listClients.get(idClient);
		client.setIdAgent(0);
		int idAgent = client.getIdAgent();
		Agent agent = listAgents.get(idAgent);
		agent.setIdClient(0);
		registerAgent(agent);
		listAgents.remove(idAgent);
	}
	
	private void sendMessageError(Socket socket, Message msg) {
		
	}
}
