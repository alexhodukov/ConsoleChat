package com.chat.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class IncrementUtil {;
	private static AtomicInteger incIdUser = new AtomicInteger(); 
	private static AtomicInteger incIdChat = new AtomicInteger();
	
	public static int generateIdUser() {
		return incIdUser.incrementAndGet();
	}
	
	public static int generateIdChat() {
		return incIdChat.incrementAndGet();
	}
}
