package com.client.handlers;

import java.io.UnsupportedEncodingException;

import com.client.enums.MessageType;
import com.client.enums.Role;
import com.client.model.ManagerClient;

public class MessageHandler {
	private static final String UNSUP_COMMAND = "Unsupported command, please, write the command correctly!";
	private static final String REGISTER = "/r";
	private static final String AGENT = "a";
	private static final String CLIENT = "c";
	private static final String EXIT = "/e";
	private static final String LEAVE = "/l";
	private static final String LEAVE_CHAT = "has left this chat";
	private static final String NO_INTERLOCUTOR_CLIENT = "You haven't a client for conversation. Your message isn't sent!";
	private static final String NO_INTERLOCUTOR_AGENT = "You can't leave this chat, because you haven't an agent for conversation!";
	private static final String NO_CHAT = "You aren't in any chat!";
	
	private ManagerClient manager;
	private String message;
	private boolean isNeedShowMessage;
	private boolean isCorrectMessage;
	
	public MessageHandler(String msg, ManagerClient manager) {
		this.message = msg;
		this.manager = manager;
	}

	public synchronized void processIncomingMessage() {
		isNeedShowMessage = true;
		MessageType type = MessageType.valueOf(message.substring(0, 3));
		String src = message.substring(4, message.length());
		String[] tokens = src.split("_");
		message = "";
		for (int i = 5; i < tokens.length; i++) {
			if (i > 5) {
				message += "_";
			}
			message += tokens[i];
		}
		switch (type) {
		case REG : {
			manager.setId(Integer.parseInt(tokens[1]));
			manager.setIdChat(Integer.parseInt(tokens[2]));
			manager.setName(tokens[3]);
			manager.setRole(Role.valueOf(tokens[4]));
		} break;
		
		case MSG : {			
			setInterlocutor(tokens);
			message = manager.getInterlocutor().getName() + " : " + message;
		} break;
		
		case LEV : 
		case EXT : {
			Role role = Role.valueOf(tokens[4]);
			
			if (role == Role.CLIENT) {
				manager.setIdChat(0);	
			}
			manager.disconnectInterlucutor();
		} break;
		
		case SRV : {
			setInterlocutor(tokens);
		} break;
		}
	}
	
	private void setInterlocutor(String[] tokens) {
		if (manager.getInterlocutor().getId() == 0 && Integer.parseInt(tokens[0]) != 0) {
			
			manager.getInterlocutor().setId(Integer.parseInt(tokens[0]));
			manager.setIdChat(Integer.parseInt(tokens[2]));
			manager.getInterlocutor().setName((tokens[3]));
			manager.getInterlocutor().setRole((Role.valueOf(tokens[4])));	
		}
	}
	
	public synchronized void processOutgoingMessage(int id, Role role) {
		isCorrectMessage = true;
		if (role == Role.GUEST) {
			String[] tokens = message.split(" ");			
			if (tokens.length == 3 && REGISTER.equals(tokens[0]) && (AGENT.equals(tokens[1]) || CLIENT.equals(tokens[1]))) {
				manager.setName(tokens[2]);
				if (AGENT.equals(tokens[1])) {
					manager.setRole(Role.AGENT);
					createRegistrationMessage();
				}
				if (CLIENT.equals(tokens[1])) {
					manager.setRole(Role.CLIENT);
					createRegistrationMessage();
				}
				
			} else {
				setErrorMessage(UNSUP_COMMAND);
			}
		} else {
			
			switch (message) {
			case LEAVE : {
				if (manager.getInterlocutor().getId() == 0) {
					if (manager.getRole() == Role.AGENT) {
						setErrorMessage(NO_CHAT);	
					} else {
						setErrorMessage(NO_INTERLOCUTOR_AGENT);
					}
				} else {
					createLeaveMessage();
					manager.disconnectInterlucutor();
				}
			} break;
			
			case EXIT : {
				createExitMessage();
				manager.disconnectInterlucutor();
			} break;
			
			default : {
				if (manager.getRole() == Role.AGENT && manager.getInterlocutor().getId() == 0) {
					setErrorMessage(NO_INTERLOCUTOR_CLIENT);
				} else {
					createMessage();	
				}
			} break;
			}
		}
	}
	
	private void createRegistrationMessage() {
		StringBuilder build = new StringBuilder();
		build.append(MessageType.REG + "_")
			.append(createMessageServiceInformation())
			.append(" ")
			.append("\n");
		
		message = build.toString();
	}
	
	private String createMessageServiceInformation() {
		StringBuilder build = new StringBuilder();
		build.append(manager.getId() + "_")
			.append(manager.getInterlocutor().getId() + "_")
			.append(manager.getIdChat() + "_")
			.append(manager.getName() + "_")
			.append(manager.getRole().toString() + "_");
		
		return build.toString();
	}
	
	private void createMessage() {
		StringBuilder build = new StringBuilder();
		build.append(MessageType.MSG.toString() + "_")
			.append(createMessageServiceInformation())
			.append(message)
			.append("\n");
		
		message = build.toString();
	}
	
	private void createLeaveMessage() {
		StringBuilder build = new StringBuilder();
		build.append(MessageType.LEV.toString() + "_")
			.append(createMessageServiceInformation())
			.append(manager.getName() + " " + LEAVE_CHAT)
			.append("\n");
		
		message = build.toString();
	}
	
	private void createExitMessage() {
		StringBuilder build = new StringBuilder();
		build.append(MessageType.EXT.toString() + "_")
			.append(createMessageServiceInformation())
			.append(manager.getName() + " " + LEAVE_CHAT)
			.append("\n");
		
		message = build.toString();
	}
	
	private void setErrorMessage(String msg) {
		isCorrectMessage = false;
		message = msg;
	}
	
	public boolean isCorrectMessage() {
		return isCorrectMessage;
	}
	
	public synchronized byte[] getMessageBytes() {
		byte[] ar = {};
		try {
			ar = message.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ar;
	}
	
	public String getMessage() {
		return message;
	}
	
	
	public boolean isNeedShowMessage() {
		return isNeedShowMessage;
	}
}
