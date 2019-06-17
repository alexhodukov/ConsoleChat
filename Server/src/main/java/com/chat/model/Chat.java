package com.chat.model;

public class Chat {
	private int id;
	private int idClient;
	private int idAgent;
	private boolean isAgentConnecting;
	
	public Chat(int id, int idClient) {
		this.id = id;
		this.idClient = idClient;
	}
	
	public void disconnectAgent() {
		idAgent = 0;
	}

	public int getId() {
		return id;
	}
	
	public int getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(int idAgent) {
		this.idAgent = idAgent;
	}

	public int getIdClient() {
		return idClient;
	}
	
	

	public boolean isAgentConnecting() {
		return isAgentConnecting;
	}

	public void setAgentConnecting(boolean isAgentConnecting) {
		this.isAgentConnecting = isAgentConnecting;
	}

	@Override
	public String toString() {
		return "Chat [id=" + id + ", idClient=" + idClient + ", idAgent=" + idAgent + ", isAgentConnecting="
				+ isAgentConnecting + "]";
	}	
	
}
