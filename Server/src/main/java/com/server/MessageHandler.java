package com.server;

import java.net.Socket;

public class MessageHandler {	
	private static final String WELCOME = "Please, register as agent or client: /register agent(client)" + "\n";
	private static final String REG_SUCCESS = "Registration successful!" + "\n";
	private static final String UNSUP_COMMAND = "Unsupported command, please, write the command correctly!" + "\n";
	private static final String INCOR_MESSAGE = "Incorrect format message!" + "\n";
	private static final String REGISTER = "/r";
	private static final String AGENT = "a";
	private static final String CLIENT = "c";
	
	private ServiceManager manager;
	
	public MessageHandler(ServiceManager manager) {
		this.manager = manager;
	}
	
	public void processMessage(String src, Socket socket) {
		Message msg = new Message(src);
		MessageType type = MessageType.valueOf(src.substring(0, 3));
		msg.setMessage(msg.getMessage().substring(4, msg.getMessage().length()));
		String[] tokens = msg.getMessage().split("_");
		switch (type) {
		case REG : {
			registerUser(msg, socket);
		} break;
		case MSG : {
			msg.setMessage("MSG_" + msg.getMessage() + "\n");
			msg.setIdSender(Integer.parseInt(tokens[0]));
			msg.setIdReceiver(Integer.parseInt(tokens[1]));
			msg.setIdChat(Integer.parseInt(tokens[2]));
			msg.setNameSender(tokens[3]);
			Role role = Role.valueOf(tokens[4]);
			msg.setRoleSender(role);
			if (role == Role.CLIENT) {
				msg.setRoleReceiver(Role.AGENT);
				int idAgent = manager.getIdAgentByIdChat(msg.getIdChat());
				msg.setIdReceiver(idAgent);
			} else {
				msg.setRoleReceiver(Role.CLIENT);
			}
			if (role == Role.CLIENT && msg.getIdReceiver() == 0 && !manager.isAgentConnecting(msg.getIdChat())) {
				manager.connectAgent(msg);	
			} 
			manager.registerMessage(msg);	
		} break;
		case LEV : {
			msg.setNameSender(tokens[2]);
			int idReceiver = Integer.parseInt(tokens[1]);
			msg.setIdReceiver(idReceiver);
			int idSender = Integer.parseInt(tokens[0]);
			Role roleSender = Role.valueOf(tokens[3]);
			msg.setRoleSender(roleSender);
			int idChat = Integer.parseInt(tokens[4]);
			if (roleSender == Role.CLIENT) {
				msg.setRoleReceiver(Role.AGENT);
			} else {
				msg.setRoleReceiver(Role.CLIENT);
			}
			msg.setMessage("leave");
			createServiceMessage(msg);
			manager.registerMessage(msg);
			manager.leaveConversation(idSender, roleSender, idChat);
		} break;
		case EXT : {
			msg.setNameSender(tokens[2]);
			int idReceiver = Integer.parseInt(tokens[1]);
			msg.setIdReceiver(idReceiver);
			int idSender = Integer.parseInt(tokens[0]);
			Role roleSender = Role.valueOf(tokens[3]);
			msg.setRoleSender(roleSender);
			int idChat = Integer.parseInt(tokens[4]);
			if (roleSender == Role.CLIENT) {
				msg.setRoleReceiver(Role.AGENT);
			} else {
				msg.setRoleReceiver(Role.CLIENT);
			}
			msg.setMessage("exit");
			createServiceMessage(msg);
			manager.registerMessage(msg);
			manager.exit(idSender, roleSender, idChat);
		} break;
		}
	}
	
	public Message registerUser(Message msg, Socket socket) {
		String[] tokens = msg.getMessage().split("_");
		if (tokens.length == 2) {
			int id = 0;
			Role role = Role.valueOf(tokens[0]);
			switch (role) {
			case AGENT : {
				registerSuccess(msg, REG_SUCCESS, tokens[1], Role.AGENT);
				id = manager.createAgent(socket, msg.getNameReceiver());
			} break;
			case CLIENT : {
				registerSuccess(msg, REG_SUCCESS, tokens[1], Role.CLIENT);
				id = manager.createClient(socket, msg.getNameReceiver());
				int idChat = manager.createChat(id);
				msg.setIdChat(idChat);
			} break;
			default : {
				setErrorMessage(msg, UNSUP_COMMAND);
			} break;
			}	
			msg.setIdReceiver(id);
			addHeaderRegistration(msg);
			manager.registerMessage(msg);
			if (role == Role.AGENT) {
				manager.doFreeAgent(id);
			}
		} else {
			setErrorMessage(msg, INCOR_MESSAGE);
		}
		
		return msg;
	}
	
	private void registerSuccess(Message msg, String src, String name, Role role) {
		msg.setMessage(src);
		msg.setNameReceiver(name);
		msg.setRoleReceiver(role);
	}
	
	
	private void addHeaderRegistration(Message msg) {		
		StringBuilder build = new StringBuilder();
		build.append(MessageType.REG.toString() + "_")
			.append(msg.getIdReceiver() + "_")
			.append(msg.getRoleReceiver() + "_")
			.append(msg.getNameReceiver() + "_")
			.append(msg.getIdChat() + "_")
			.append(msg.getMessage());
		
		msg.setMessage(build.toString());
	}
	
	private void createServiceMessage(Message msg) {
		StringBuilder build = new StringBuilder();
		build.append(MessageType.SRV.toString() + "_")
			.append(msg.getNameSender() + "_")
			.append(msg.getRoleSender() + "_")
			.append(msg.getMessage())
			.append("\n");
		
		msg.setMessage(build.toString());
	}
	
	private void setErrorMessage(Message msg, String s) {
		msg.setErrorMessage(true);
		msg.setMessage(s);
	}
}
