package com.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class ClientInput implements Runnable {
	private ManagerClient manager;
	private BufferedReader reader;
	private Socket socket;
	
	public ClientInput(ManagerClient manager, BufferedReader reader, Socket socket) {
		this.manager = manager;
		this.reader = reader;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		System.out.println("Please, register!");
		String line;
		try {
			while (!socket.isClosed() && (line = reader.readLine()) != null) {
				MessageHandler msgHd = new MessageHandler(line, manager);
				msgHd.processIncomingMessage();
				if (msgHd.isNeedShowMessage()) {
					System.out.println(msgHd.getMessage());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}