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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + idAgent;
		result = prime * result + idClient;
		result = prime * result + (isAgentConnecting ? 1231 : 1237);
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
		Chat other = (Chat) obj;
		if (id != other.id)
			return false;
		if (idAgent != other.idAgent)
			return false;
		if (idClient != other.idClient)
			return false;
		if (isAgentConnecting != other.isAgentConnecting)
			return false;
		return true;
	}	
	
}
