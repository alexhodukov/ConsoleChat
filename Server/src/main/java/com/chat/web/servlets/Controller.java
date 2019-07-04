package com.chat.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.chat.enums.Role;
import com.chat.handlers.HttpMessageHandler;
import com.chat.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		HttpSession session = req.getSession();		
		HttpMessageHandler httpMsgHandler = (HttpMessageHandler) req.getServletContext().getAttribute("httpMsgHandler");
		
		int idSender = (int) session.getAttribute("idUser");
		int idReceiver = (int) session.getAttribute("idReceiver");
		int idChat = (int) session.getAttribute("idChat");
		Role roleSender = (Role) session.getAttribute("role");

		
		if (roleSender == Role.CLIENT && idReceiver == 0 && httpMsgHandler.isExistAgentInChat(idChat)) {
			idReceiver = httpMsgHandler.getIdAgentByIdChat(idChat);
			session.setAttribute("idReceiver", idReceiver);
		}
		
		if ((roleSender == Role.CLIENT && idReceiver > 0) || roleSender == Role.AGENT) {
			AsyncContext async = req.startAsync();
			async.start(() -> {
				PrintWriter out;
				try {
					
					out = async.getResponse().getWriter();
					List<Message> listMsg = new ArrayList<>(httpMsgHandler.getMessages(idSender));
					List<String> listMsgStr = new ArrayList<>();
					if (roleSender == Role.AGENT && (int) session.getAttribute("idReceiver") == 0) {
						if (!listMsg.isEmpty()) {
							Message msg = listMsg.get(0);
							int idSend = msg.getIdSender();
							int idCh = msg.getIdChat();
							session.setAttribute("idReceiver", idSend);
							session.setAttribute("idChat", idCh);
						}
					}
					for (Message m : listMsg) {
						listMsgStr.add(m.getMessage());
					}
					ObjectMapper map = new ObjectMapper();
					String json = map.writeValueAsString(listMsgStr);
					out.write(json);
				} catch (IOException e) {
					e.printStackTrace();
				}
				async.complete();
				System.out.println("End method async");
			});	
		}
		
		
		
	}
}