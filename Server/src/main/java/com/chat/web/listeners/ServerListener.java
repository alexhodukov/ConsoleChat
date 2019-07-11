package com.chat.web.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.chat.server.Server;

public class ServerListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent event) {
		System.out.println("ServerListener");
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		Server server = (Server) ctx.getBean("server", Server.class);
//		Server server = new Server();
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
