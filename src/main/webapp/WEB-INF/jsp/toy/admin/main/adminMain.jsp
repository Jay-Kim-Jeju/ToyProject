<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>

<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>

</head>

<body>
<div id="wrapper">

<%-- Common Header/Side Menu --%>
<jsp:include page="/WEB-INF/jsp/toy/admin/include/adminHeader.jsp" flush="false">
    <jsp:param value="home_menu" name="admin_menu"/>
</jsp:include>

<main id="main">
    <section class="contents-wrap">
        <div class="title-area">
            <h2 class="depth-title"><spring:message code="admin.main.title" /></h2>
            <span class="memo"><spring:message code="admin.main.welcome" /></span>
        </div>

        <section class="content-box">
            <div style="text-align: center; padding: 100px 0;">
                <h1 style="font-size: 48px; font-weight: bold; color: #333; margin-bottom: 20px;">
                    MAIN PAGE
                </h1>
                <p style="font-size: 18px; color: #666;">
                    <spring:message code="admin.main.description" />
                </p>
            </div>
        </section>
    </section>
</main>

</div> <%-- #wrapper --%>
</body>
</html>
