package com.client;

import java.io.UnsupportedEncodingException;

public class MessageHandler {
	private static final String UNSUP_COMMAND = "Unsupported command, please, write the command correctly!";
	private static final String REGISTER = "/r";
	private static final String AGENT = "a";
	private static final String CLIENT = "c";
	private static final String ERROR_PARSE = "Unsupported command, please, write the command correctly!";
	
	private String message;
	private boolean isCorrectMessage;
	
	public MessageHandler(String msg) {
		this.message = msg;
	}
	
	public void processIncomingMessage(ManagerClient man) {
		String[] tokens = message.split("_");	
		System.out.println("msg " + message);
		if (message.startsWith("REG") & tokens.length == 4) {
			man.setId(Integer.parseInt(tokens[1]));
			man.setRole(Role.valueOf(tokens[2]));
			message = tokens[3];
		} else {
			message = tokens[1] + " : " + tokens[2];
		}
	}
	
	public void processOutgoingMessage(int id, Role role) {
		isCorrectMessage = true;
		if (role == Role.GUEST) {
			String[] tokens = message.split(" ");			
			if (tokens.length == 3 && REGISTER.equals(tokens[0]) && (AGENT.equals(tokens[1]) || CLIENT.equals(tokens[1]))) {
				System.out.println("Correct true");
			} else {
				setErrorMessage(UNSUP_COMMAND);
			}
			addHeaderRegistration();
		} else {
			addHeaderMessage();
		}
	}
	
	private void addHeaderRegistration() {
		message = "REG_" + message + "\n";
	}
	
	private void addHeaderMessage() {
		message = "MSG_" + message + "\n";
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
	
	
}
