package com.chat.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.chat.handlers.HttpMessageHandler;
import com.chat.server.Server;

@ComponentScan("com.chat")
@Configuration
public class AppConfig {
	
	@Bean
	public Server server() {
		Server server = new Server();
		Runnable r = () -> {
			server.start();
		};
		Thread t = new Thread(r);
		t.setName("Thread server starting");
		t.setDaemon(true);
		t.start();
		return server;
	}
	
	@Bean
	public HttpMessageHandler httpMessageHundler(Server server) {
		return server.getHttpMsgHandler();
	}
	
}
