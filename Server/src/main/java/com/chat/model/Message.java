package com.chat.model;

import java.io.UnsupportedEncodingException;

import javax.validation.constraints.NotBlank;

import com.chat.enums.CommunicationMethod;
import com.chat.enums.MessageType;
import com.chat.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Message {
	
	@NotBlank(message = "Message may not be null")
	private String message;
	private int idReceiver;
	private int idSender;
	private int idChat;
	private Role roleReceiver;
	private Role roleSender;
	private String nameReceiver;
	
	@NotBlank(message = "nameSender may not be null")
	private String nameSender;
	private boolean isErrorMessage;
	private MessageType msgType;
	private CommunicationMethod comMethod;
	
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
	
	@JsonIgnore
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
		if (comMethod == CommunicationMethod.CONSOLE) {
			String[] tokens = message.split("_");
			System.out.println("message " + message);
			message = tokens[6];	
		}
	}
	
	public void convertToConsole() {
		if (comMethod == CommunicationMethod.WEB) {
			StringBuilder build = new StringBuilder();
			build.append(msgType + "_")
				.append(idSender + "_")
				.append(idReceiver + "_")
				.append(idChat + "_")
				.append(nameSender + "_")
				.append(roleSender + "_")
				.append(message + "\n");
			
			message = build.toString();	
		}
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

	public CommunicationMethod getComMethod() {
		return comMethod;
	}

	public void setComMethod(CommunicationMethod comMethod) {
		this.comMethod = comMethod;
	}

	@Override
	public String toString() {
		return "Message [message=" + message + ", idReceiver=" + idReceiver + ", idSender=" + idSender + ", idChat="
				+ idChat + ", roleReceiver=" + roleReceiver + ", roleSender=" + roleSender + ", nameReceiver="
				+ nameReceiver + ", nameSender=" + nameSender + ", isErrorMessage=" + isErrorMessage + ", msgType="
				+ msgType + "]";
	}

}
