package com.client;

import java.util.Scanner;

public class ClientInput implements Runnable {
	private Scanner reader;
	
	public ClientInput(Scanner reader) {
		this.reader = reader;
	}
	
	@Override
	public void run() {
		while (reader.hasNextLine()) {
			System.out.println(reader.nextLine());
		}
	}
}