package com.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;

public class ClientInput implements Runnable {
	private ManagerClient manager;
	private BufferedInputStream bufIn;
	private BufferedReader reader;
	
	public ClientInput(ManagerClient manager, BufferedInputStream bufIn, BufferedReader reader) {
		this.manager = manager;
		this.bufIn = bufIn;
		this.reader = reader;
	}
	
	@Override
	public void run() {
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				System.out.println("line " + line);
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