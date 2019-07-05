package com.chat.services;

import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.chat.enums.CommunicationMethod;
import com.chat.enums.MessageType;
import com.chat.enums.Role;
import com.chat.model.Agent;
import com.chat.model.Chat;
import com.chat.model.Client;
import com.chat.model.Message;
import com.chat.utils.MessageUtils;

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
	private ExecutorService execConnAgent;
	private Map<Integer, Queue<Message>> listHttpMessages;
	
	public ServiceManager() {
		this.listAgents = new HashMap<>();
		this.freeAgents = new LinkedList<>();
		this.listClients = new HashMap<>();
		this.listChats = new HashMap<>();
		this.listMessages = new LinkedList<>();
		this.execConnAgent = Executors.newCachedThreadPool();
		this.incIdAgent = new AtomicInteger(10);
		this.incIdClient = new AtomicInteger();
		this.incIdChat = new AtomicInteger();
		this.listHttpMessages = new HashMap<>();
	}
	
	
	public int createAgent(Socket socket, String name) {
		int id = incIdAgent.incrementAndGet();
		Agent agent = new Agent(socket, id, name);
		registerAgent(agent);
		return id;
	}
	
	public int createHttpAgent(String name) {
		int id = incIdAgent.incrementAndGet();
		Agent agent = new Agent(id, name);
		registerAgent(agent);
		return id;
	}
	
	public int createClient(Socket socket, String name) {
		int id = incIdClient.incrementAndGet();
		Client client = new Client(socket, id, name);
		registerClient(client);
		return id;
	}
	
	public int createHttpClient(String name) {
		int id = incIdClient.incrementAndGet();
		Client client = new Client(id, name);
		registerClient(client);
		return id;
	}
	
	public int createChat(int idClient) {
		int idChat = incIdChat.incrementAndGet();
		Chat chat = new Chat(idChat, idClient);
		listChats.put(idChat, chat);
		return idChat;
	}
	
	private synchronized void registerAgent(Agent agent) {
		listAgents.put(agent.getId(), agent);
		log.info("Registration agent " + agent);
	}
	
	public synchronized void doFreeAgent(int idAgent) {
		freeAgents.add(idAgent);
		log.info("Agent " + listAgents.get(idAgent).getName() + " is free.");
		notifyAll();
	}
	
	private synchronized void registerClient(Client client) {
		listClients.put(client.getId(), client);
		log.info("Registration client " + client);
	}
	
	public synchronized void registerMessage(Message msg) {
		int idReceiver = msg.getIdReceiver();
		int idSender = msg.getIdSender();
		Role roleSender = msg.getRoleSender();
		Role roleReceiver = msg.getRoleReceiver();
		
		if (roleSender == Role.CLIENT && idReceiver == 0) {
			listClients.get(msg.getIdSender()).addMsg(msg);
			log.info("Register message in unread listMessages " + msg.getMessage());
		} else {
			if (isEqualsCommunicationMethod(msg.getMsgType(), roleReceiver, idReceiver, CommunicationMethod.WEB)) {
				
				if (listHttpMessages.containsKey(idReceiver)) {
					listHttpMessages.get(idReceiver).add(msg);
				} else {
					Queue<Message> listMessages = new LinkedList<>();
					listMessages.add(msg);
					listHttpMessages.put(idReceiver, listMessages);
				}
				log.info("Register message in HTTP list " + msg.getMessage());
				
			} else {
				listMessages.add(msg);
				log.info("Register message in CONSOLE list " + msg.getMessage());
			}	
			notifyAll();
		}
		
	}
	
	private boolean isEqualsCommunicationMethod(MessageType msgType, Role roleReceiver, int idReceiver, CommunicationMethod method) {
		
		return 	(roleReceiver == Role.AGENT && listAgents.get(idReceiver).getComMethod() == method) ||
				(roleReceiver == Role.CLIENT && listClients.get(idReceiver).getComMethod() == method);
	}
	
	public synchronized Queue<Message> getHttpMessages(int idReceiver) {
		Queue<Message> que = new LinkedList<>();
		if (listHttpMessages.containsKey(idReceiver)) {
			que = listHttpMessages.remove(idReceiver);
		} else {
			try {
				wait(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return que;
	}
	
	public int getIdAgentByIdChat(int idChat) {
		return listChats.get(idChat).getIdAgent();
	}
	
	public boolean isExistAgentInChat(int idChat) {
		return listChats.get(idChat).getIdAgent() != 0;
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
				
				log.info("Attempt send message " + msg.getMessage());
				
				int idReceiver = msg.getIdReceiver();
				int idSender = msg.getIdSender();
				switch (msg.getRoleReceiver()) {
				case AGENT : {
					Agent agent = listAgents.get(idReceiver);
					if (agent != null) {
						agent.sendMessage(msg);
					}
				} break;
				
				case CLIENT : {
					Client client = listClients.get(idReceiver);
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
	
	public synchronized boolean isExistFreeAgents() {
		return freeAgents.size() > 0;
	}
	
	public boolean isAgentConnecting(int idChat) {
		return listChats.get(idChat).isAgentConnecting();
	}
	
	private synchronized Agent getFreeAgent() {
		try {
			while (!isExistFreeAgents()) {
				wait();	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return listAgents.get(freeAgents.poll());
	}
	
	public void connectAgent(Message msg) {
		listChats.get(msg.getIdChat()).setAgentConnecting(true);
		Runnable r = () -> {
			Agent agent = null;
			while (agent == null) {
				 agent = getFreeAgent();
			}
			
			Message msgAgentFount = MessageUtils.createMessageClientWhenWaiting(msg, MessageUtils.AGENT_FOUND);
			registerMessage(msgAgentFount);
			
			int idChat = msg.getIdChat();
			listChats.get(idChat).setIdAgent(agent.getId());
			listChats.get(idChat).setAgentConnecting(false);
			
			log.info("Starting conversation between agent " + agent.getName() + " and client " + msg.getNameSender() + ".");
			
			getUnreadMessages(idChat, agent);			
		};
		
		Thread t = new Thread(r);
		t.setName("Thread connection to agent from " + msg.getNameSender());
		execConnAgent.execute(t);
	}
	
	private void getUnreadMessages(int idChat, Agent agent) {
		Client client = listClients.get(listChats.get(idChat).getIdClient());
		while (!client.isEmptyListMsg()) {
			Message m = client.nextUnreadMsg();
			m.setIdReceiver(agent.getId());
			log.info("Unread message " + m.getMessage() + " will be registered in the general heap. Name " + listAgents.get(agent.getId()).getName());
			registerMessage(m);
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
	
	public boolean agentIsFree(int id) {
		return !listAgents.containsKey(id);
	}
}
