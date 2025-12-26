<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2025-12-03
  Time: 오전 12:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@ include file="/WEB-INF/jsp/toy/com/include/taglib.jsp" %>


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
            <a href="">
                    <img src="${pageContext.request.contextPath}/images/admin/frame/logo.png" alt="logo">
            </a>
        </h1>
        <nav class="gnb">
            <ul id="depth1" class="depth1">

                <li id="cctv_menu">
                    <a href="<c:url value='' />"><spring:message code="admin.header.menu.cctv" /></a> <%-- CHANGED: i18n --%>
                </li>
                <li id="place_menu">
                    <a href="<c:url value='' />"><spring:message code="admin.header.menu.place" /></a> <%-- CHANGED: i18n --%>
                </li>
                <li id="today_menu">
                    <a href="<c:url value='' />"><spring:message code="admin.header.menu.today" /></a> <%-- CHANGED: i18n --%>
                </li>
                <li id="rcmm_menu">
                    <a href="<c:url value='' />"><spring:message code="admin.header.menu.rcmm" /></a> <%-- CHANGED: i18n --%>
                </li>
                <li id="jejunews_menu">
                    <a href="<c:url value='' />"><spring:message code="admin.header.menu.jejunews" /></a> <%-- CHANGED: i18n --%>
                </li>

                <li id="influencer_menu">
                    <a href="<c:url value='' />" class="drop"><spring:message code="admin.header.menu.influencer" /></a> <%-- CHANGED: i18n --%>
                    <ul class="depth2">
                        <li><a href="<c:url value='' />"><spring:message code="admin.header.menu.influencer.manage" /></a></li> <%-- CHANGED: i18n --%>
                        <li><a href="<c:url value='' />"><spring:message code="admin.header.menu.influencer.goods" /></a></li> <%-- CHANGED: i18n --%>
                        <li><a href="<c:url value='' />"><spring:message code="admin.header.menu.influencer.order" /></a></li> <%-- CHANGED: i18n --%>
                    </ul>
                </li>

                <li id="excclc_menu">
                    <a href="<c:url value='' />" class="drop"><spring:message code="admin.header.menu.excclc" /></a> <%-- CHANGED: i18n --%>
                </li>

                <li id="member_menu">
                    <a href="<c:url value='' />"><spring:message code="admin.header.menu.member" /></a> <%-- CHANGED: i18n --%>
                </li>


                <li id="system_menu">
                    <a href="<c:url value='/toy/admin/sys/accesslog/listAdmAcssLog.do' />" class="drop"><spring:message code="admin.header.menu.system" /></a> <%-- CHANGED: i18n --%>
                    <ul class="depth2">
                        <li><a href="<c:url value='' />"><spring:message code="admin.header.menu.system.mngr" /></a></li>
                        <li><a href="<c:url value='' />"><spring:message code="admin.header.menu.system.auth" /></a></li>
                        <li><a href="<c:url value='/toy/admin/sys/code/grp/list.do' />"><spring:message code="admin.header.menu.system.code" /></a></li>
                        <li><a href="<c:url value='' />"><spring:message code="admin.header.menu.system.allowIp" /></a></li>
                        <li><a href="<c:url value='/toy/admin/sys/accesslog/listAdmAcssLog.do' />"><spring:message code="admin.header.menu.system.log" /></a></li>
                    </ul>
                </li>

                <%--<li id="config_menu">
                    <a href="<c:url value='/toy/config/detailLogo.ac' />" class="drop">환경설정</a>
                    <ul class="depth2">
                        <li><a href="<c:url value='/toy/config/detailLogo.ac' />">로고설정</a></li>
                    </ul>
                </li>--%>

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
                <a href="javascript:window.open('<c:url value="" />','detailMber', 'width=1000,height=440');">
                    <div class="photo" style="background-image: url('${pageContext.request.contextPath}/images/admin/frame/profile.png')"></div>
                    <span class="setting"><i class="material-icons-outlined">settings</i><spring:message code="admin.header.profile.account" />
</span>
                </a>
            </div>
            <ul class="user-info">
                <li class="user">${sessionScope.sessionAdminVO.mngrNm} [${sessionScope.sessionAdminVO.authorDc}]</li>
                <li><spring:message code="admin.header.lastLogin" /> : ${sessionScope.sessionAdminVO.lastLgnDt}</li>
            </ul>
        </div>
        <nav class="flex wp100 px-10">
            <a href="/" class="site wp100" target="_blank"><i class="material-icons-outlined">home</i>
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

            <c:if test="${menuActiveMap.adminMenu1 eq 'cctv'}">	<%-- 날씨Live : 2depth 메뉴 --%>
                <li>
                    <a href="<c:url value="" />" id="cctv_cctv_menu"><i class="material-icons-outlined">video_camera_front</i>CCTV 관리</a>
                </li>
            </c:if>

            <c:if test="${menuActiveMap.adminMenu1 eq 'rcmm'}">	<%-- 추천장소 관리 : 2depth 메뉴 --%>
                <li>
                    <a href="<c:url value="" />" id="rcmm_place_menu"><i class="material-icons-outlined">place</i>추천장소 관리</a>
                </li>
            </c:if>

            <c:if test="${menuActiveMap.adminMenu1 eq 'today'}">	<%-- 오늘제주 관리 : 2depth 메뉴 --%>
                <li>
                    <a href="<c:url value="" />" id="today_mngr_menu"><i class="material-icons-outlined">wb_sunny</i>오늘제주 관리</a>
                </li>
            </c:if>

            <c:if test="${menuActiveMap.adminMenu1 eq 'place'}">	<%-- 장소 관리 : 2depth 메뉴 --%>
                <li>
                    <a href="<c:url value="" />" id="place_mngr_menu"><i class="material-icons-outlined">place</i>장소관리</a>
                </li>
            </c:if>

            <c:if test="${menuActiveMap.adminMenu1 eq 'jejunews'}">	<%-- 제주소식 : 2depth 메뉴 --%>
                <li>
                    <a href="<c:url value="" />" id="jeju_news_menu"><i class="material-icons-outlined">feed</i>제주소식</a>
                </li>
            </c:if>

            <c:if test="${menuActiveMap.adminMenu1 eq 'influencer'}">	<%-- 인플루언서 관리 : 2depth 메뉴 --%>
                <li>
                    <a href="<c:url value='' />" id="influencer_manage_menu"><i class="material-icons-outlined">settings</i>인플루언서 관리</a>
                </li>
                <li>
                    <a href="<c:url value='' />" id="influencer_goods_menu"><i class="material-icons-outlined">inventory_2</i>상품관리</a>
                </li>
                <li>
                    <a href="<c:url value='' />" id="influencer_sale_menu"><i class="material-icons-outlined">assignment</i>주문관리</a>
                </li>
            </c:if>

            <c:if test="${menuActiveMap.adminMenu1 eq 'client'}">	<%-- 고객관리 : 2depth 메뉴 --%>
                <li>
                    <a href="<c:url value='' />" id="client_user_menu"><i class="material-icons-outlined">people</i>사용자관리</a>
                </li>
                <li>
                    <a href="<c:url value='' />" id="client_dropUser_menu"><i class="material-icons-outlined">person_remove</i>탈퇴사용자</a>
                </li>
            </c:if>

            <c:if test="${menuActiveMap.adminMenu1 eq 'excclc'}">	<%--정산 : 2depth 메뉴--%>
                <li>
                    <a href="<c:url value="" />" id="excclc_excclc_menu" <%--class="drop"--%>><i class="material-icons-outlined">request_quote</i>정산</a>
                    <div class="depth3">
                        <strong>정산</strong>

                            <%--<ul>
                                <li id="excclc_excclc_calendar_menu"><a href="<c:url value="/toy/excclc/listExcclcCalendar.ac" />">달력 보기</a></li>
                                <li id="excclc_excclc_list_menu"><a href="<c:url value="/toy/excclc/listExcclc.ac" />">목록 보기</a></li>
                            </ul>--%>

                    </div>
                </li>
            </c:if>

            <c:if test="${menuActiveMap.adminMenu1 eq 'system'}">	<%-- 시스템설정 : 2depth 메뉴 --%>
                <li>
                    <a href="<c:url value="" />" id="system_mngr_menu"><i class="material-icons-outlined">manage_accounts</i>관리자관리</a>
                </li>
                <li>
                    <a href="<c:url value="" />" id="system_auth_menu"><i class="material-icons-outlined">lock</i>권한관리</a>
                </li>
                <li>
                    <a href="<c:url value="/toy/admin/sys/code/grp/list.do" />" id="system_code_menu"><i class="material-icons-outlined">apps</i>코드관리</a>
                </li>
                <li>
                    <a href="<c:url value="" />" id="system_popup_menu"><i class="material-icons-outlined">apps</i>팝업관리</a>
                </li>
                <li>
                    <a href="<c:url value='/toy/admin/sys/accesslog/listAdmAcssLog.do' />" id="system_accessLog_menu"><i class="material-icons-outlined">list_alt</i>접속이력조회</a>
                </li>
            </c:if>

            <%--<c:if test="${menuActiveMap.adminMenu1 eq 'config'}">	&lt;%&ndash; 환경설정 : 2depth 메뉴 &ndash;%&gt;
               <li>
                   <a href="<c:url value="/toy/config/detailLogo.ac" />" id="config_logo_menu"><i class="material-icons-outlined">apps</i>로고설정</a>
               </li>
               <li>
                   <a href="<c:url value="/toy/config/detailOperation.ac" />" id="config_operation_menu"><i class="material-icons-outlined">apps</i>운영설정</a>
               </li>
               <li>
                   <a href="<c:url value="/toy/config/listFee.ac" />" id="config_fee_menu"><i class="material-icons-outlined">apps</i>수수료관리</a>
               </li>
             </c:if>--%>




            <%--<c:if test="${menuActiveMap.adminMenu1 eq 'stats'}">	&lt;%&ndash; 통계 : 2depth 메뉴 &ndash;%&gt;
              <li>
                  <a href="<c:url value="/toy/stats/clientStats/listClientStats.ac" />" id="stats_clientStats_menu"><i class="material-icons-outlined">apps</i>고객통계</a>
              </li>
              <li>
                  <a href="<c:url value="/toy/stats/useStats/listUseStats.ac" />" id="stats_useStats_menu"><i class="material-icons-outlined">apps</i>고객이용통계</a>
              </li>
              <li>
                  <a href="<c:url value="/toy/stats/compStats/listCompStats.ac" />" id="stats_compStats_menu"><i class="material-icons-outlined">apps</i>입점업체통계</a>
              </li>
              <li>
                  <a href="<c:url value="/toy/stats/saleStats/listSaleStats.ac" />" id="stats_saleStats_menu"><i class="material-icons-outlined">apps</i>매출통계</a>
              </li>
              <li>
                  <a href="<c:url value="/toy/statis/listVisitLog.ac" />" id="stats_visitLog_menu" class="drop"><i class="material-icons-outlined">apps</i>방문통계</a>
                  <div class="depth3">
                      <strong>접속통계</strong>
                      <ul>
                          <li id="stats_visitLog_normal_menu"><a href="/toy/statis/listVisitLog.ac">일반통계</a></li>
                          <li id="stats_visitLog_device_menu"><a href="/toy/statis/listVisitLogDevice.ac">기기별통계</a></li>
                      </ul>
                  </div>
              </li>
            </c:if>--%>

        </ul>
    </nav>
    <span class="sideGnb-btn close" id="sideGnb-btn"><button type="button">사이드 메뉴 접기</button></span>
</aside>
<%-- //Side Navi --%>
