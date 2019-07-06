package com.chat.handlers;

import java.util.Collection;

import com.chat.enums.Role;
import com.chat.model.Message;
import com.chat.services.ServiceManager;

public class HttpMessageHandler {
	private ServiceManager manager;

	public HttpMessageHandler(ServiceManager manager) {
		this.manager = manager;
	}
	
	public void process(Message msg) {
		msg.setRoleReceiver(msg.getRoleSender() == Role.AGENT ? Role.CLIENT : Role.AGENT);
		manager.registerMessage(msg);
	}
	
	public int registerUser(Role role, String name) {
		int id = 0;
		switch (role) {
		case AGENT : {
			id = manager.createHttpAgent(name);
		} break;
		case CLIENT : {
			id = manager.createHttpClient(name);
		} break;
		}	
		return id;
	}
	
	public void doFreeAgent(int idAgent) {
		manager.doFreeAgent(idAgent);
	}
	
	public int createChat(int idClient) {
		return manager.createChat(idClient);
	}
	
	public Collection<Message> getMessages(int idReceiver) { 
		return manager.getHttpMessages(idReceiver);
	}
	
	public boolean isAgentConnecting(int idChat) {
		return manager.isAgentConnecting(idChat);
	}
	
	public void connectAgent(Message msg) {
		manager.connectAgent(msg);
	}
	
	public boolean isExistAgentInChat(int idChat) {
		return manager.isExistAgentInChat(idChat);
	}
	
	public int getIdAgentByIdChat(int idChat) {
		return manager.getIdAgentByIdChat(idChat);
	}
	
	public void leaveConversation(int idUser, Role role, int idChat) {
		manager.leaveConversation(idUser, role, idChat);
	}
	
	public void exit(int idUser, Role role, int idChat) {
		manager.exit(idUser, role, idChat);
	}
}
