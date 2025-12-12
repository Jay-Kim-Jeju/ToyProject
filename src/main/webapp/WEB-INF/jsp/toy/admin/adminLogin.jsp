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
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="author" content="Jay">
  <meta name="format-detection" content="telephone=no">

  <%@ include file="/WEB-INF/jsp/toy/com/include/csrfMetaTags.jsp" %>

  <title>관리자 로그인</title>

  <!-- css -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/import.css?jsCssVer=${jsCssVer}">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/login.css?jsCssVer=${jsCssVer}">

  <!-- script -->
  <script src="${pageContext.request.contextPath}/js/com/jquery-3.4.1.js"></script>
  <script src="${pageContext.request.contextPath}/js/com/jquery-ui-1.12.1.js"></script>
  <script src="${pageContext.request.contextPath}/js/admin/html-func.js?jsCssVer=${jsCssVer}"></script>
  <script src="${pageContext.request.contextPath}/js/admin/html-comm.js?jsCssVer=${jsCssVer}" ></script>

  <script type="text/javascript">
    let returnURL = "";

    /**
     * 로그인
     */
    function fn_login(){
      if($("#mngrUid").val() == ""){
        alert("아이디를 입력하세요.");
        $("#mngrUid").focus();
        return false;
      }
      if($("#pwdEncpt").val() == ""){
        alert("비밀번호를 입력하세요");
        $("#pwdEncpt").focus();
        return false;
      }

      $.ajax({
        type: 'POST',
        url: "/toy/admin/loginAction.doax",
        data: $("#login_form").serialize(),
        beforeSend : function(xhr){
          let token = $("meta[name='_csrf']").attr("content");
          let header = $("meta[name='_csrf_header']").attr("content");
          xhr.setRequestHeader(header, token);
        },
        success: function(data) {
          //로그인 성공이면
          if(data.result == "Y") {
            location.href = data.returnURL;
          }
          //로그인 실패이면
          else {
            alert(data.message);
          }
        },
        error: function(request, status, error) {
          if (request.status == 510) {
            alert("에러가 발생했습니다. 로그인세션이 만료되었습니다.");
            location.replace("/toy/admin/login.ac");
            return false;
          } else if (request.status == 403) {
            alert("접근 권한이 없습니다.");
            return false;
          }

          alert("시스템 오류\n관리자에게 문의해주세요.");
        }
      });

      return false;
    }

  </script>
</head>
<body style="background-image: url('${pageContext.request.contextPath}/images/admin/login/bg-img2.jpg')">
<div id="wrapper">
  <header id="header">
    <h1><img src="${pageContext.request.contextPath}/images/admin/login/logo.png" alt="logo"></h1>
  </header>

  <main id="main">

    <section class="login-wrap">
      <div class="login-box">
        <form id="login_form" name="login_form" onSubmit="return fn_login();" method="post">
          <input type="hidden" name="returnURL" value="${returnURL }" />
          <h2>관리자 로그인</h2>
          <div class="int-box">
            <label for="userID">아이디</label>
            <input type="text" class="userID" id="mngrUid" name="mngrUid" value="${userVO.mngrUid}" placeholder="아이디">
          </div>
          <div class="int-box">
            <label for="userPW">패스워드</label>
            <input type="password" class="userPW" id="pwdEncpt" name="pwdEncpt" placeholder="패스워드">
          </div>
          <div class="login-btn"><button type="submit">로그인</button></div>
        </form>

        <p class="copy">Copyright(c) 2025 Jay. All rights reserved.</p>
      </div>
    </section> <!--//login-wrap-->
  </main>
</div> <!--//wrapper-->
</body>
</html>

