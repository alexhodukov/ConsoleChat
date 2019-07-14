<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="webjars/jquery/3.3.0/jquery.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#submit').click(function(e) {
				var name = $('#name');
				name.val(name.val().replace(/\s/g, ""));
				if (name.val() == "") {
					e.preventDefault();
					alert("Type name!")
				}
				
			});
		});
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
		<input type="text" name="name" id="name" required/>
		</p>	
		
   <p><input type="submit" value="Register" id="submit"></p>
	</form>

</body>
</html>