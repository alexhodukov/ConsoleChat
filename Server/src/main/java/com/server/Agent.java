package com.server;

import java.net.Socket;

public class Agent {
	private Socket socket; 
	private String name;
	private boolean isFree;
	
	public Agent(Socket socket, String name) {
		this.socket = socket;
		this.name = name;
		this.isFree = true;
	}
	

	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	public String getName() {
		return name;
	}
	
	public Socket getSocket() {
		return socket;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isFree ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Agent other = (Agent) obj;
		if (isFree != other.isFree)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Agent [name=" + name + ", isFree=" + isFree + "]";
	}
	
	
}
