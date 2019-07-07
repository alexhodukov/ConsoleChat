<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="webjars/jquery/3.3.0/jquery.js"></script>
	<script type="text/javascript">

		$(document).ready(function() {
			
			var id = "${idUser}";
			var idChat = "${idChat}";
			var role = "${role}";
			var name = "${name}";
			var idReceiver = 0;
			
			
			var str_no_interlocutor_agent = "You can't leave this chat, because you haven't an agent for conversation!";
			var str_no_interlocutor_client = "You haven't a client for conversation. Your message isn't sent!";
			var str_no_chat = "You aren't in any chat!";
			var str_exit = "/e";
			var str_leave = "/l";
			
			var isIAmExiting = false;
			var isFirstMsg = true;
			var polling = false;
			
			var connecting = true;
			
			if (role == "AGENT") {
				polling = true;
				getMessages();
			}
			
			function getMessages(){
			    $.ajax({ 
			    	type: 'GET',
			    	url: 'chat?id=' + id + '&idChat=' + idChat + '&idReceiver=' + idReceiver + '&role=' + role + '&name=' + name,
			    	dataType: 'json',
					success: function(data){
						$.each(data, function(key, value) {	
							switch (value.msgType) {
							case "LEV" :
								console.log("LEV");
								addMessageChat(value.nameSender + " : " + value.message);
								polling = false;
								break;
								
							case "EXT" :
								if (isIAmExiting) {
									exit();	
								} else {
									if (role == "CLIENT") {
										polling = false;
										isFirstMsg = true;
									}
									addMessageChat(value.nameSender + " : " + value.message);
									idReceiver = 0;
								}
								console.log("EXT " + value.message);
								break;
								
							case "MSG" :
								console.log("MSG");
								addMessageChat(value.nameSender + " : " + value.message);
								if (idReceiver == 0) {
									idReceiver = value.idSender;
								}
								break;
								
							case "REG" :
								break;
								
							case "SRV" :
								addMessageChat("---SERVICE--- " + value.message);
								break;
							}
							
							
						})
					},
					error: function (jqXHR, exception) {
				        if (jqXHR.status === 0) {
				            connecting = false;
				        } 
				    },
					complete: function() {
						if (connecting && polling) {
							getMessages();
						}
					}
				});
			};
			
			function sendMessage(message) {
				$.ajax({
					type: 'POST',
					url: 'chat',
					data: {message: message,
						id : id,
						idReceiver : idReceiver,
						idChat : idChat,
						role : role,
						name : name
						},
					success: function() {
						if (role == "CLIENT" && isFirstMsg) {
							isFirstMsg = false;
							polling = true;
							getMessages();
						}
					}
				});
			}
			
			function leave() {
				if (idReceiver == 0) {
					if (role == "CLIENT") {
						addMessageChat(str_no_interlocutor_agent);	
					} else {
						addMessageChat(str_no_chat);
					}
				} else {
					idReceiver = 0;
					polling = false;	
				}
			}
			
			
			function exit() {
				idReceiver = 0;
				polling = false;
				sendMessageExit();
			}
			
			
			$('#send').click(function() {
				var message = $('#message').val();
				
				if (role == "AGENT" && idReceiver < 1) {
					addMessageChat(str_no_interlocutor_client);
				} else {
					if (message == str_exit) {
						isIAmExiting = true;	
					} else {
						addMessageChat("I am : " + message);
					}
					sendMessage(message);
				}
				
				$('#message').val("");
				if (message == str_leave) {
					leave();
				}
			});
			
			function addMessageChat(message) {
				var chat = $('#chat');
				chat.val(chat.val() + message + "\n");
				chat.scrollTop(chat.prop('scrollHeight') - chat.height());
			}
			
			
			function sendMessageExit() {
				var message = "exit";
				$.ajax({
					type: 'DELETE',
					url: 'chat?message=exit&id=' + id +'&idChat=' + idChat + '&role=' + role,
					data: {message: message
						},
					complete: function() {
						window.location.href = "logout";
					}
				});
			}
			
		});
		
	</script>
	
	<title>Insert title here</title>
</head>

<body>
	<center>
		<h1>Please, write a message</h1>
	</center>

	<form action="register" method="post">
		<p>
		<textarea id="chat" rows="20" cols="100" readonly></textarea>
		</p>

	    <p>
	   	<label>Enter message : </label>
		<input type="text" name="message" id="message"/>
		</p>	
		
   <p><input type="button" value="Отправить" id="send"></p>
   <p><span id="result1"></span>
	</form>
<%-- 	<p><c:out value="${name}"/> --%>
</body>
</html>