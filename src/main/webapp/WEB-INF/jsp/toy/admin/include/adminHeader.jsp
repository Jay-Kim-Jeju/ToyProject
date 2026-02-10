<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2025-12-03
  Time: 오전 12:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@ include file="/WEB-INF/jsp/toy/com/include/taglib.jsp" %>


<%-- For Auth Check --%>
<c:set var="authList" value="${sessionScope.sessionAdminVO.auth}" />
<c:set var="canSeeAllMenus" value="${authList ne null and authList.contains('ADMINISTRATOR')}" />


<script type="text/javascript">
    $(function (){
        $("#" + "${menuActiveMap.adminMenu1}_menu").addClass("active");
        $("#" + "${menuActiveMap.adminMenu1}_${menuActiveMap.adminMenu2}_menu").addClass("active");
        <c:if test="${not empty menuActiveMap.adminMenu3}">
        $("#" + "${menuActiveMap.adminMenu1}_${menuActiveMap.adminMenu2}_${menuActiveMap.adminMenu3}_menu").addClass("active");
        </c:if>

    });
</script>

<%--Header 영역--%>
<header id="header">
    <div class="gnb-wrapper">
        <h1 class="logo">
            <a href="/toy/admin/main.do">
                    <img src="${pageContext.request.contextPath}/images/admin/frame/logo.png" alt="logo">
            </a>
        </h1>
        <nav class="gnb">
            <ul id="depth1" class="depth1">

                <c:if test="${canSeeAllMenus or (authList ne null and authList.contains('MENU1'))}"> <%-- Auth Check --%>
                    <li id="">
                        <a href="<c:url value='' />">MENU1</a> <%-- CHANGED: i18n --%>
                    </li>
                </c:if>


                <c:if test="${canSeeAllMenus or (authList ne null and authList.contains('MENU2'))}"> <%-- Auth Check --%>
                    <li id="">
                        <a href="<c:url value='' />">MENU2</a> <%-- CHANGED: i18n --%>
                    </li>
                </c:if>


                <c:if test="${canSeeAllMenus or (authList ne null and authList.contains('SYSTEM'))}"> <%-- Auth Check --%>
                    <li id="system_menu">
                        <a href="<c:url value='/toy/admin/sys/accesslog/listAdmAcssLog.do' />" class="drop"><spring:message code="admin.header.menu.system" /></a> <%-- CHANGED: i18n --%>
                        <ul class="depth2">
                            <li><a href="<c:url value='/toy/admin/sys/mngr/list.do' />"><spring:message code="admin.header.menu.system.mngr" /></a></li>
                            <li><a href="<c:url value='/toy/admin/sys/auth/role/list.do' />"><spring:message code="admin.header.menu.system.auth" /></a></li>
                            <li><a href="<c:url value='/toy/admin/sys/code/grp/list.do' />"><spring:message code="admin.header.menu.system.code" /></a></li>
                            <li><a href="<c:url value='' />"><spring:message code="admin.header.menu.system.allowIp" /></a></li>
                            <li><a href="<c:url value='/toy/admin/sys/accesslog/listAdmAcssLog.do' />"><spring:message code="admin.header.menu.system.log" /></a></li>
                        </ul>
                    </li>
                </c:if>

            </ul>
        </nav>
    </div>
</header>
<%--//Header 영역--%>

<%-- Side Navi --%>
<aside id="side-wrapper" class="open">
    <div class="global">
        <div class="login-info">
            <div class="profile">
                <a href="javascript:window.open('<c:url value="/toy/admin/my/detailPopMngr.do" />','myDetailPopMngr', 'width=570,height=320');">
                    <div class="photo" style="background-image: url('${pageContext.request.contextPath}/images/admin/frame/profile.png')"></div>
                    <span class="setting"><i class="material-icons-outlined">settings</i><spring:message code="admin.header.profile.account" />
</span>
                </a>
            </div>
            <ul class="user-info">
                <li class="user">${sessionScope.sessionAdminVO.mngrNm} [${sessionScope.sessionAdminVO.displayRoleName}]</li>
                <li><spring:message code="admin.header.lastLogin" /> : ${sessionScope.sessionAdminVO.lastLgnDt}</li>
            </ul>
        </div>
        <nav class="flex wp100 px-10">
            <a href="/" class="site wp100" target="_blank">
                <i class="material-icons-outlined">home</i>
                <span class="text">
                    <spring:message code="admin.header.openSite" />
                </span>
            </a>
            <a href="<c:url value='/toy/admin/logout.ac' />" class="logout wp100">
                <i class="material-icons-outlined">logout</i>
                <span class="text">
                    <spring:message code="admin.header.logout" />
                </span>
            </a>
        </nav>

    </div>
    <nav class="gnb" id="side-menu">
        <ul class="depth2">


            <c:if test="${canSeeAllMenus or (authList ne null and authList.contains('MENU1'))}"> <%-- Auth Check --%>
                <c:if test="${menuActiveMap.adminMenu1 eq 'menu1'}">	<%-- menu1 : 1depth 메뉴 --%>
                    <li>
                        <a href="<c:url value="" />" id="menu1_menu1_menu"><i class="material-icons-outlined">video_camera_front</i>menu1</a>
                    </li>
                </c:if>
            </c:if>

            <c:if test="${canSeeAllMenus or (authList ne null and authList.contains('MENU2'))}"> <%-- Auth Check --%>
                <c:if test="${menuActiveMap.adminMenu1 eq 'menu2'}">	<%-- menu2 : 1depth 메뉴 --%>
                    <li>
                        <a href="<c:url value="" />" id="menu2_menu2_menu"><i class="material-icons-outlined">video_camera_front</i>menu2</a>
                    </li>
                </c:if>
            </c:if>


            <c:if test="${canSeeAllMenus or (authList ne null and authList.contains('SYSTEM'))}"> <%-- Auth Check --%>
                <c:if test="${menuActiveMap.adminMenu1 eq 'system'}">	<%-- 시스템설정 : 1depth 메뉴 --%>
                    <li>
                        <a href="<c:url value="/toy/admin/sys/mngr/list.do" />" id="system_mngr_menu"><i class="material-icons-outlined">manage_accounts</i>관리자관리</a>                                              <%-- └─ 관리자 : 2depth 메뉴 --%>
                    </li>
                    <li>
                        <a href="<c:url value="/toy/admin/sys/auth/role/list.do" />" id="system_auth_menu"><i class="material-icons-outlined">lock</i>권한관리</a>  <%-- └─ 권한 : 2depth 메뉴 --%>
                    </li>
                    <li>
                        <a href="<c:url value="/toy/admin/sys/code/grp/list.do" />" id="system_code_menu"><i class="material-icons-outlined">apps</i>코드관리</a>   <%-- └─ 코드 : 2depth 메뉴 --%>
                    </li>
                    <li>
                        <a href="" id=""><i class="material-icons-outlined">apps</i>접속허용IP 관리</a>
                    </li>                                                       <%-- └─ 접속허용IP : 2depth 메뉴 --%>
                    <li>
                        <a href="<c:url value='/toy/admin/sys/accesslog/listAdmAcssLog.do' />" id="system_accesslog_menu"><i class="material-icons-outlined">list_alt</i>접속이력조회</a> <%-- └─ 접속이력조회 : 2depth 메뉴 --%>
                    </li>
                </c:if>
            </c:if>




        </ul>
    </nav>
    <span class="sideGnb-btn close" id="sideGnb-btn"><button type="button">사이드 메뉴 접기</button></span>
</aside>
<%-- //Side Navi --%>
