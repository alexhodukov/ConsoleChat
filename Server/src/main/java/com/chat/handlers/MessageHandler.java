package com.chat.handlers;

import java.net.Socket;

import com.chat.enums.CommunicationMethod;
import com.chat.enums.Role;
import com.chat.model.Message;
import com.chat.services.ServiceManager;
import com.chat.utils.MessageUtils;

public class MessageHandler {	
	private ServiceManager manager;
	
	public MessageHandler(ServiceManager manager) {
		this.manager = manager;
	}
	
	public void processMessage(String src, Socket socket) {
		Message msg = new Message(src);
		msg.setComMethod(CommunicationMethod.CONSOLE);
		MessageUtils.createMessageByParseString(msg);
		
		switch (msg.getMsgType()) {
		case REG : {
			registerUser(msg, socket);
		} break;
		
		case MSG : {			
			
			if (msg.getRoleSender() == Role.CLIENT && msg.getIdReceiver() == 0) {
				int idAgent = manager.getIdAgentByIdChat(msg.getIdChat());
				msg.setIdReceiver(idAgent);
			}
			
			if (msg.getRoleSender() == Role.CLIENT && msg.getIdReceiver() == 0 && !manager.isAgentConnecting(msg.getIdChat())) {
				manager.registerMessage(MessageUtils.createMessageClientWhenWaiting(msg, MessageUtils.SEARCHING_AGENT));
				manager.connectAgent(msg);	
			} 
			
			MessageUtils.convertInformationToStringMessage(msg);
			manager.registerMessage(msg);	
		} break;
		
		case LEV : {			
			MessageUtils.convertInformationToStringMessage(msg);
			manager.registerMessage(msg);
			manager.leaveConversation(msg.getIdSender(), msg.getRoleSender(), msg.getIdChat());
		} break;
		
		case EXT : {
			MessageUtils.convertInformationToStringMessage(msg);
			manager.registerMessage(msg);
			manager.exit(msg.getIdSender(), msg.getRoleSender(), msg.getIdChat());
		} break;
		}
	}
	
	private void registerUser(Message msg, Socket socket) {
		String[] tokens = msg.getMessage().split("_");
		int id = 0;
		switch (msg.getRoleSender()) {
		case AGENT : {
			id = manager.createAgent(socket, msg.getNameSender());
		} break;
		case CLIENT : {
			id = manager.createClient(socket, msg.getNameSender());
			int idChat = manager.createChat(id);
			msg.setIdChat(idChat);
		} break;
		}
		
		msg.setIdReceiver(id);
		msg.setNameReceiver(msg.getNameSender());
		msg.setRoleReceiver(msg.getRoleSender());
		msg.setMessage(MessageUtils.REG_SUCCESS);
		
		MessageUtils.convertInformationToStringMessage(msg);
		manager.registerMessage(msg);
		
		if (msg.getRoleReceiver() == Role.AGENT) {
			manager.doFreeAgent(id);
		}
	}	

}
