package com.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.chat.handlers.HttpMessageHandler;
import com.chat.handlers.IncomingSocketHandler;
import com.chat.handlers.MessageHandler;
import com.chat.services.ServiceManager;

public class Server {	
	private ServiceManager manager;
	private MessageHandler msgHandler;
	private HttpMessageHandler httpMsgHandler;
	
	private static final Logger logger = Logger.getLogger(Server.class);

	public Server() {
		this.manager = new ServiceManager();
		this.msgHandler = new MessageHandler(manager);
		this.httpMsgHandler = new HttpMessageHandler(manager);
	}

	public void start() {
		logger.info("Server started. It's logger");
		try (ServerSocket s = new ServerSocket(8282)) {
			System.out.println("Server started!");
			
			Runnable r = () -> {
				while (true) {
					manager.sendMessage();	
				}
			};
			Thread sending = new Thread(r);
			sending.setName("Thread sending messages.");
			ExecutorService execMessage = Executors.newCachedThreadPool();
			execMessage.execute(sending);
			
			while (true) {
				Socket incoming = s.accept();
				Thread t = new Thread(new IncomingSocketHandler(incoming, msgHandler));
				t.setName("Thread server incoming socket");
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

	public HttpMessageHandler getHttpMsgHandler() {
		return httpMsgHandler;
	}
	
	
}
