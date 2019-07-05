package com.client;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class ClientOutput implements Runnable {
	private ManagerClient manager;
	private BufferedOutputStream bufOut;
	
	public ClientOutput(ManagerClient manager, BufferedOutputStream bufOut) {
		this.manager = manager;
		this.bufOut = bufOut;
	}
	
	@Override
	public void run() {
		try (Scanner sc = new Scanner(System.in)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				
				if (!line.isEmpty()) {
					MessageHandler msgHd = new MessageHandler(line, manager);
					msgHd.processOutgoingMessage(manager.getId(), manager.getRole());
					if (msgHd.isCorrectMessage()) {
						try {
							bufOut.write(msgHd.getMessageBytes());
							bufOut.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}	
					} else {
						System.out.println(msgHd.getMessage());
					}
					
					if ("/e".equals(line)) {
						manager.terminateChat();
					}
	
				}
							}	
		} 
	}
}