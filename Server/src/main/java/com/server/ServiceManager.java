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

public class ServiceManager {
	private static Logger log = Logger.getLogger(ServiceManager.class.getName());
	private AtomicInteger incIdAgent;
	private AtomicInteger incIdClient;
	private AtomicInteger incIdChat;
	private Map<Integer, Agent> listAgents;
	private Queue<Integer> freeAgents;
	private Map<Integer, Client> listClients;
	private Map<Integer, Chat> listChats;
	private Queue<Message> listMessages;
	private ExecutorService execMessage;
	private ExecutorService execConnAgent;
	
	public ServiceManager() {
		this.listAgents = new HashMap<>();
		this.freeAgents = new LinkedList<>();
		this.listClients = new HashMap<>();
		this.listChats = new HashMap<>();
		this.listMessages = new LinkedList<>();
		this.execMessage = Executors.newFixedThreadPool(5);
		this.execConnAgent = Executors.newCachedThreadPool();
		this.incIdAgent = new AtomicInteger();
		this.incIdClient = new AtomicInteger();
		this.incIdChat = new AtomicInteger();
	}
	
	
	public int createAgent(Socket socket, String name) {
		int id = incIdAgent.incrementAndGet();
		Agent agent = new Agent(socket, id, name);
		registerAgent(agent);
		return id;
	}
	
	public int createClient(Socket socket, String name) {
		int id = incIdClient.incrementAndGet();
		Client client = new Client(socket, id, name);
		registerClient(client);
		return id;
	}
	
	public int createChat(int idClient) {
		int idChat = incIdChat.incrementAndGet();
		Chat chat = new Chat(idChat, idClient);
		listChats.put(idChat, chat);
		return idChat;
	}
	
	public synchronized void registerAgent(Agent agent) {
		listAgents.put(agent.getId(), agent);
		log.info("Registration agent " + agent);
	}
	
	public synchronized void doFreeAgent(int idAgent) {
		freeAgents.add(idAgent);
		log.info("Agent " + listAgents.get(idAgent).getName() + " is free.");
		notifyAll();
	}
	
	public synchronized void registerClient(Client client) {
		listClients.put(client.getId(), client);
		log.info("Registration client " + client);
	}
	
	public synchronized void registerMessage(Message msg) {
		if (msg.getRoleSender() == Role.CLIENT && msg.getIdReceiver() == 0) {
			listClients.get(msg.getIdSender()).addMsg(msg);
			log.info("Register message in unread listMessages" + msg.getMessage());
		} else {
			listMessages.add(msg);
			log.info("Register message in queue" + msg.getMessage());
			notifyAll();	
		}
	}
	
	public int getIdAgentByIdChat(int idChat) {
		return listChats.get(idChat).getIdAgent();
	}
	
	public int getIdAgentInChat(int idChat) {
		return listChats.get(idChat).getIdAgent();
	}
	
	public synchronized void sendMessage() {
		try {
			while (listMessages.isEmpty()) {
				wait();	
			}
			
			if (!listMessages.isEmpty()) {
				Message msg = listMessages.poll();
				
				log.info("sending message " + msg.getMessage());
				
				int id = msg.getIdReceiver();
				switch (msg.getRoleReceiver()) {
				case AGENT : {
					Agent agent = listAgents.get(id);
					if (agent != null) {
						agent.sendMessage(msg);
					}
				} break;
				case CLIENT : {
					Client client = listClients.get(id);
					if (client != null) {
						client.sendMessage(msg);
					}
				} break;
				}
			}
				
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public synchronized Agent getFreeAgent() {
		try {
			while (!isExistFreeAgents()) {
				wait();	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return listAgents.get(freeAgents.poll());
	}
	
	public synchronized boolean isExistFreeAgents() {
		return freeAgents.size() > 0;
	}
	
	public boolean isAgentConnecting(int idChat) {
		return listChats.get(idChat).isAgentConnecting();
	}
	
	public void connectAgent(Message msg) {
		listChats.get(msg.getIdChat()).setAgentConnecting(true);
		Runnable r = () -> {
			Agent agent = null;
			while (agent == null) {
				 agent = getFreeAgent();
			}
			
			int idChat = msg.getIdChat();
			listChats.get(idChat).setIdAgent(agent.getId());
			listChats.get(idChat).setAgentConnecting(false);
			
			log.info("Starting conversation between agent " + agent.getName() + " and client " + msg.getNameSender() + ".");
			
			Client client = listClients.get(listChats.get(idChat).getIdClient());
			while (!client.isEmptyListMsg()) {
				Message m = client.nextUnreadMsg();
				m.setIdReceiver(agent.getId());
				log.info("Unread message " + m.getMessage() + " ---- sending agent " + listAgents.get(agent.getId()).getName());
				registerMessage(m);
			}
			
		};
		execConnAgent.execute(r);
	}
	
	public void getUnreadMessag(int idChat) {
		int idClient = listChats.get(idChat).getIdClient();
		int idAgent = listChats.get(idChat).getIdAgent();
		Client client = listClients.get(idClient);
		while (!client.isEmptyListMsg()) {
			Message m = client.nextUnreadMsg();
			registerMessage(m);
			log.info("Unread message " + m + " ---- sent agent " + listAgents.get(idAgent).getName());
		}
	}
	
	public void leaveConversation(int idUser, Role role, int idChat) {
		if (role == Role.AGENT) {
			doFreeAgent(listAgents.get(idUser).getId());
		} 
		if (role == Role.CLIENT) {
			int idAgent = listChats.get(idChat).getIdAgent();
			doFreeAgent(listAgents.get(idAgent).getId());
		}
		listChats.get(idChat).disconnectAgent();
	}
	
	public void exit(int idUser, Role role, int idChat) {
		if (role == Role.AGENT) {
			listAgents.remove(idUser);
			listChats.get(idChat).disconnectAgent();
		}
		if (role == Role.CLIENT) {
			listClients.remove(idUser);
			int idAgent = listChats.get(idChat).getIdAgent();
			doFreeAgent(listAgents.get(idAgent).getId());
		}
		
	}
	
	private void sendMessageError(Socket socket, Message msg) {
		
	}
	
	public boolean agentIsFree(int id) {
		return !listAgents.containsKey(id);
	}
}
