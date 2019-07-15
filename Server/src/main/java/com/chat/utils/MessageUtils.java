package com.chat.utils;

import com.chat.enums.CommunicationMethod;
import com.chat.enums.MessageType;
import com.chat.enums.Role;
import com.chat.model.Agent;
import com.chat.model.Message;

public class MessageUtils {
	public static final String SEARCHING_AGENT = "Now will be a search free agent for you";
	public static final String AGENT_FOUND = "connected to the chat";
	public static final String REG_SUCCESS = "Registration successful!";
	public static final String LEAVE = "/l";
	public static final String EXIT = "/e";
	public static final String LEAVE_CHAT = "has left this chat";
	
	public static void createMessageByParseString(Message msg) {
		String[] tokens = msg.getMessage().split("_");
		
		MessageType msgType = MessageType.valueOf(tokens[0]);
		int idSender = Integer.parseInt(tokens[1]);
		int idReceiver = Integer.parseInt(tokens[2]);
		int idChat = Integer.parseInt(tokens[3]);
		String nameSender = tokens[4];
		Role roleSender = Role.valueOf(tokens[5]);
		
		String message = "";
		for (int i = 6; i < tokens.length; i++) {
			if (i > 6) {
				message += "_";
			}
			message += tokens[i];
		}
	
		msg.setMsgType(msgType);
		msg.setIdSender(idSender);
		msg.setIdReceiver(idReceiver);
		msg.setIdChat(idChat);
		msg.setNameSender(nameSender);
		msg.setRoleSender(roleSender);
		msg.setMessage(message);
		
		if (roleSender == Role.CLIENT) {
			msg.setRoleReceiver(Role.AGENT);
		} else {
			msg.setRoleReceiver(Role.CLIENT);
		}
	}
	
	public static Message createMessageClientWhenWaiting(Message msgOrigin, Agent agent, String src) {
		Message msg = new Message();
		msg.setComMethod(CommunicationMethod.CONSOLE);
		msg.setMsgType(MessageType.SRV);
		msg.setIdSender(agent == null ? 0 : agent.getId());
		msg.setIdReceiver(msgOrigin.getIdSender());
		msg.setIdChat(msgOrigin.getIdChat());
		msg.setNameSender(agent == null ? "--SERVICE--" : agent.getName());
		msg.setRoleSender(msgOrigin.getRoleSender() == Role.AGENT ? Role.CLIENT : Role.AGENT);
		msg.setRoleReceiver(msgOrigin.getRoleSender());
		msg.setMessage(src);
		convertInformationToStringMessage(msg);
		return msg;
	}
	
	public static Message createServiceMessageLeaveExit(Message msgOrigin, String src) {
		Message msg = new Message();
		msg.setIdSender(0);
		msg.setIdReceiver(msgOrigin.getIdSender());
		msg.setIdChat(msgOrigin.getIdChat());
		msg.setNameSender("---SERVICE---");
		msg.setRoleSender(msgOrigin.getRoleSender() == Role.AGENT ? Role.CLIENT : Role.AGENT);
		msg.setRoleReceiver(msgOrigin.getRoleSender());
		msg.setMessage(src);
		return msg;
	}
	
	public static void convertInformationToStringMessage(Message msg) {
		StringBuilder build = new StringBuilder();
		build.append(msg.getMsgType() + "_")
			.append(msg.getIdSender() + "_")
			.append(msg.getIdReceiver() + "_")
			.append(msg.getIdChat() + "_")
			.append(msg.getNameSender() + "_")
			.append(msg.getRoleSender() + "_")
			.append(msg.getMessage() + "\n");
		
		msg.setMessage(build.toString());
	}
}
