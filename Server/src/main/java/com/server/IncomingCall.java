package com.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class IncomingCall implements Runnable {
	private static Logger log = Logger.getLogger(IncomingCall.class.getName());
	
	private Socket socket;
	private MessageHandler msgHandler;
	private boolean isExit;
	
	public IncomingCall(Socket socket, MessageHandler msgHandler) {
		this.socket = socket;
		this.msgHandler = msgHandler;
	}
	
	@Override
	public void run() {		
		try (InputStream input = socket.getInputStream();
			OutputStream output = socket.getOutputStream()) {
			
			Scanner reader = new Scanner(new InputStreamReader(input, "UTF-8"));
			
			while (reader.hasNextLine()) {				
				String line = reader.nextLine();
//				System.out.println("Incoming line " + line);
				msgHandler.processMessage(line, socket);
			}
			
			waitThread();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private synchronized void waitThread() {
		while (!isExit) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public synchronized void terminatedChat() {
		isExit = true;
		notify();
	}

	public Socket getSocket() {
		return socket;
	}

	
}
