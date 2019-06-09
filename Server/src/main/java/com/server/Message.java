package com.server;

import java.io.UnsupportedEncodingException;

public class Message {
	private String message;
	private int idReceiver;
	private Role roleReceiver;
	private String nameSender;
	private boolean isErrorMessage;
	
	public Message(String msg) {
		this();
		this.message = msg;
	}
	
	public Message() {
		this.roleReceiver = Role.GUEST;
	}
	
	public String getMessage() {
		return message;
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

	public void setIdReceiver(int idReceiver) {
		this.idReceiver = idReceiver;
	}

	public int getIdReceiver() {
		return idReceiver;
	}

	public boolean isErrorMessage() {
		return isErrorMessage;
	}

	public void setErrorMessage(boolean isErrorMessage) {
		this.isErrorMessage = isErrorMessage;
	}

	public void setMessage(String msg) {
		this.message = msg;
	}

	public Role getRoleReceiver() {
		return roleReceiver;
	}

	public void setRoleReceiver(Role roleReceiver) {
		this.roleReceiver = roleReceiver;
	}

	public String getNameSender() {
		return nameSender;
	}

	public void setNameSender(String nameSender) {
		this.nameSender = nameSender;
	}

	@Override
	public String toString() {
		return "Message [src=" + message + ", idReceiver=" + idReceiver + ", roleReceiver=" + roleReceiver + ", nameSender="
				+ nameSender + ", isErrorMessage=" + isErrorMessage + "]";
	}

}
