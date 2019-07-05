package com.client;

import java.io.UnsupportedEncodingException;

public class MessageHandler {
	private static final String UNSUP_COMMAND = "Unsupported command, please, write the command correctly!";
	private static final String REGISTER = "/r";
	private static final String AGENT = "a";
	private static final String CLIENT = "c";
	private static final String EXIT = "/e";
	private static final String LEAVE = "/l";
	private static final String LEAVE_CHAT = "has left this chat";
	private static final String NO_INTERLOCUTOR = "You haven't a client for discussion. Your message isn't sent!";
	
	private ManagerClient manager;
	private String message;
	private boolean isNeedShowMessage;
	private boolean isCorrectMessage;
	
	public MessageHandler(String msg, ManagerClient manager) {
		this.message = msg;
		this.manager = manager;
	}

	public void processIncomingMessage() {
		isNeedShowMessage = true;
		MessageType type = MessageType.valueOf(message.substring(0, 3));
		message = message.substring(4, message.length());
		String[] tokens = message.split("_");
		switch (type) {
		case REG : {
			manager.setId(Integer.parseInt(tokens[1]));
			manager.setIdChat(Integer.parseInt(tokens[2]));
			manager.setName(tokens[3]);
			manager.setRole(Role.valueOf(tokens[4]));
			message = tokens[5];
		} break;
		
		case MSG : {
			if (manager.getInterlocutor().getId() == 0) {
				manager.getInterlocutor().setId(Integer.parseInt(tokens[0]));
				manager.setIdChat(Integer.parseInt(tokens[2]));
				manager.getInterlocutor().setName((tokens[3]));
				manager.getInterlocutor().setRole((Role.valueOf(tokens[4])));
			}
			
			message = manager.getInterlocutor().getName() + " : " + tokens[5];
		} break;
		
		case LEV : 
		case EXT : {
			Role role = Role.valueOf(tokens[4]);
			message = tokens[5];
			
			if (role == Role.CLIENT) {
				manager.setIdChat(0);	
			}
			manager.disconnectInterlucutor();
		} break;
		
		case SRV : {
			message = tokens[5];
		} break;
		}
	}
	
	public void processOutgoingMessage(int id, Role role) {
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
				createLeaveMessage();
			} break;
			
			case EXIT : {
				createExitMessage();
			} break;
			
			default : {
				if (manager.getRole() == Role.AGENT && manager.getInterlocutor().getId() == 0) {
					setErrorMessage(NO_INTERLOCUTOR);
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
	
	public byte[] getMessageBytes() {
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
