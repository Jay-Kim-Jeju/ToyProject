<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/com/include/taglib.jsp" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1">
    <meta name="format-detection" content="telephone=no">
    <meta name="author" content="Jay">

    <title>Toy Project</title>

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
            <c:if test="${empty param.errCode}">Error</c:if>
            <c:if test="${param.errCode=='SESSION_OUT'}">Session has expired.</c:if>
            <c:if test="${param.errCode=='NO_AUTH'}">You do not have permission.</c:if>
            <c:if test="${param.errCode=='NO_DATA'}">No data found.</c:if>
            <c:if test="${param.errCode=='PARMA_ERR'}">Invalid parameters.</c:if>
            <c:if test="${param.errCode=='DATA_ALREAD'}">Data already exists.</c:if>
            <c:if test="${param.errCode=='DATA_ALREAD_REG'}">Request already exists.</c:if>
            <c:if test="${param.errCode=='FLE_SIZE_OVER'}">File size is too large.</c:if>
            <c:if test="${param.errCode=='FLE_SIZE_SMALL'}">File size is too small.</c:if>
            <c:if test="${param.errCode=='FLE_NOT_EXIST'}">File does not exist.</c:if>
            <c:if test="${param.errCode=='NOT_FIND_PAGE'}">Requested page was not found.</c:if>
            <c:if test="${param.errCode=='ERROR_SERVER'}">A server error occurred.</c:if>
        </h2>

        <p class="error-text">Please contact the administrator.</p>

        <div class="button">
            <a href="${pageContext.request.contextPath}/toy/admin/main.do">Go to home</a>
        </div>
    </div>

</div>
</body>
</html>
