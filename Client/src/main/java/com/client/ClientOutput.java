package com.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientOutput implements Runnable {
	private Socket socket;
	private PrintWriter pw;
	
	public ClientOutput(Socket socket, PrintWriter pw) {
		this.socket = socket;
		this.pw = pw;
	}
	
	@Override
	public void run() {
		try (Scanner sc = new Scanner(System.in)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				pw.println(line);
				if ("/exit".equals(line)) {
					try {
						socket.close();
						System.exit(0);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}	
		} 
	}
}