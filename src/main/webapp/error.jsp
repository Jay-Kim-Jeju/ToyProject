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

    <!-- css -->
    <link rel="icon" href="${pageContext.request.contextPath}/images/com/favicon.ico">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mw/import.css?jsCssVer=${jsCssVer}">

    <!-- script -->
    <script src="${pageContext.request.contextPath}/js/com/jquery-3.4.1.js"></script>
    <script src="${pageContext.request.contextPath}/js/com/jquery-ui-1.12.1.js"></script>
    <script src="${pageContext.request.contextPath}/js/com/swiper-4.5.0.js"></script>
    <script src="${pageContext.request.contextPath}/js/mw/html-func.js?jsCssVer=${jsCssVer}"></script>
    <script src="${pageContext.request.contextPath}/js/mw/html-comm.js?jsCssVer=${jsCssVer}"></script>
</head>
<body>
<div id="wrapper">

    <div class="error-wrap">
        <img class="icon" src="${pageContext.request.contextPath}/images/com/error.png" alt="error">

        <h2 class="error-msg">
            <c:if test="${empty param.errCode}">Error</c:if>
            <c:if test="${param.errCode=='SESSION_OUT'}">세션이 만료 되었습니다.</c:if>
            <c:if test="${param.errCode=='NO_AUTH'}">접근 권한이 없습니다.</c:if>
            <c:if test="${param.errCode=='NO_DATA'}">값이 없습니다.</c:if>
            <c:if test="${param.errCode=='PARMA_ERR'}">인자가 옳지 않습니다.</c:if>
            <c:if test="${param.errCode=='DATA_ALREAD'}">이미 등록되었습니다.</c:if>
            <c:if test="${param.errCode=='DATA_ALREAD_REG'}">이미 신청되었습니다.</c:if>
            <c:if test="${param.errCode=='FLE_SIZE_OVER'}">파일크기가 너무 큽니다.</c:if>
            <c:if test="${param.errCode=='FLE_SIZE_SMALL'}">파일크기가 너무 작습니다.</c:if>
            <c:if test="${param.errCode=='FLE_NOT_EXIST'}">파일이 존재하지 않거나 삭제되었습니다.</c:if>
            <c:if test="${param.errCode=='NOT_FIND_PAGE'}">요청하신 페이지가 존재 하지 않습니다.</c:if>
            <c:if test="${param.errCode=='ERROR_SERVER'}">서버에 문제가 발생했습니다.</c:if>
        </h2>

        <p class="error-text">잘못된 접근입니다. 관리자에게 문의 해주세요.</p>

        <div class="button">
            <a href="${pageContext.request.contextPath}/toy/admin/main.ac">홈으로 이동</a>
        </div>
    </div> <!--//error-wrap-->

</div> <!-- //wrapper -->
</body>
</html>