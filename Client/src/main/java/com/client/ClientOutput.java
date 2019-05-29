package com.client;

import java.io.PrintWriter;
import java.util.Scanner;

public class ClientOutput implements Runnable {
	private PrintWriter pw;
	
	public ClientOutput(PrintWriter pw) {
		this.pw = pw;
	}
	
	@Override
	public void run() {
		try (Scanner sc = new Scanner(System.in)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				pw.println(line);
			}	
		} 
	}
}