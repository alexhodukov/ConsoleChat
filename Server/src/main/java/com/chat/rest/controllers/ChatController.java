package com.chat.rest.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chat.dto.UserDto;
import com.chat.enums.Role;
import com.chat.model.Agent;
import com.chat.model.Chat;
import com.chat.model.Client;
import com.chat.model.Message;
import com.chat.rest.dto.ChatDto;
import com.chat.server.Server;

@RestController
@RequestMapping("/chat")
public class ChatController {
	
	private Server server;

	public ChatController(Server server) {
		this.server = server;
	}

	@PostMapping("chat")
	public ResponseEntity<Void> createArticle(@Valid @RequestBody ChatDto article) {
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}
	
	@GetMapping("/clients")
	public ResponseEntity<List<Client>> listAllClients() {
		List<Client> list = server.getManager().getListClients();
		if (list.isEmpty()) {
			return new ResponseEntity<List<Client>>(list, HttpStatus.NOT_FOUND);	
		}
		return new ResponseEntity<List<Client>>(list, HttpStatus.OK);
	}
	
	@GetMapping("/agents")
	public ResponseEntity<List<Agent>> listAllAgents() {
		List<Agent> list = server.getManager().getListAgents();
		if (list.isEmpty()) {
			return new ResponseEntity<List<Agent>>(list, HttpStatus.NOT_FOUND);	
		}
		return new ResponseEntity<List<Agent>>(list, HttpStatus.OK);
	}
	
	@GetMapping("/freeAgents")
	public ResponseEntity<List<Agent>> listFreeAgents() {
		List<Agent> list = server.getManager().getFreeAgents();
		if (list.isEmpty()) {
			return new ResponseEntity<List<Agent>>(list, HttpStatus.NOT_FOUND);	
		}
		return new ResponseEntity<List<Agent>>(list, HttpStatus.OK);
	}
	
	@GetMapping("/agent/{id}")
	public ResponseEntity<Agent> agentById(@PathVariable("id") Integer id) {
		Agent agent = server.getManager().getAgentById(id);
		if (agent == null) {
			return new ResponseEntity<Agent>(agent, HttpStatus.NOT_FOUND);	
		}
		return new ResponseEntity<Agent>(agent, HttpStatus.OK);
	}
	
	@PostMapping("/agent")
	public ResponseEntity<Integer> createAgent(@Valid @RequestBody UserDto user) {
		int id = server.getManager().createHttpAgent(user.getName());
		server.getManager().doFreeAgent(id);
		return new ResponseEntity<Integer>(id, HttpStatus.CREATED);
	}
	
	@GetMapping("/countFreeAgents")
	public ResponseEntity<Integer> coutnFreeAgents() {
		Integer count = server.getManager().getCountFreeAgents();
		if (count == 0) {
			return new ResponseEntity<Integer>(count, HttpStatus.NOT_FOUND);	
		}
		return new ResponseEntity<Integer>(count, HttpStatus.OK);
	}
	
	@GetMapping("/client/{id}")
	public ResponseEntity<Client> clientById(@PathVariable("id") Integer id) {
		Client client = server.getManager().getClientById(id);
		if (client == null) {
			return new ResponseEntity<Client>(client, HttpStatus.NOT_FOUND);	
		}
		return new ResponseEntity<Client>(client, HttpStatus.OK);
	}
	
	@PostMapping("/client")
	public ResponseEntity<Integer> createClient(@Valid @RequestBody UserDto user) {
		int id = server.getManager().createHttpClient(user.getName());
		return new ResponseEntity<Integer>(id, HttpStatus.CREATED);
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
	
	@PostMapping("/sendMessage")
	public ResponseEntity<Void> createMessage(@Valid @RequestBody Message msg) {
		server.getHttpMsgHandler().processMessage(msg);
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

	
}
