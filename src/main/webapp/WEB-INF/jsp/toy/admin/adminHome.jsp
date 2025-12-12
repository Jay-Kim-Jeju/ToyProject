<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>

<!-- jsgrid css -->
<!--  -->

<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>
<script type="text/javascript">
</script>
</head>

<body>
<div id="wrapper">

<%-- 공통 헤더/사이드 메뉴 --%>
<jsp:include page="/WEB-INF/jsp/toy/admin/include/adminHeader.jsp" flush="false">
    <jsp:param value="home_menu" name="admin_menu"/>
</jsp:include>

<main id="main">
    <section class="contents-wrap">
        <div class="title-area">
            <h2 class="depth-title">관리자 메인</h2>
            <span class="memo">관리자 대시보드 또는 안내 문구를 여기에 표시합니다.</span>
        </div>

        <section class="content-box">
            <!-- Change Contents (↓↓콘텐츠 변경↓↓) -->
            <p>여기에 앞으로 관리용 카드, 링크, 통계 등을 배치할 예정.</p>

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
                        <td><c:out value="${m.username}" /></td>
                        <td><c:out value="${m.email}" /></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <!-- //Change Contents (↑↑콘텐츠 변경↑↑) -->
        </section>
    </section>
</main>

</div> <%-- #wrapper 닫기 (top.jsp에서 열어줌) --%>
</body>
</html>
