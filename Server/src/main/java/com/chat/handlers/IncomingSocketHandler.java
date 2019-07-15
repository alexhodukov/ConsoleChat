package com.chat.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class IncomingSocketHandler implements Runnable {	
	private static final Logger logger = Logger.getLogger(IncomingSocketHandler.class);
	
	private Socket socket;
	private MessageHandler msgHandler;
	
	public IncomingSocketHandler(Socket socket, MessageHandler msgHandler) {
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
				msgHandler.processMessage(line, socket);
			}
			
			reader.close();
		} catch (IOException e) {
			logger.warn("Socket is closed", e);
		} 
	}
}
