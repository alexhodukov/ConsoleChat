package com.client;

import java.io.UnsupportedEncodingException;

public class MessageHandler {
	private String src;
	
	public MessageHandler(String src) {
		this.src = src;
	}
	
	public void processIncomingMessage() {
		
	}
	
	public void processOutgoingMessage() {
		src = "REG_" + src + "\n";
	}
	
	public boolean isCorrectMessage() {
		return true;
	}
	
	public byte[] getMessage() {
		byte[] ar = {};
		try {
			ar = src.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ar;
	}
}
