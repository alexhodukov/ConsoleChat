package com.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.chat.handlers.IncomingSocketHandler;
import com.chat.handlers.MessageHandler;
import com.chat.services.ServiceManager;

public class Server {
	private ServiceManager manager;
	private MessageHandler msgHandler;

	public Server() {
		this.manager = new ServiceManager();
		this.msgHandler = new MessageHandler(manager);
	}

	public void start() {
		try (ServerSocket s = new ServerSocket(8282)) {
			System.out.println("Server started!");
			
			Runnable r = () -> {
				while (true) {
					manager.sendMessage();	
				}
			};
			Thread sending = new Thread(r);
			sending.setName("Thread sending");
			sending.start();
			
			while (true) {
				Socket incoming = s.accept();
				Thread t = new Thread(new IncomingSocketHandler(incoming, msgHandler));
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ServiceManager getManager() {
		return manager;
	}

	public MessageHandler getMsgHandler() {
		return msgHandler;
	}
	
	
}
