package com.chat.handlers;

import java.util.Collection;

import com.chat.enums.CommunicationMethod;
import com.chat.enums.MessageType;
import com.chat.enums.Role;
import com.chat.model.Message;
import com.chat.services.ServiceManager;
import com.chat.utils.MessageUtils;

public class HttpMessageHandler {
	private ServiceManager manager;

	public HttpMessageHandler(ServiceManager manager) {
		this.manager = manager;
	}
	
	public void process(Message msg) {
		msg.setComMethod(CommunicationMethod.WEB);
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
		manager.registerMessage(MessageUtils.createMessageClientWhenWaiting(msg, null, MessageUtils.SEARCHING_AGENT));
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
		manager.deleteHttpMessageById(idUser);
		manager.exit(idUser, role, idChat);
	}
	
	public void createServiceMessageLeaveExit(Message msgOrigin, MessageType type, String src) {
		Message servMsg = MessageUtils.createServiceMessageLeaveExit(msgOrigin, src);
		servMsg.setMsgType(type);
		System.out.println("servMsg " + servMsg);
		manager.registerMessage(servMsg);
	}
}
