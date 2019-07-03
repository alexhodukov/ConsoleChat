<!-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> -->

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Bot</title>
</head>
<body>
<!-- <section> -->
<!--     <h3>Bot info</h3> -->
<%--     <jsp:useBean id="bot" scope="request" type="ru.javawebinar.topjava.model.Bot"/> --%>
<!--     <tr> -->
<%--         <td>ID: ${bot.id} | Name: ${bot.name} | Serial number: ${bot.serial}</td> --%>
<!--         <td><a href="bot?action=update">Update</a></td> -->
<!--     </tr> -->
<!-- </section> -->
<ul>
            <c:forEach var="user" items="${users}">
                <li><c:out value="${user}" /></li>
            </c:forEach>
        </ul>
</body>
</html>