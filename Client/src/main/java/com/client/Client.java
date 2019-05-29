package com.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		try (Socket socket = new Socket("localhost", 8282);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			Scanner reader = new Scanner(new InputStreamReader(socket.getInputStream()))) {
			
			Thread tInput = new Thread(new ClientInput(reader));
			tInput.start();
			Thread tOutput = new Thread(new ClientOutput(writer));
			tOutput.start();
			
			try {
				tInput.join();
				tOutput.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	
	}

}
