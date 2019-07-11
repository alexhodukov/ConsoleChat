package com.chat.web.servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.chat.enums.Role;
import com.chat.handlers.HttpMessageHandler;
import com.chat.server.Server;

@Configurable
public class RegisterServlet extends HttpServlet {
	
	@Autowired
	private Server server;
	
	private HttpMessageHandler httpMsgHandler;
	
	@Override
	public void init() throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		httpMsgHandler = server.getHttpMsgHandler();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Role role = Role.valueOf(req.getParameter("role").toUpperCase());
		String name = req.getParameter("name");

		
		int idChat = 0;
		int idUser = httpMsgHandler.registerUser(role, name);
		if (role == Role.CLIENT) {
			idChat = httpMsgHandler.createChat(idUser);
		} else {
			httpMsgHandler.doFreeAgent(idUser);
		}
		
		req.setAttribute("role", role);
		req.setAttribute("name", name);
		req.setAttribute("idUser", idUser);
		req.setAttribute("idChat", idChat);		
		req.setAttribute("idReceiver", 0);
		
        RequestDispatcher disp = req.getRequestDispatcher("/chat.jsp");
		disp.forward(req, resp);

	}

}
