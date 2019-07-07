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

import com.chat.enums.MessageType;
import com.chat.enums.Role;
import com.chat.handlers.HttpMessageHandler;
import com.chat.model.Message;
import com.chat.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ControllerServlet extends HttpServlet {
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();		
		HttpMessageHandler httpMsgHandler = (HttpMessageHandler) req.getServletContext().getAttribute("httpMsgHandler");

		Message msg = new Message(req.getParameter("message"));
		int idSender = Integer.parseInt(req.getParameter("id"));
		int idChat = Integer.parseInt(req.getParameter("idChat"));
		Role roleSender = Role.valueOf(req.getParameter("role"));		

		switch (msg.getMessage()) {
		case "exit" : {
			httpMsgHandler.exit(idSender, roleSender, idChat);
			session.invalidate();
		} break;
		
		case "leave" : {
			httpMsgHandler.leaveConversation(idSender, roleSender, idChat);
		} break;
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
		HttpMessageHandler httpMsgHandler = (HttpMessageHandler) req.getServletContext().getAttribute("httpMsgHandler");
		
		Message msg = new Message(req.getParameter("message"));
		
		int idSender = Integer.parseInt(req.getParameter("id"));
		int idReceiver = Integer.parseInt(req.getParameter("idReceiver"));
		int idChat = Integer.parseInt(req.getParameter("idChat"));
		Role roleSender = Role.valueOf(req.getParameter("role"));
		String nameSender = req.getParameter("name");
		
		msg.setIdSender(idSender);
		msg.setIdReceiver(idReceiver);
		msg.setIdChat(idChat);
		msg.setRoleSender(roleSender);
		msg.setNameSender(nameSender);
		msg.setRoleReceiver(msg.getRoleSender() == Role.AGENT ? Role.CLIENT : Role.AGENT);
		
		if (roleSender == Role.CLIENT && idReceiver == 0 && !httpMsgHandler.isAgentConnecting(idChat)) {
			httpMsgHandler.connectAgent(msg);
		}
		
		
		switch(msg.getMessage()) {
		case MessageUtils.LEAVE : {
			System.out.println("DoPost.Leave");
			httpMsgHandler.createServiceMessageLeaveExit(msg, MessageType.LEV, "leave");
			
			msg.setMsgType(MessageType.LEV);
			msg.setMessage(msg.getNameSender() + " " + MessageUtils.LEAVE_CHAT);
			httpMsgHandler.process(msg);
		} break;
		
		case MessageUtils.EXIT : {
			httpMsgHandler.createServiceMessageLeaveExit(msg, MessageType.EXT, "exit");
			
			if (msg.getIdReceiver() > 0) {
				msg.setMsgType(MessageType.EXT);
				msg.setMessage(msg.getNameSender() + " " + MessageUtils.LEAVE_CHAT);
				httpMsgHandler.process(msg);	
			}
		} break;
		
		default : {
			httpMsgHandler.process(msg);
		} break;
		}				
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();		
		HttpMessageHandler httpMsgHandler = (HttpMessageHandler) req.getServletContext().getAttribute("httpMsgHandler");
		
		int idSender = Integer.parseInt(req.getParameter("id"));
		int idReceiver = Integer.parseInt(req.getParameter("idReceiver"));
		int idChat = Integer.parseInt(req.getParameter("idChat"));
		Role roleSender = Role.valueOf(req.getParameter("role"));
		String nameSender = req.getParameter("name");


		
		if (roleSender == Role.CLIENT && idReceiver == 0 && httpMsgHandler.isExistAgentInChat(idChat)) {
			idReceiver = httpMsgHandler.getIdAgentByIdChat(idChat);
		}
		
		if ((roleSender == Role.CLIENT && idReceiver > 0) || 
			(roleSender == Role.CLIENT && httpMsgHandler.isAgentConnecting(idChat)) ||
			roleSender == Role.AGENT) {
			
			int newIdReceiver = idReceiver;
			
			AsyncContext async = req.startAsync();
			async.getResponse().setContentType("application/json");
			Runnable r = () -> {
				PrintWriter out;
				try {	
					out = async.getResponse().getWriter();
					List<Message> listMsg = new ArrayList<>(httpMsgHandler.getMessages(idSender));

					if (roleSender == Role.AGENT && newIdReceiver == 0) {
						if (!listMsg.isEmpty()) {
							Message msg = listMsg.get(0);
							int idSend = msg.getIdSender();
							int idCh = msg.getIdChat();
							session.setAttribute("idReceiver", idSend);
							session.setAttribute("idChat", idCh);
						}
					}
					for (Message m : listMsg) {
						m.convertToWeb();
					}
					ObjectMapper map = new ObjectMapper();
					String json = map.writeValueAsString(listMsg);
//					System.out.println("name " + nameSender + ", role " + roleSender + ", json " + json);
					out.write(json);
				} catch (IOException e) {
					e.printStackTrace();
				}
				async.complete();
			};
			
			Thread t = new Thread(r);
			t.setName("Thread async servlet");
			t.setDaemon(true);
			async.start(t);
		} 
	}
}