<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="webjars/jquery/3.3.0/jquery.js"></script>
	<script type="text/javascript">

		$(document).ready(function() {
			
			var isPolling = false;
			$('#result1').html("${idReceiver}");
			
			function sendMessage(message) {
				$.ajax({
					type: 'POST',
					url: 'chat',
					data: {message: message
						},
					success: function() {
						console.log("success sendMessage()");
					}
				});
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
							console.log('key ' + key + ', value ' + value);
						})
					}
				});
			};
			
			
			$('#send').click(function() {
				var message = $('#message').val();
				var chat = $('#chat');
				
				if ("${role}" == "AGENT" && "${idReceiver}" < 1) {
					console.log("You haven't a client for discussion!");
				} else {
					chat.val(chat.val() + message + "\n");
					chat.scrollTop(chat.prop('scrollHeight') - chat.height());
					sendMessage(message);
					getMessages();
				}
	
				isPolling = true;
// 				getMessages();
			});
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
		<textarea id="chat" rows="3" cols="100" readonly></textarea>
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