package com.client.Runner;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import com.client.Threads.InputThread;
import com.client.Threads.OutputThread;
import com.client.model.ManagerClient;

public class ClientRunner {

	public static void main(String[] args) {		
		try (Socket socket = new Socket("localhost", 8282);
			BufferedOutputStream bufOut = new BufferedOutputStream(socket.getOutputStream());
			BufferedInputStream bufIn = new BufferedInputStream(socket.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			
			ManagerClient manager = new ManagerClient(socket);			
			Thread tInput = new Thread(new InputThread(manager, reader, socket));
			tInput.start();
			Thread tOutput = new Thread(new OutputThread(manager, bufOut));
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
