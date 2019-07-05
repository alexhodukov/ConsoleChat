package com.client;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientInput implements Runnable {
	private ManagerClient manager;
	private BufferedReader reader;
	
	public ClientInput(ManagerClient manager, BufferedReader reader) {
		this.manager = manager;
		this.reader = reader;
	}
	
	@Override
	public void run() {
		String line;
		try {
			while ((line = reader.readLine()) != null) {
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