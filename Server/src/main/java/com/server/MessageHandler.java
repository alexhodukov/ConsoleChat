package com.server;

public class MessageHandler {	
	private static final String WELCOME = "Please, register as agent or client: /register agent(client)";
	private static final String REG_SUCCESS = "Registration successful!\n";
	private static final String UNSUP_COMMAND = "Unsupported command, please, write the command correctly!";
	private static final String REGISTER = "/r";
	private static final String AGENT = "a";
	private static final String CLIENT = "c";
	
	public MessageHandler() {
	}
	
	public Message processMessage(String src) {
		Message msg = new Message(src);

		return msg;
	}
	
	public void addNameSender(Message msg, String name) {
		String s = msg.getMessage();
		String[] tokens = s.split("_");
		
		StringBuilder builder = new StringBuilder();
		builder.append(tokens[0] + "_" + name);
		
		for (int i = 1; i < tokens.length; i++) {
			builder.append("_" + tokens[i]);
		}
		msg.setMessage(builder.toString());
	}
	
	public Message registerUser(int id, String src) {
		Message msg = new Message();
		msg.setIdReceiver(id);
		
		if (src.startsWith("REG")) {
			String[] tokens = src.split("_");
			String body = "";
			if (tokens.length > 1) {
				StringBuilder builder = new StringBuilder();
				for (int i = 1; i < tokens.length; i++) {
					builder.append(tokens[i]);
				}	
				body = builder.toString();
			} else {
				body = tokens[1];
				
			}
			
			tokens = body.split(" ");			
			if (tokens.length == 3 && REGISTER.equals(tokens[0])) {
				switch (tokens[1]) {
				case AGENT : {
					registerSuccess(msg, REG_SUCCESS, tokens[2], Role.AGENT);
				} break;
				case CLIENT : {
					registerSuccess(msg, REG_SUCCESS, tokens[2], Role.CLIENT);
				} break;
				default : {
					setErrorMessage(msg, UNSUP_COMMAND);
				} break;
				}					
			} else {
				setErrorMessage(msg, UNSUP_COMMAND);
			}
			addHeaderRegistration(msg);
		}
		
		
		return msg;
	}
	
	private void addHeaderRegistration(Message msg) {
		msg.setMessage("REG_" + msg.getIdReceiver() + "_" + msg.getRoleReceiver() + "_" + msg.getMessage());
	}
	
	private void registerSuccess(Message msg, String src, String name, Role role) {
		msg.setMessage(src);
		msg.setNameSender(name);
		msg.setRoleReceiver(role);
	}
	
	private void setErrorMessage(Message msg, String s) {
		msg.setErrorMessage(true);
		msg.setMessage(s);
	}
}
