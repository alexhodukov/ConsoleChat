<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="webjars/jquery/3.3.0/jquery.js"></script>
	<script type="text/javascript">

		$(document).ready(function() {
			
			var isFirstMsg = true;
			var isPolling = false;
			var idReceiver = 0;
			
			if ("${role}" == "AGENT") {
				getMessages();
			}
			
			function getMessages(){
			    $.ajax({ 
			    	type: 'GET',
			    	url: 'chat',
			    	dataType: 'json',
			    	// 			    	data: {
// 			    		message: message
// 					}, 
// 					complete: poll, 
// 					timeout: 4000,
					success: function(data){
						$.each(data, function(key, value) {
							if (idReceiver == 0) {
								idReceiver = value.idSender;
							}
							addMessageChat(value.nameSender + " : " + value.message);
							console.log('key ' + key + ', value ' + value.message);
						})
					},
					error: function (jqXHR, exception) {
				        var msg = '';
				        if (jqXHR.status === 0) {
				            msg = 'Not connect.\n Verify Network.';
				        } else if (jqXHR.status == 404) {
				            msg = 'Requested page not found. [404]';
				        } else if (jqXHR.status == 500) {
				            msg = 'Internal Server Error [500].';
				        } else if (exception === 'parsererror') {
				            msg = 'Requested JSON parse failed.';
				        } else if (exception === 'timeout') {
				            msg = 'Time out error.';
				        } else if (exception === 'abort') {
				            msg = 'Ajax request aborted.';
				        } else {
				            msg = 'Uncaught Error.\n' + jqXHR.responseText;
				        }
				        console.log(msg);
				    },
					complete: function() {
						getMessages();
					}
				});
			};
			
			function sendMessage(message) {
				$.ajax({
					type: 'POST',
					url: 'chat',
					data: {message: message
						},
					success: function() {
						console.log("success sendMessage()");
						if ("${role}" == "CLIENT" && isFirstMsg) {
							isFirstMsg = false;
							getMessages();
						}
					}
				});
			}
			
			
			$('#send').click(function() {
				var message = $('#message').val();
				if ("${role}" == "AGENT" && idReceiver < 1) {
					console.log("You haven't a client for discussion!");
				} else {
					addMessageChat("I am : " + message);
					sendMessage(message);
				}
			});
			
			function addMessageChat(message) {
				var chat = $('#chat');
				chat.val(chat.val() + message + "\n");
				chat.scrollTop(chat.prop('scrollHeight') - chat.height());
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