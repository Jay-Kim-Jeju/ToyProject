<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // memberList가 아직 세팅되지 않았다면 (최초 진입)
    if (request.getAttribute("memberList") == null) {
%>
<jsp:forward page="index.do" />
<%
        // forward 후에는 아래 HTML을 실행하지 않도록 바로 return
        return;
    }
%>

<html>
<head>
    <title>기본</title>
</head>
<body>
<h2>index.jsp 환영합니다.</h2>

<h3>Member List</h3>

<table border="1">
    <thead>
    <tr>
        <th>Username</th>
        <th>Email</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="m" items="${memberList}">
        <tr>
            <td>${m.username}</td>
            <td>${m.email}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>
