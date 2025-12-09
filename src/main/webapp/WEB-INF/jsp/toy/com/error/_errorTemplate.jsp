<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2025-12-09
  Time: 오전 4:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">
<head>
    <meta charset="UTF-8" />
    <title>
        <spring:message code="${param.titleCode}" text="Error" />
    </title>

    <!-- Same resources as root error.jsp -->
    <link rel="icon" href="${pageContext.request.contextPath}/images/com/favicon.ico">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mw/import.css?jsCssVer=${jsCssVer}" />

    <script src="${pageContext.request.contextPath}/js/com/jquery-3.4.1.js"></script>
    <script src="${pageContext.request.contextPath}/js/com/jquery-ui-1.12.1.js"></script>
    <script src="${pageContext.request.contextPath}/js/com/swiper-4.5.0.js"></script>
    <script src="${pageContext.request.contextPath}/js/mw/html-func.js?jsCssVer=${jsCssVer}"></script>
    <script src="${pageContext.request.contextPath}/js/mw/html-comm.js?jsCssVer=${jsCssVer}"></script>

</head>

<body>
<div id="wrapper">

    <div class="error-wrap">
        <img class="icon" src="${pageContext.request.contextPath}/images/com/error.png" alt="error" />

        <h2 class="error-msg">
            <spring:message code="${param.titleCode}" text="Error" />
        </h2>

        <p class="error-text">
            <spring:message code="${param.messageCode}"
                            text="An unexpected error occurred." />
            <br/>
            <spring:message code="error.contact.admin"
                            text="Please contact the administrator." />
        </p>

        <br/>
        <c:if test="${not empty exception}">
            <c:out value="${exception.message}" />
        </c:if>

        <div class="button">
            <a href="${pageContext.request.contextPath}/toy/main.ac">
                <spring:message code="button.goHome" text="Go to home" />
            </a>
        </div>
    </div> <!--// error-wrap -->

</div> <!-- // wrapper -->
</body>
</html>
