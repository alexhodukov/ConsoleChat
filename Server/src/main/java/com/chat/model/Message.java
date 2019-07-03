package com.chat.model;

import java.io.UnsupportedEncodingException;

import com.chat.enums.MessageType;
import com.chat.enums.Role;

public class Message {
	private String message;
	private int idReceiver;
	private int idSender;
	private int idChat;
	private Role roleReceiver;
	private Role roleSender;
	private String nameReceiver;
	private String nameSender;
	private boolean isErrorMessage;
	private MessageType msgType;
	
	public Message(String msg) {
		this();
		this.message = msg;
	}
	
	public Message() {
		this.roleReceiver = Role.GUEST;
		this.msgType = MessageType.MSG;
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
	
	public void convertToWeb() {
		String[] tokens = message.split("_");
		message = tokens[6];
	}
	
	public void convertToConsole() {
		StringBuilder build = new StringBuilder();
		build.append(msgType.toString() + "_")
			.append(idReceiver + "_")
			.append(roleReceiver + "_")
			.append(nameReceiver + "_")
			.append(idChat + "_")
			.append(message + "\n");
		
		message = build.toString();
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

	public Role getRoleSender() {
		return roleSender;
	}

	public void setRoleSender(Role roleSender) {
		this.roleSender = roleSender;
	}

	public String getNameSender() {
		return nameSender;
	}

	public void setNameSender(String nameSender) {
		this.nameSender = nameSender;
	}

	public String getNameReceiver() {
		return nameReceiver;
	}

	public void setNameReceiver(String nameReceiver) {
		this.nameReceiver = nameReceiver;
	}

	public int getIdChat() {
		return idChat;
	}

	public void setIdChat(int idChat) {
		this.idChat = idChat;
	}

	public int getIdSender() {
		return idSender;
	}

	public void setIdSender(int idSender) {
		this.idSender = idSender;
	}
	
	

	public MessageType getMsgType() {
		return msgType;
	}

	public void setMsgType(MessageType msgType) {
		this.msgType = msgType;
	}

	@Override
	public String toString() {
		return "Message [message=" + message + ", idReceiver=" + idReceiver + ", idSender=" + idSender + ", idChat="
				+ idChat + ", roleReceiver=" + roleReceiver + ", roleSender=" + roleSender + ", nameReceiver="
				+ nameReceiver + ", nameSender=" + nameSender + "]";
	}
	
	

}