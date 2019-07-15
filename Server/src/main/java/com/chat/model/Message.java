package com.chat.model;

import java.io.UnsupportedEncodingException;

import javax.validation.constraints.NotBlank;

import org.apache.log4j.Logger;

import com.chat.enums.CommunicationMethod;
import com.chat.enums.MessageType;
import com.chat.enums.Role;
import com.chat.server.Server;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Message {
	private static final Logger logger = Logger.getLogger(Message.class);
	
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
			logger.warn("UnsupportedEncodingException", e);
		}
		return ar;
	}
	
	public void convertToWeb() {
		if (comMethod == CommunicationMethod.CONSOLE) {
			String[] tokens = message.split("_");
			message = "";
			for (int i = 6; i < tokens.length; i++) {
				if (i > 6) {
					message += "_";
				}
				message += tokens[i];
			}
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comMethod == null) ? 0 : comMethod.hashCode());
		result = prime * result + idChat;
		result = prime * result + idReceiver;
		result = prime * result + idSender;
		result = prime * result + (isErrorMessage ? 1231 : 1237);
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((msgType == null) ? 0 : msgType.hashCode());
		result = prime * result + ((nameReceiver == null) ? 0 : nameReceiver.hashCode());
		result = prime * result + ((nameSender == null) ? 0 : nameSender.hashCode());
		result = prime * result + ((roleReceiver == null) ? 0 : roleReceiver.hashCode());
		result = prime * result + ((roleSender == null) ? 0 : roleSender.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (comMethod != other.comMethod)
			return false;
		if (idChat != other.idChat)
			return false;
		if (idReceiver != other.idReceiver)
			return false;
		if (idSender != other.idSender)
			return false;
		if (isErrorMessage != other.isErrorMessage)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (msgType != other.msgType)
			return false;
		if (nameReceiver == null) {
			if (other.nameReceiver != null)
				return false;
		} else if (!nameReceiver.equals(other.nameReceiver))
			return false;
		if (nameSender == null) {
			if (other.nameSender != null)
				return false;
		} else if (!nameSender.equals(other.nameSender))
			return false;
		if (roleReceiver != other.roleReceiver)
			return false;
		if (roleSender != other.roleSender)
			return false;
		return true;
	}

}
