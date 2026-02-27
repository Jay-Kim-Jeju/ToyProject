<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2025-12-16
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>

<!-- jsgrid -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid-theme.css" />
<script src="${pageContext.request.contextPath}/js/com/jsgrid/jsgrid.js"></script>

<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>
<script type="text/javascript">
    var jsGridId = "AccessLogJsGrid";
    var I18N_ACCESSLOG = {
        noData: "<spring:message code='admin.common.grid.noResults' javaScriptEscape='true' />"
    };

    $(function (){
        fn_setAcssLogJsGrid();

        $(".datepicker").datepicker("destroy");

        $("#sStartDt").datepicker({
            maxDate: 0,
            showOn: "both",
            buttonImage: "/images/com/jquery/calendar.png",
            buttonImageOnly: true,
            onSelect: function(selectDate) {
                $("#sEndDt").datepicker("option", "minDate", selectDate);
            }
        });

        $("#sEndDt").datepicker({
            showOn: "both",
            maxDate: 0,
            buttonImage: "/images/com/jquery/calendar.png",
            buttonImageOnly: true
        });
    });

    function fn_Search() {
        $("#AccessLogJsGrid").jsGrid({ pageIndex: 1 });
        fn_setAcssLogJsGrid();
    }

    function fn_setAcssLogJsGrid(){
        $("#AccessLogJsGrid").jsGrid({
            width: "100%",
            height: "auto",
            filtering: false,
            autoload: true,
            sorting: false,
            editing: false,
            inserting: false,
            paging: true,
            pageLoading: true,
            pageSize: 10,
            pageButtonCount: 10,
            pagerContainer: "#externalPager_acssLog",
            controller: {
                loadData: function(filter) {
                    var d = $.Deferred();
                    $("#pageIndex").val(filter.pageIndex);
                    $.ajax({
                        url: "/toy/admin/sys/accesslog/selectAdmAcssLogList.doax",
                        data: $("#searchForm").serialize(),
                        dataType: "json"
                    }).done(function(response) {
                        if (!response.data || response.data.length === 0) {
                            $("#" + jsGridId).jsGrid({
                                autoload: false,
                                noDataContent: '<div class="not-content"><span class="icon"></span><div class="text">' + I18N_ACCESSLOG.noData + '</div></div>'
                            });
                        }

                        d.resolve(response);
                        $("#itemsCount").text(gfn_commas(response.itemsCount));
                    });
                    return d.promise();
                }
            },
            fields: [
                { name: "mngrUid", title: "<spring:message code='admin.system.mngr.common.field.id' javaScriptEscape='true' />", type: "text", width: 80, align: "center" },
                { name: "accessIp", title: "<spring:message code='admin.system.accesslog.field.accessIp' javaScriptEscape='true' />", type: "text", width: 100, align: "center" },
                { name: "reqUri", title: "<spring:message code='admin.system.accesslog.field.reqUri' javaScriptEscape='true' />", type: "text", width: 250, align: "left", sorting: false },
                { name: "actionDesc", title: "<spring:message code='admin.system.accesslog.field.actionDesc' javaScriptEscape='true' />", type: "text", width: 250, align: "left", inserting: false, editing: false, filtering: false },
                { name: "regDt", title: "<spring:message code='admin.system.accesslog.field.accessTime' javaScriptEscape='true' />", type: "text", width: 140, align: "center", inserting: false, editing: false, filtering: false },
                { name: "memo", title: "<spring:message code='admin.system.accesslog.field.memo' javaScriptEscape='true' />", type: "text", width: 80, align: "left", inserting: false, editing: false, filtering: false }
            ]
        });
    }
</script>
</head>

<body>
<div id="wrapper">

    <%-- Common Header/Side Menu --%>
    <jsp:include page="/WEB-INF/jsp/toy/admin/include/adminHeader.jsp" flush="false" />

    <main id="main">
        <section class="contents-wrap">
            <div class="title-area">
                <h2 class="depth-title"><spring:message code="admin.system.accesslog.list.title" /></h2>
            </div>

            <section class="search-form">
                <div class="form-inline2">
                    <form name="searchForm" id="searchForm" method="get" action="${curURL}" onsubmit="fn_Search(); return false;">
                        <input type="hidden" id="pageIndex" name="pageIndex" value="${searchVO.pageIndex}"/>
                        <div class="form-group">
                            <label class="tit"><spring:message code="admin.system.mngr.common.field.id" /></label>
                            <input type="text" placeholder="<spring:message code='admin.system.accesslog.placeholder.mngrId' />" class="w130" id="sMngrUid" name="sMngrUid" value="${searchVO.sMngrUid}">
                        </div>
                        <div class="form-group">
                            <label class="tit"><spring:message code="admin.system.accesslog.field.searchPeriod" /></label>
                            <span class="date-wrap w142"><input class="datepicker" type="text" id="sStartDt" name="sStartDt" value="${searchVO.sStartDt}"></span>
                            <span>~</span>
                            <span class="date-wrap w142"><input class="datepicker" type="text" id="sEndDt" name="sEndDt" value="${searchVO.sEndDt}"></span>
                        </div>
                        <div class="form-group">
                            <label class="tit"><spring:message code="admin.system.accesslog.field.accessIp" /></label>
                            <input type="text" placeholder="<spring:message code='admin.system.accesslog.placeholder.accessIp' />" class="w150" id="sAccessIp" name="sAccessIp" value="${searchVO.sAccessIp}">
                        </div>
                        <div class="form-group">
                            <label class="tit"><spring:message code="admin.system.accesslog.field.reqUri" /></label>
                            <input type="text" placeholder="<spring:message code='admin.system.accesslog.placeholder.reqUri' />" class="w150" id="sReqUri" name="sReqUri" value="${searchVO.sReqUri}">
                        </div>
                        <div class="form-group">
                            <label class="tit"><spring:message code="admin.system.accesslog.field.actionDesc" /></label>
                            <input type="text" placeholder="<spring:message code='admin.system.accesslog.placeholder.actionDesc' />" class="w200" id="sActionDesc" name="sActionDesc" value="${searchVO.sActionDesc}">
                        </div>
                        <button type="submit" class="btn block search"><i class="material-icons-outlined">search</i> <spring:message code="admin.common.button.search" /></button>
                    </form>
                </div>
            </section>

            <section class="content-box">
                <h3 class="title2"><spring:message code="admin.common.searchResult" /><small>[<spring:message code="admin.common.total" /> <strong id="itemsCount" class="text-blue">0</strong><spring:message code="admin.common.countUnit" />]</small></h3>

                <div class="table-content-wrapper">
                    <div id="AccessLogJsGrid" ></div>
                    <div id="externalPager_acssLog" style="text-align:center;"></div>
                </div>
            </section>
        </section>
    </main>
</div>
</body>
</html>
