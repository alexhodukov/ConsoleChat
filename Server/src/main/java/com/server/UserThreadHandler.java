package com.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class UserThreadHandler implements Runnable {
	private Server server;
	private TypeUser type;
	private String name;
	
	private static String[] regClient = {"/r", "c"};
	private static String[] regAgent = {"/r", "a"};
	private static String leave = "/leave";
	private static String exit = "/exit";
	
	private Socket socket;
	private Socket interlocutor;
	
	public UserThreadHandler(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		this.type = TypeUser.GUEST;
	}
	
	@Override
	public void run() {		
		try (InputStream input = socket.getInputStream();
			OutputStream output = socket.getOutputStream()) {
			
			Scanner reader = new Scanner(input, "UTF-8");
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
			writer.println("Hello! \"/exit\" - exit, \"/leave\" - leave, \"/register agent(client)\"");
			boolean done = false;
			String name = "";
			while (!done && reader.hasNextLine()) {
				String line = reader.nextLine();
				System.out.println("line " + line);
				String[] tokens = line.split(" ");
				if (type == TypeUser.AGENT) {
					OutputStream outCl = server.client.getSocket().getOutputStream();
					PrintWriter writerCl = new PrintWriter(new OutputStreamWriter(outCl, "UTF-8"), true);
					writerCl.println("agent " + name + ": " + line);
				}
				if (type == TypeUser.CLIENT) {
					OutputStream outAg = server.agent.getSocket().getOutputStream();
					PrintWriter writerAg = new PrintWriter(new OutputStreamWriter(outAg, "UTF-8"), true);
					writerAg.println(name + ": " + line);
				}
				if (tokens.length > 1 && regAgent[0].equals(tokens[0]) && regAgent[1].equals(tokens[1])) {
					for (int i = 2; i < tokens.length; i++) {
						name += tokens[i];
					}
					server.agent = new Agent(socket, name);
					type = TypeUser.AGENT;
					this.name = name;
//					server.registerAgent(new Agent(socket, name));
					System.out.println("Register agent " + name);
				}
				if (tokens.length > 1 && regClient[0].equals(tokens[0]) && regClient[1].equals(tokens[1])) {
					for (int i = 2; i < tokens.length; i++) {
						name += tokens[i];
					}
					server.client = new Client(socket, name);
					type = TypeUser.CLIENT;
					this.name = name;
					System.out.println("Register client " + name);
				}
//			Scanner reader = new Scanner(input, "UTF-8");
//			PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
//			writer.println("Hello! \"/exit\" - exit, \"/leave\" - leave, \"/register agent(client)\"");
//			boolean done = false;
//			boolean isCreateConnection = false;
//			Connection con = null;
//			Client client;
//			String name = "";
//			while (!done && reader.hasNextLine()) {
////				String line = reader.nextLine();
//				String line = "";
//				System.out.println("line " + line);
//				String[] tokens = line.split(" ");
//				if (tokens.length > 1 && regAgent[0].equals(tokens[0]) && regAgent[1].equals(tokens[1])) {
//					for (int i = 2; i < tokens.length; i++) {
//						name += tokens[i];
//					}
//					server.registerAgent(new Agent(incoming, name));
//					System.out.println("Register agent " + name);
//					System.out.println("Agent is closed " + incoming.isClosed());
//				}
//				if (isCreateConnection) {
//					if (server.isExistFreeAgents()) {
//						Agent agent = server.getFreeAgent();
//						con.setAgent(agent);
//					}
//					con.startMeeting();
//					if (con.isLeave()) {
//						server.registerAgent(con.disconnectAgent());
//					}
//					if (con.isExit()) {
//						done = true;
//					}
//				}
//				if (tokens.length > 1 && regClient[0].equals(tokens[0]) && regClient[1].equals(tokens[1])) {
//					for (int i = 2; i < tokens.length; i++) {
//						name += tokens[i];
//					}
//					client = new Client(incoming, name);
//					isCreateConnection = true;
//					con = new Connection(client);
//					System.out.println("Register client " + name);
//					System.out.println("Client is closed " + incoming.isClosed());
//				}
				
//				writer.println("Echo: " + line + "   ---   port " + incoming.getPort());
				
//				if (line.trim().equals("/leave")) {
//					done = true;
//				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private boolean equalsArrays(String[] ar) {
		boolean equals = true;
		int i = 0;
		for (String token : ar) {
			if (!regAgent[i].equals(token)) {
				equals = false;
			}
		}
		return equals;
	}
	
}
