package com.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private ServiceManager manager;

	public Server() {
		this.manager = new ServiceManager();
	}

	public void start() {
		try (ServerSocket s = new ServerSocket(8282)) {
			System.out.println("Server started!");
			while (true) {
				Socket incoming = s.accept();
				Thread t = new Thread(new UserThreadHandler(manager, incoming));
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ServiceManager getManager() {
		return manager;
	}
}
