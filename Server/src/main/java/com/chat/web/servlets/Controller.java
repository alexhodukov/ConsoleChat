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
import com.chat.model.Message;

public class Controller extends HttpServlet {
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();		
		HttpMessageHandler httpMsgHandler = (HttpMessageHandler) req.getServletContext().getAttribute("httpMsgHandler");
		
		Message msg = new Message(req.getParameter("message"));
		
		int idSender = (int) session.getAttribute("idUser");
		int idReceiver = (int) session.getAttribute("idReceiver");
		int idChat = (int) session.getAttribute("idChat");
		Role roleSender = (Role) session.getAttribute("role");
		String nameSender = (String) session.getAttribute("name");
		
		msg.setIdSender(idSender);
		msg.setIdReceiver(idReceiver);
		msg.setIdChat(idChat);
		msg.setRoleSender(roleSender);
		msg.setNameSender(nameSender);
		
		if (roleSender == Role.CLIENT && idReceiver == 0 && !httpMsgHandler.isAgentConnecting(idChat)) {
			httpMsgHandler.connectAgent(msg);
		}
		
		httpMsgHandler.process(msg);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}
}