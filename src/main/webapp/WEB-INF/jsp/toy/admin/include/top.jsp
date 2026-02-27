<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@ include file="/WEB-INF/jsp/toy/com/include/taglib.jsp" %>

<!DOCTYPE html>
<html lang="${pageContext.request.locale.language}">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="author" content="Jay">
  <meta name="format-detection" content="telephone=no">

  <%-- tags for CSRF--%>
  <%@ include file="/WEB-INF/jsp/toy/com/include/csrfMetaTags.jsp" %>

  <title><spring:message code="admin.top.title" /></title>
  <!-- css -->
  <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
  <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/com/favicon.ico">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jquery-ui-1.12.1.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/swiper-4.5.0.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/font.css?jsCssVer=${jsCssVer}">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/reset.css?jsCssVer=${jsCssVer}">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/grid.css?jsCssVer=${jsCssVer}">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/comm.css?jsCssVer=${jsCssVer}">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/frame.css?jsCssVer=${jsCssVer}">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/contents.css?jsCssVer=${jsCssVer}">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/print.css?jsCssVer=${jsCssVer}">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/popup.css?jsCssVer=${jsCssVer}">

  <!-- script -->
  <script src="${pageContext.request.contextPath}/js/com/jquery-3.4.1.js"></script>
  <script src="${pageContext.request.contextPath}/js/com/jquery-ui-1.12.1.js"></script>
  <script src="${pageContext.request.contextPath}/js/com/libphonenumber-js.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/admin/html-func.js?jsCssVer=${jsCssVer}"></script>
  <script src="${pageContext.request.contextPath}/js/admin/html-comm.js?jsCssVer=${jsCssVer}"></script>

  <script>
    let CONTEXT_PATH = "";
    let FLAG_Y = "${Constant.FLAG_Y}";
    let FLAG_N = "${Constant.FLAG_N}";

    // Error message 異쒕젰
    function fn_alertErrorMsg(response) {
      if (response.errorMessage != undefined) {
        if (response.errorMessage.indexOf("${Constant.ADMIN_LOGIN_URL}") != -1) {
          alert("<spring:message code='admin.top.alert.accessDenied' javaScriptEscape='true' />");
          location.href = "${Constant.ADMIN_LOGIN_URL}";
        } else {
          alert(response.errorMessage);
        }
      } else {
        alert("<spring:message code='admin.top.alert.systemError' javaScriptEscape='true' />");
      }

      return false;
    }

  </script>

  <%@ include file="/WEB-INF/jsp/toy/com/include/localeSwitcher.jsp" %>

