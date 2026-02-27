<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2025-12-11
  Time: 오전 7:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@ include file="/WEB-INF/jsp/toy/com/include/taglib.jsp" %>

<%
  HttpSession m_Session = request.getSession();
  String sessionID = m_Session.getId();

  String strServerCert = "";

%>

<!DOCTYPE html>
<html lang="${pageContext.request.locale.language}">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="author" content="Jay">
  <meta name="format-detection" content="telephone=no">

  <%@ include file="/WEB-INF/jsp/toy/com/include/csrfMetaTags.jsp" %>

  <title><spring:message code="admin.login.title" /></title>

  <!-- css -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/import.css?jsCssVer=${jsCssVer}">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/login.css?jsCssVer=${jsCssVer}">

  <!-- script -->
  <script src="${pageContext.request.contextPath}/js/com/jquery-3.4.1.js"></script>
  <script src="${pageContext.request.contextPath}/js/com/jquery-ui-1.12.1.js"></script>
  <script src="${pageContext.request.contextPath}/js/admin/html-func.js?jsCssVer=${jsCssVer}"></script>
  <script src="${pageContext.request.contextPath}/js/admin/html-comm.js?jsCssVer=${jsCssVer}" ></script>
  <script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>

  <script type="text/javascript">

    // Show logout message once (if any).
    $(function () {
      var msg = $("#logoutMessage").val();
      if (msg) {
        alert(msg);
        // Prevent repeated alert on refresh by removing the query parameter.
        if (window.history && window.history.replaceState) {
          var url = new URL(window.location.href);
          url.searchParams.delete("reason");
          window.history.replaceState({}, document.title, url.toString());
        }
      }
    });

    let returnURL = "";

    // i18n messages for JavaScript alerts
    const MSG_ENTER_ID = "<spring:message code='admin.login.alert.enterId' javaScriptEscape='true' />";
    const MSG_ENTER_PW = "<spring:message code='admin.login.alert.enterPassword' javaScriptEscape='true' />";
    const MSG_ERR_510  = "<spring:message code='admin.login.error.510' javaScriptEscape='true' />";
    const MSG_ERR_403  = "<spring:message code='admin.login.error.403' javaScriptEscape='true' />";
    const MSG_ERR_SYS  = "<spring:message code='admin.login.error.system' javaScriptEscape='true' />";

    /**
     * 로그인
     */
    function fn_login(){
      if($("#mngrUid").val() == ""){
        alert(MSG_ENTER_ID); // CHANGED: i18n
        $("#mngrUid").focus();
        return false;
      }
      if($("#pwdEncpt").val() == ""){
        alert(MSG_ENTER_PW); // CHANGED: i18n
        $("#pwdEncpt").focus();
        return false;
      }

      $.ajax({
        type: 'POST',
        url: "/toy/admin/loginAction.ac",
        data: $("#login_form").serialize(),
        beforeSend: function (xhr) {
          // Your custom logic only (loading spinner etc.)
        },
        success: function(data) {
          //로그인 성공이면
          if(data.result == "Y") {
            location.href = data.returnURL;
          }
          //로그인 실패이면
          else {
            // NOTE: server message can be i18n'ed later by returning messageCode instead of message text
            alert(data.message);
          }
        },
        error: function(request, status, error) {
          if (request.status == 510) {
            alert(MSG_ERR_510);
            location.replace("/toy/admin/login.do");
            return false;
          } else if (request.status == 403) {
            alert(MSG_ERR_403);
            return false;
          }

          alert(MSG_ERR_SYS);
        }
      });

      return false;
    }

  </script>

  <%@ include file="/WEB-INF/jsp/toy/com/include/localeSwitcher.jsp" %>
</head>
<body style="background-image: url('${pageContext.request.contextPath}/images/admin/login/bg-img2.jpg')">
<c:if test="${not empty logoutMessage}">
  <input type="hidden" id="logoutMessage" value="<c:out value='${logoutMessage}'/>" />
</c:if>
<div id="wrapper">
  <header id="header">
    <h1><img src="${pageContext.request.contextPath}/images/admin/login/logo.png" alt="logo"></h1>
  </header>

  <main id="main">

    <section class="login-wrap">
      <div class="login-box">
        <form id="login_form" name="login_form" onSubmit="return fn_login();" method="post">
          <input type="hidden" name="returnURL" value="${returnURL }" />

          <h2><spring:message code="admin.login.heading" /></h2>

          <div class="int-box">
            <label for="mngrUid"><spring:message code="admin.login.label.id" /></label>
            <input type="text" class="userID" id="mngrUid" name="mngrUid" value="${userVO.mngrUid}" placeholder="<spring:message code='admin.login.placeholder.id' />">
          </div>
          <div class="int-box">
            <label for="pwdEncpt"><spring:message code="admin.login.label.password" /></label>
            <input type="password" class="userPW" id="pwdEncpt" name="pwdEncpt" placeholder="<spring:message code='admin.login.placeholder.password' />">
          </div>
          <div class="login-btn"><button type="submit"><spring:message code="admin.login.button.login" /></button></div>
        </form>

        <p class="copy">Copyright(c) 2025 Jay. All rights reserved.</p>
      </div>
    </section> <!--//login-wrap-->
  </main>
</div> <!--//wrapper-->
</body>
</html>

