package com.chat.web.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.chat.server.Server;

public class ServerListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent event) {
		Server server = new Server();
		event.getServletContext().setAttribute("msgHandler", server.getMsgHandler());
		event.getServletContext().setAttribute("httpMsgHandler", server.getHttpMsgHandler());
		
		Runnable r = () -> {
			server.start();
		};
		Thread t = new Thread(r);
		t.setName("Thread server starting");
		t.setDaemon(true);
		t.start();
	}
	
	@Override
		public void contextDestroyed(ServletContextEvent event) {
			
		}
}
