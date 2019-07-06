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
		msg.setRoleReceiver(msg.getRoleSender() == Role.AGENT ? Role.CLIENT : Role.AGENT);
		
		
		
		if (msg.equals(MessageUtils.LEAVE)) {
			session.setAttribute("idReceiver", 0);
			if (roleSender == Role.AGENT) {
				session.setAttribute("idChat", 0);
			}
			httpMsgHandler.leaveConversation(idSender, roleSender, idChat);
			msg.setMessage(msg.getNameSender() + " " + MessageUtils.LEAVE_CHAT);
			
			httpMsgHandler.createServiceMessageLeaveExit(msg, MessageType.EXT, "leave");
		}
		
		if (msg.equals(MessageUtils.EXIT)) {
			httpMsgHandler.exit(idSender, roleSender, idChat);
			msg.setMessage(msg.getNameSender() + " " + MessageUtils.LEAVE_CHAT);
			
			httpMsgHandler.createServiceMessageLeaveExit(msg, MessageType.EXT, "exit");
		}
		
		
		
		
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
		String nameSender = (String) session.getAttribute("name");

		
		if (roleSender == Role.CLIENT && idReceiver == 0 && httpMsgHandler.isExistAgentInChat(idChat)) {
			idReceiver = httpMsgHandler.getIdAgentByIdChat(idChat);
			session.setAttribute("idReceiver", idReceiver);
		}
		
		if ((roleSender == Role.CLIENT && idReceiver > 0) || 
			(roleSender == Role.CLIENT && httpMsgHandler.isAgentConnecting(idChat)) ||
			roleSender == Role.AGENT) {
			
			System.out.println("NOW in Async. role " + roleSender + ", name " + nameSender + ", idSender " + idSender + ", idChat " + idChat);
			
			AsyncContext async = req.startAsync();
			async.getResponse().setContentType("application/json");
			Runnable r = () -> {
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
					String json = map.writeValueAsString(listMsg);
					System.out.println("json " + json);
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
		} else {
			System.out.println("idChat " + idChat);
			System.out.println("is Agent Connecting? " + httpMsgHandler.isAgentConnecting(idChat));
		}
	}
}