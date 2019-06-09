package com.server;

import java.io.UnsupportedEncodingException;

public class Message {
	private String src;
	private int idReceiver;
	private Role role;
	private String name;
	private boolean isErrorMessage;
	
	public Message(String src) {
		this();
		this.src = src;
	}
	
	public Message() {
		this.role = Role.GUEST;
	}
	
	public byte[] getMessage() {
		byte[] ar = {};
		try {
			ar = src.getBytes("UTF-8");
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

	public void setSrc(String src) {
		this.src = src;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Message [src=" + src + ", idReceiver=" + idReceiver + ", role=" + role + ", name=" + name
				+ ", isErrorMessage=" + isErrorMessage + "]";
	}

	
}
