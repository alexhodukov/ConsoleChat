package com.chat.web.servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.chat.enums.Role;
import com.chat.handlers.HttpMessageHandler;

public class RegisterServlet extends HttpServlet {
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Role role = Role.valueOf(req.getParameter("role").toUpperCase());
		String name = req.getParameter("name");
		
		
		HttpSession session = req.getSession();		
		HttpMessageHandler httpMsgHandler = (HttpMessageHandler) req.getServletContext().getAttribute("httpMsgHandler");
		
		int idChat = 0;
		int idUser = httpMsgHandler.registerUser(role, name);
		if (role == Role.CLIENT) {
			idChat = httpMsgHandler.createChat(idUser);
		} else {
			httpMsgHandler.doFreeAgent(idUser);
		}
		
		session.setAttribute("role", role);
		session.setAttribute("name", name);
		session.setAttribute("idUser", idUser);
		session.setAttribute("idChat", idChat);		
		session.setAttribute("idReceiver", 0);
		
        RequestDispatcher disp = req.getRequestDispatcher("/chat.jsp");
		disp.forward(req, resp);

	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
}
