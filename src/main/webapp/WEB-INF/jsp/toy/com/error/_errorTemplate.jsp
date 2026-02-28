<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <title>
        <spring:message code="${param.titleCode}" text="Error" />
    </title>

    <style>
        body { margin: 0; font-family: "Malgun Gothic", "Apple SD Gothic Neo", sans-serif; color: #111827; background: #f8fafc; }
        #wrapper { min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 24px; box-sizing: border-box; }
        .error-wrap { width: 100%; max-width: 560px; background: #fff; border: 1px solid #e5e7eb; border-radius: 8px; padding: 28px; box-sizing: border-box; text-align: center; }
        .error-msg { margin: 0; font-size: 28px; line-height: 1.4; }
        .error-text { margin: 16px 0 0; font-size: 18px; line-height: 1.6; color: #4b5563; }
        .button { margin-top: 28px; }
        .button a { display: inline-block; padding: 10px 16px; background: #111827; color: #fff; text-decoration: none; border-radius: 6px; font-weight: 600; }
    </style>
</head>

<body>
<div id="wrapper">

    <div class="error-wrap">
        <h2 class="error-msg">
            <spring:message code="${param.titleCode}" text="Error" />
        </h2>

        <p class="error-text">
            <spring:message code="${param.messageCode}" text="An unexpected error occurred." />
            <br/>
            <spring:message code="error.contact.admin" text="Please contact the administrator." />
        </p>

        <br/>
        <c:if test="${not empty exception}">
            <c:out value="${exception.message}" />
        </c:if>

        <div class="button">
            <a href="${pageContext.request.contextPath}/toy/admin/main.do">
                <spring:message code="button.goHome" text="Go to home" />
            </a>
        </div>
    </div>

</div>
</body>
</html>
