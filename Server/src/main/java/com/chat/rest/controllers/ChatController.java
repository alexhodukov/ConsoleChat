package com.chat.rest.controllers;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chat.model.Agent;
import com.chat.model.Chat;
import com.chat.model.Client;
import com.chat.model.Message;
import com.chat.rest.dto.UserDto;
import com.chat.server.Server;

@RestController
@RequestMapping("/chat")
public class ChatController {
	
	private Server server;

	public ChatController(Server server) {
		this.server = server;
	}
	
	@PostMapping("/agent")
	public ResponseEntity<Integer> createAgent(@Valid @RequestBody UserDto user) {
		int id = server.getManager().createHttpAgent(user.getName());
		server.getManager().doFreeAgent(id);
		return new ResponseEntity<Integer>(id, HttpStatus.CREATED);
	}
	
	@PostMapping("/client")
	public ResponseEntity<Integer> createClient(@Valid @RequestBody UserDto user) {
		int id = server.getManager().createHttpClient(user.getName());
		return new ResponseEntity<Integer>(id, HttpStatus.CREATED);
	}
	
	@PostMapping("/sendMessage")
	public ResponseEntity<Void> createMessage(@Valid @RequestBody Message msg) {
		server.getHttpMsgHandler().processMessage(msg);
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}
	
	@GetMapping("/agents")
	public ResponseEntity<List<Agent>> listAllAgents(
			@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "3") int pageSize) {
		
		List<Agent> allAgents = server.getManager().getListAgents();
		PagedListHolder<Agent> page = new PagedListHolder<>(allAgents);
		page.setPage(pageNumber);
		page.setPageSize(pageSize);
		List<Agent> list = page.getPageList();
		if (list.isEmpty() || pageSize == 0 || (pageNumber + 1) > page.getPageCount()) {
			return new ResponseEntity<List<Agent>>(Collections.emptyList(), HttpStatus.NOT_FOUND);	
		}
		return new ResponseEntity<List<Agent>>(list, HttpStatus.OK);
	}
	
	@GetMapping("/freeAgents")
	public ResponseEntity<List<Agent>> listFreeAgents(
			@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "3") int pageSize) {
		
		List<Agent> allAgents = server.getManager().getFreeAgents();
		PagedListHolder<Agent> page = new PagedListHolder<>(allAgents);
		page.setPage(pageNumber);
		page.setPageSize(pageSize);
		List<Agent> list = page.getPageList();
		if (list.isEmpty() || pageSize == 0 || (pageNumber + 1) > page.getPageCount()) {
			return new ResponseEntity<List<Agent>>(Collections.emptyList(), HttpStatus.NOT_FOUND);	
		}
		return new ResponseEntity<List<Agent>>(list, HttpStatus.OK);
	}
	
	@GetMapping("/countFreeAgents")
	public ResponseEntity<Integer> coutnFreeAgents() {
		Integer count = server.getManager().getCountFreeAgents();
		if (count == 0) {
			return new ResponseEntity<Integer>(count, HttpStatus.NOT_FOUND);	
		}
		return new ResponseEntity<Integer>(count, HttpStatus.OK);
	}
	
	@GetMapping("/agent/{id}")
	public ResponseEntity<Agent> agentById(@PathVariable("id") Integer id) {
		Agent agent = server.getManager().getAgentById(id);
		if (agent == null) {
			return new ResponseEntity<Agent>(agent, HttpStatus.NOT_FOUND);	
		}
		return new ResponseEntity<Agent>(agent, HttpStatus.OK);
	}
	
	@GetMapping("/clients")
	public ResponseEntity<List<Client>> listAllClients(
			@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "3") int pageSize) {
		
		List<Client> allClients = server.getManager().getListClients();
		PagedListHolder<Client> page = new PagedListHolder<>(allClients);
		page.setPage(pageNumber);
		page.setPageSize(pageSize);
		List<Client> list = page.getPageList();
		if (list.isEmpty() || pageSize == 0 || (pageNumber + 1) > page.getPageCount()) {
			return new ResponseEntity<List<Client>>(Collections.emptyList(), HttpStatus.NOT_FOUND);	
		}
		return new ResponseEntity<List<Client>>(list, HttpStatus.OK);
	}
	
	@GetMapping("/client/{id}")
	public ResponseEntity<Client> clientById(@PathVariable("id") Integer id) {
		Client client = server.getManager().getClientById(id);
		if (client == null) {
			return new ResponseEntity<Client>(client, HttpStatus.NOT_FOUND);	
		}
		return new ResponseEntity<Client>(client, HttpStatus.OK);
	}
	
	@GetMapping("/publicChats")
	public ResponseEntity<List<Chat>> listAllPublicChats(
			@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "3") int pageSize) {
		
		List<Chat> allChats = server.getManager().getPublicChats();
		PagedListHolder<Chat> page = new PagedListHolder<>(allChats);
		page.setPage(pageNumber);
		page.setPageSize(pageSize);
		List<Chat> list = page.getPageList();
		if (list.isEmpty() || pageSize == 0 || (pageNumber + 1) > page.getPageCount()) {
			return new ResponseEntity<List<Chat>>(Collections.emptyList(), HttpStatus.NOT_FOUND);	
		}
		return new ResponseEntity<List<Chat>>(list, HttpStatus.OK);
	}
	
	@GetMapping("/chats/{id}")
	public ResponseEntity<Chat> chatById(@PathVariable("id") Integer id) {
		Chat chat = server.getManager().getChatById(id);
		if (chat == null) {
			return new ResponseEntity<Chat>(chat, HttpStatus.NOT_FOUND);	
		}
		return new ResponseEntity<Chat>(chat, HttpStatus.OK);
	}
	
	@GetMapping("/messages/{id}")
	public ResponseEntity<List<Message>> listAllMessages(@PathVariable("id") Integer id) {
		List<Message> list = new ArrayList<>(server.getManager().getHttpMessages(id));
		if (list.isEmpty()) {
			return new ResponseEntity<List<Message>>(list, HttpStatus.NOT_FOUND);	
		}
		return new ResponseEntity<List<Message>>(list, HttpStatus.OK);
	}
	
}
