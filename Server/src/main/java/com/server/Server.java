package com.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private Chat chat;
	private MessageHandler msgHandler;

	public Server() {
		this.msgHandler = new MessageHandler();
		this.chat = new Chat(msgHandler);
	}

	public void start() {
		try (ServerSocket s = new ServerSocket(8282)) {
			System.out.println("Server started!");
			
			Runnable r = () -> {
				chat.sendMessage();
			};
			Thread sending = new Thread(r);
			sending.start();
			
			while (true) {
				Socket incoming = s.accept();
				Thread t = new Thread(new IncomingCall(this, incoming));
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Chat getChat() {
		return chat;
	}

	public MessageHandler getMsgHandler() {
		return msgHandler;
	}
	
	
}
