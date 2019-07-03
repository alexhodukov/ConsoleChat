<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="webjars/jquery/3.3.0/jquery.js"></script>
	<script type="text/javascript">
// 		$(document).ready(function() {
// 			$('#submit').click(function() {
// 				var name = $('#name').val();
// 				var role = $('#role option:selected').val();
// 				$.ajax({
// 					type: 'POST',
// 					url: 'register',
// 					data: {name: name,
// 						role: role},
// 					success: function(result) {
// 						$('#result1').html(result);
// 					}
// 				});
// 			});
// 		});
	</script>
	<title>Registration page</title>
</head>

<body>
	<center>
		<h1>Welcome to Dynamic Web Chat. Please register.</h1>
	</center>

	<form action="register" method="post">
		<p>
		<label>Select user type : </label>
		<select size="1" name="role" id="role">
		    <option value="agent">Agent</option>
		    <option value="client">Client</option>
	   </select>
	   </p>
	   <p>
	   	<label>Enter name : </label>
		<input type="text" name="name" id="name"/>
		</p>	
		
   <p><input type="submit" value="Отправить" id="submit"></p>
	</form>

</body>
</html>