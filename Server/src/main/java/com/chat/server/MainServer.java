package com.chat.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;

public class MainServer {
	
	static {
		try {
	          LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}

}
