package com.client;

import java.io.UnsupportedEncodingException;

public class MessageHandler {
	private static final String UNSUP_COMMAND = "Unsupported command, please, write the command correctly!";
	private static final String REGISTER = "/r";
	private static final String AGENT = "a";
	private static final String CLIENT = "c";
	private static final String ERROR_PARSE = "Incorrect message format!";
	private static final String EXIT = "/e";
	private static final String LEAVE = "/l";
	
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
			if (tokens.length == 5) {
				manager.setId(Integer.parseInt(tokens[0]));
				manager.setRole(Role.valueOf(tokens[1]));
				manager.setName(tokens[2]);
				manager.setIdChat(Integer.parseInt(tokens[3]));
				message = tokens[4];
			}
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
		case SRV : {
			String name = tokens[0];
			Role role = Role.valueOf(tokens[1]);
			String msg = tokens[2];
			
			if (role == Role.CLIENT) {
				manager.setIdChat(0);	
			}
			manager.disconnectInterlucutor(name);
			isNeedShowMessage = false;
		} break;
		}
	}
	
	public void processOutgoingMessage(int id, Role role) {
		isCorrectMessage = true;
		if (role == Role.GUEST) {
			String[] tokens = message.split(" ");			
			if (tokens.length == 3 && REGISTER.equals(tokens[0]) && (AGENT.equals(tokens[1]) || CLIENT.equals(tokens[1]))) {
				if (AGENT.equals(tokens[1])) {
					createRegistrationMessage(Role.AGENT, tokens[2]);
				}
				if (CLIENT.equals(tokens[1])) {
					createRegistrationMessage(Role.CLIENT, tokens[2]);
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
				createMessage();
			} break;
			}
		}
	}
	
	private void createRegistrationMessage(Role role, String name) {
		StringBuilder build = new StringBuilder();
		build.append(MessageType.REG + "_")
			.append(role + "_")
			.append(name + "_")
			.append("\n");
		
		message = build.toString();
	}
	
	private void createMessage() {
		StringBuilder build = new StringBuilder();
		build.append(MessageType.MSG.toString() + "_")
			.append(manager.getId() + "_")
			.append(manager.getInterlocutor().getId() + "_")
			.append(manager.getIdChat() + "_")
			.append(manager.getName() + "_")
			.append(manager.getRole().toString() + "_")
			.append(message)
			.append("\n");
		
		message = build.toString();
	}
	
	private void createLeaveMessage() {
		StringBuilder build = new StringBuilder();
		build.append(MessageType.LEV.toString() + "_")
			.append(manager.getId() + "_")
			.append(manager.getInterlocutor().getId() + "_")
			.append(manager.getName() + "_")
			.append(manager.getRole().toString() + "_")
			.append(manager.getIdChat())
			.append("\n");
		
		message = build.toString();
	}
	
	private void createExitMessage() {
		StringBuilder build = new StringBuilder();
		build.append(MessageType.EXT.toString() + "_")
			.append(manager.getId() + "_")
			.append(manager.getInterlocutor().getId() + "_")
			.append(manager.getName() + "_")
			.append(manager.getRole().toString() + "_")
			.append(manager.getIdChat())
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
