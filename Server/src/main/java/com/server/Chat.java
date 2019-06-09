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
		this.execConnAgent = Executors.newCachedThreadPool();
	}
	
	public synchronized void registerMessage(Message message) {
		listMessages.add(message);
		log.info("Register message " + message);
		notifyAll();
	}
	
	public void processMessage(String src, int id, Role role) {
		Message msg = msgHandler.processMessage(src);
		String name = "";
		switch (role) {
		case AGENT : {
			int idReceiver = listAgents.get(id).getIdClient();
			System.out.println("Chat.processMessage().idReceiver " + idReceiver);
			name = listAgents.get(id).getName();
			msg.setIdReceiver(idReceiver);
			msg.setRoleReceiver(Role.CLIENT);
			msgHandler.addNameSender(msg, name);
			registerMessage(msg);
		} break;
		case CLIENT : {
			name = listClients.get(id).getName();
			msg.setRoleReceiver(Role.AGENT);
			int idAgent = listClients.get(id).getIdAgent();
			if (idAgent == 0) {
				connectAgent(id, msg);
			} else {
				msg.setIdReceiver(idAgent);
				msgHandler.addNameSender(msg, name);
				registerMessage(msg);
			}
		} break;
		}
		
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
//			System.out.println("msg " + msg);
			int id = msg.getIdReceiver();
			switch (msg.getRoleReceiver()) {
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
		log.info("Registration Agent " + agent);
		notifyAll();
	}
	
	public synchronized void registerClient(Client client) {
		listClients.put(client.getId(), client);
		log.info("Registration Client " + client);
	}
	
	public synchronized Agent getFreeAgent() {
		try {
			while (!isExistFreeAgents()) {
				wait();	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("Getting Free Agent " + freeAgents.peek());
		return freeAgents.poll();
	}
	
	public synchronized boolean isExistFreeAgents() {
		return freeAgents.size() > 0;
	}
	
	public void createUser(Socket socket, Message msg) {
		switch (msg.getRoleReceiver()) {
		case CLIENT : {
			createClient(socket, msg.getIdReceiver(), msg.getNameSender());
		} break;
		case AGENT : {
			createAgent(socket, msg.getIdReceiver(), msg);
		} break;
		}
	}
	
	public void createAgent(Socket socket, int id, Message msg) {
		Agent agent = new Agent(socket, id, msg.getNameSender());
		registerAgent(agent);
		agent.sendMessage(msg);
//		log.info("Registration agent " + msg.getNameSender());
	}
	
	public void createClient(Socket socket, int id, String name) {
		Client client = new Client(socket, id, name);
		registerClient(client);
//		log.info("Registration client " + name);
	}
	
	public void connectAgent(int idClient, Message msg) {
		Runnable r = () -> {
			Agent agent = getFreeAgent();
		
			msg.setIdReceiver(agent.getId());
			listAgents.put(agent.getId(), agent);
			Client client = listClients.get(idClient);
			client.setIdAgent(agent.getId());
			agent.setIdClient(client.getId());
			agent.sendMessage(msg);
			log.info("Starting conversation between agent " + agent.getName() + " and client " + client.getName() + ".");
			log.info("... AGENT... idAgent " + agent.getId() + ", idClient " + agent.getIdClient());
			log.info("... CLIENT... idClient " + client.getId() + ", idAgent " + client.getIdAgent());
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
	
	public boolean agentIsFree(int id) {
		return !listAgents.containsKey(id);
	}
}
