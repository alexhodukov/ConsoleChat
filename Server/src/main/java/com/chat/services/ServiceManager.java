package com.chat.services;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.chat.enums.CommunicationMethod;
import com.chat.enums.MessageType;
import com.chat.enums.Role;
import com.chat.model.Agent;
import com.chat.model.Chat;
import com.chat.model.Client;
import com.chat.model.Message;
import com.chat.utils.IncrementUtil;
import com.chat.utils.MessageUtils;

public class ServiceManager {
	private static final Logger logger = Logger.getLogger(ServiceManager.class);
	
	private static final int timeOutWaiting = 30_000;
	private static final int initCountFreeAgents = 50;
	private static final int initCountMessages = 200;
	private Map<Integer, Agent> listAgents;
	private Queue<Integer> freeAgents;
	private Map<Integer, Client> listClients;
	private Map<Integer, Chat> listChats;
	private Queue<Message> listMessages;
	private ExecutorService execConnAgent;
	private Map<Integer, Queue<Message>> listHttpMessages;
	
	public ServiceManager() {
		this.listAgents = Collections.synchronizedMap(new HashMap<>());
		this.freeAgents = new ArrayBlockingQueue<>(initCountFreeAgents);
		this.listClients = Collections.synchronizedMap(new HashMap<>());
		this.listChats = Collections.synchronizedMap(new HashMap<>());
		this.listMessages = new ArrayBlockingQueue<>(initCountMessages);
		this.execConnAgent = Executors.newCachedThreadPool();
		this.listHttpMessages = Collections.synchronizedMap(new HashMap<>());
	}
	
	public List<Client> getListClients() {
		List<Client> list = new ArrayList<Client>();
		synchronized (listClients) {
			for (Client a : listClients.values()) {
				list.add(a);
			}
			return list;	
		}
	}

	public List<Agent> getListAgents() {
		List<Agent> list = new ArrayList<Agent>();
		synchronized (listAgents) {
			for (Agent a : listAgents.values()) {
				list.add(a);
			}
			return list;	
		}
	}
	
	public List<Agent> getFreeAgents() {
		List<Agent> agents = new ArrayList<>();
		Integer[] listId = freeAgents.toArray(new Integer[freeAgents.size()]);
		synchronized (listAgents) {
			for (Integer id : listId) {
				if (listAgents.containsKey(id)) {
					agents.add(listAgents.get(id));	
				}
			}
		}
		return agents;
	}
	
	public List<Chat> getPublicChats() {
		List<Chat> list = new ArrayList<Chat>();
		synchronized (listChats) {
			for (Chat chat : listChats.values()) {
				if (chat.getIdAgent() > 0) {
					list.add(chat);
				}
			}
			return list;	
		}
	}
	
	public Agent getAgentById(int id) {
		return listAgents.get(id);
	}
	
	public Client getClientById(int id) {
		return listClients.get(id);
	}
	
	public Chat getChatById(int id) {
		return listChats.get(id);
	}
	
	public Integer getCountFreeAgents() {
		return getFreeAgents().size();
	}
	
	public int createAgent(Socket socket, String name) {
		int id = IncrementUtil.generateIdUser();
		Agent agent = new Agent(socket, id, name);
		registerAgent(agent);
		return id;
	}
	
	public int createHttpAgent(String name) {
		int id = createAgent(null, name);
		createListMessagesForHttpUser(id);
		return id;
	}
	
	public int createClient(Socket socket, String name) {
		int id = IncrementUtil.generateIdUser();
		Client client = new Client(socket, id, name);
		registerClient(client);
		return id;
	}
	
	public int createHttpClient(String name) {
		int id = createClient(null, name);
		createListMessagesForHttpUser(id);
		return id;
	}
	
	public void createListMessagesForHttpUser(int id) {
		Queue<Message> listMessages = new LinkedList<>();
		listHttpMessages.put(id, listMessages);
	}
	
	public int createChat(int idClient) {
		int idChat = IncrementUtil.generateIdChat();
		Chat chat = new Chat(idChat, idClient);
		listChats.put(idChat, chat);
		return idChat;
	}
	
	public void registerAgent(Agent agent) {
		listAgents.put(agent.getId(), agent);
		logger.info("Registration agent " + agent);
	}
	
	public void doFreeAgent(int idAgent) {
		synchronized (freeAgents) {
			freeAgents.add(idAgent);
			freeAgents.notifyAll();	
		}
	}
	
	public void registerClient(Client client) {
		listClients.put(client.getId(), client);
		logger.info("Registration client " + client);
	}
	
	public void registerMessage(Message msg) {
		int idReceiver = msg.getIdReceiver();
		Role roleSender = msg.getRoleSender();
		Role roleReceiver = msg.getRoleReceiver();
		
		if (roleSender == Role.CLIENT && idReceiver == 0) {
			listClients.get(msg.getIdSender()).addMsg(msg);
			logger.info("Register message in unread listMessages " + msg.getMessage());
		} else {
			if (isEqualsCommunicationMethod(msg.getMsgType(), roleReceiver, idReceiver, CommunicationMethod.WEB)) {
				Queue<Message> que = listHttpMessages.get(idReceiver);
				if (que != null) {
					synchronized (que) {
						if (que != null) {
							que.add(msg);
							logger.info("Register message in HTTP list " + msg.getMessage());
							que.notifyAll();
						}
						
					}	
				}				 
			} else {
				synchronized (listMessages) {
					listMessages.add(msg);
					logger.info("Register message in CONSOLE list " + msg.getMessage());
					listMessages.notifyAll();
				}
				
			}	
			
		}
		
	}
	
	private boolean isEqualsCommunicationMethod(MessageType msgType, Role roleReceiver, int idReceiver, CommunicationMethod method) {
		
		return 	(roleReceiver == Role.AGENT && listAgents.get(idReceiver).getComMethod() == method) ||
				(roleReceiver == Role.CLIENT && listClients.get(idReceiver).getComMethod() == method);
	}
	
	public Queue<Message> getHttpMessages(int idReceiver) {
		Queue<Message> que = listHttpMessages.get(idReceiver);
		Queue<Message> queResult = new LinkedList<>();
		if (que != null) {
			synchronized (que) {
				if (que != null) {
					if (que.isEmpty()) {
						try {
							que.wait(timeOutWaiting);
							while (!que.isEmpty()) {
								queResult.add(que.poll());
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						while (!que.isEmpty()) {
							queResult.add(que.poll());
						}
					}
					
				}
				
			}	
		}	
		return queResult;
	
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
	
	public void sendMessage() {
		synchronized (listMessages) {
			try {
				while (listMessages.isEmpty()) {
					listMessages.wait();	
				}
				 
				if (!listMessages.isEmpty()) {
					Message msg = listMessages.poll();
					
					logger.info("Attempt send message " + msg.getMessage());
					
					int idReceiver = msg.getIdReceiver();
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
		
	}
	
	public boolean isExistFreeAgents() {
		return freeAgents.size() > 0;
	}
	
	public boolean isAgentConnecting(int idChat) {
		return listChats.get(idChat).isAgentConnecting();
	}
	
	private Agent getFreeAgent() {
		synchronized (freeAgents) {
			try {
				while (!isExistFreeAgents()) {
					freeAgents.wait();	
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return listAgents.get(freeAgents.poll());	
		}
		
	}
	
	public void connectAgent(Message msg) {
		listChats.get(msg.getIdChat()).setAgentConnecting(true);
		Runnable r = () -> {
			Agent agent = null;
			while (agent == null) {
				 agent = getFreeAgent();
			}
			
			int idChat = msg.getIdChat();
			Chat chat = listChats.get(idChat);
			
			if (chat != null) {
				chat.setIdAgent(agent.getId());
				chat.setAgentConnecting(false);
				
				Message msgAgentFound = MessageUtils.createMessageClientWhenWaiting(msg, agent, agent.getName() + " " + MessageUtils.AGENT_FOUND);
				registerMessage(msgAgentFound);
				
				logger.info("Starting conversation between agent " + agent.getName() + " and client " + msg.getNameSender() + ".");
				
				getUnreadMessages(idChat, agent);	
			} else {
				doFreeAgent(agent.getId());
			}
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
			logger.info("Unread message " + m.getMessage() + " will be registered in the general heap. Name " + listAgents.get(agent.getId()).getName());
			registerMessage(m);
		}
	}
	
	public void leaveConversation(int idUser, Role role, int idChat) {
		if (role == Role.AGENT) {
			doFreeAgent(listAgents.get(idUser).getId());
		} 
		
		if (role == Role.CLIENT) {
			int idAgent = listChats.get(idChat).getIdAgent();
			if (idAgent > 0) {
				doFreeAgent(idAgent);	
			}
		}
		Chat chat = listChats.get(idChat);
		if (chat != null) {
			chat.disconnectAgent();
		}
	}
	
	public void exit(int idUser, Role role, int idChat) {
		if (role == Role.AGENT) {
			listAgents.remove(idUser);
			logger.info("Removing agent id " + idUser);
			Chat chat = listChats.get(idChat);
			if (chat != null) {
				chat.disconnectAgent();
			}
		}
		
		if (role == Role.CLIENT) {
			listClients.remove(idUser);
			logger.info("Removing client id " + idUser);
			int idAgent = listChats.get(idChat).getIdAgent();
			listChats.remove(idChat);
			logger.info("Removing chat id " + idChat);
			if (idAgent > 0) {
				doFreeAgent(idAgent);	
			}
		}
	}
	
	public void deleteHttpMessageById(int id) {
		listHttpMessages.remove(id);
	}
	
	public boolean agentIsFree(int id) {
		return !listAgents.containsKey(id);
	}
}
