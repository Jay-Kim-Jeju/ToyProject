<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2026-01-14
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>

<!-- jsgrid -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid-theme.css" />
<script src="${pageContext.request.contextPath}/js/com/jsgrid/jsgrid.js"></script>

<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>

<script type="text/javascript">
    var jsGridId = "MngrJsGrid";
    var I18N_MNGR_LIST = {
        confirmDisable: "<spring:message code='admin.system.mngr.list.confirm.disable' javaScriptEscape='true' />",
        alertDisabled: "<spring:message code='admin.system.mngr.list.alert.disabled' javaScriptEscape='true' />",
        alertNotFound: "<spring:message code='admin.system.mngr.list.alert.notFound' javaScriptEscape='true' />",
        alertDisableFailed: "<spring:message code='admin.system.mngr.list.alert.disableFailed' javaScriptEscape='true' />",
        noData: "<spring:message code='admin.common.grid.noResults' javaScriptEscape='true' />",
        colNo: "<spring:message code='admin.system.mngr.list.grid.no' javaScriptEscape='true' />",
        colId: "<spring:message code='admin.system.mngr.common.field.id' javaScriptEscape='true' />",
        colName: "<spring:message code='admin.system.mngr.common.field.name' javaScriptEscape='true' />",
        colPhone: "<spring:message code='admin.system.mngr.common.field.phone' javaScriptEscape='true' />",
        colEmail: "<spring:message code='admin.system.mngr.common.field.email' javaScriptEscape='true' />",
        colUse: "<spring:message code='admin.system.mngr.common.field.useYn' javaScriptEscape='true' />",
        colAuthApplied: "<spring:message code='admin.system.mngr.list.grid.authApplied' javaScriptEscape='true' />",
        colActions: "<spring:message code='admin.system.mngr.list.grid.actions' javaScriptEscape='true' />",
        btnDetail: "<spring:message code='admin.common.button.detail' javaScriptEscape='true' />",
        btnDisable: "<spring:message code='admin.common.button.disable' javaScriptEscape='true' />"
    };

    $(function () {
        fn_setMngrJsGrid();
    });

    function fn_openInsertPop() {
        window.open("<c:url value='/toy/admin/sys/mngr/insertPop.do'/>", "insertPopMngr",
            "width=560,height=260,scrollbars=yes");
    }

    function fn_openDetailPop(mngrUid) {
        if (!mngrUid) return;
        window.open("<c:url value='/toy/admin/sys/mngr/detailPop.do'/>?mngrUid=" + encodeURIComponent(mngrUid),
            "detailPopMngr", "width=720,height=420,scrollbars=yes");
    }

    function fn_refreshMngrGrid() {
        $("#" + jsGridId).jsGrid("loadData");
    }

    function fn_Search() {
        $("#" + jsGridId).jsGrid({ pageIndex: 1 });
        $("#" + jsGridId).jsGrid("loadData");
    }

    function fn_Reset() {
        $("#sMngrUid").val("");
        $("#sMngrNm").val("");
        $("#chkIncludeDeleted").prop("checked", false);
        $("#useYn").val("true");
        $("#" + jsGridId).jsGrid({ pageIndex: 1 });
        $("#" + jsGridId).jsGrid("loadData");
    }

    function fn_disableMngr(mngrUid) {
        if (!mngrUid) return;

        if (!confirm(I18N_MNGR_LIST.confirmDisable)) {
            return;
        }

        $.ajax({
            type: "POST",
            url: "<c:url value='/toy/admin/sys/mngr/disable.ac'/>",
            data: { mngrUid: mngrUid },
            success: function (data) {
                var r = (data && data.result) ? String(data.result) : "N";

                if (r === "Y") {
                    alert(I18N_MNGR_LIST.alertDisabled);
                    fn_refreshMngrGrid();
                    return;
                }

                if (r === "None") {
                    alert(I18N_MNGR_LIST.alertNotFound);
                    fn_refreshMngrGrid();
                    return;
                }

                alert((data && data.errorMessage) ? data.errorMessage : I18N_MNGR_LIST.alertDisableFailed);
            },
            error: fn_AjaxError
        });
    }

    function fn_setMngrJsGrid() {
        $("#" + jsGridId).jsGrid({
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
            pagerContainer: "#externalPager",
            controller: {
                loadData: function(filter) {
                    var d = $.Deferred();
                    $("#pageIndex").val(filter.pageIndex);

                    if ($("#chkIncludeDeleted").is(":checked")) {
                        $("#useYn").val("false");
                    } else {
                        $("#useYn").val("true");
                    }

                    $.ajax({
                        type : "POST",
                        url: "<c:url value='/toy/admin/sys/mngr/list.doax'/>",
                        data: $("#searchForm").serialize(),
                        dataType: "json"
                    }).done(function(response) {
                        var itemsCount = response.itemsCount || 0;
                        $("#itemsCount").text(gfn_commas(itemsCount));

                        if (!response.data || response.data.length === 0) {
                            $("#" + jsGridId).jsGrid("option", "noDataContent",
                                '<div class="not-content"><span class="icon"></span><div class="text">' + I18N_MNGR_LIST.noData + '</div></div>'
                            );
                        } else {
                            $("#" + jsGridId).jsGrid("option", "noDataContent", "");
                        }

                        d.resolve(response);
                    }).fail(function(jqXHR) {
                        d.reject(jqXHR);
                        fn_AjaxError(jqXHR);
                    });

                    return d.promise();
                }
            },
            fields: [
                { name: "rn", title: I18N_MNGR_LIST.colNo, type: "number", width: 40, align: "center" },
                { name: "mngrUid", title: I18N_MNGR_LIST.colId, type: "text", width: 110, align: "left" },
                { name: "mngrNm", title: I18N_MNGR_LIST.colName, type: "text", width: 140, align: "left" },
                { name: "telno", title: I18N_MNGR_LIST.colPhone, type: "text", width: 130, align: "center" },
                { name: "emlAdres", title: I18N_MNGR_LIST.colEmail, type: "text", width: 180, align: "left" },
                { name: "useYn", title: I18N_MNGR_LIST.colUse, type: "text", width: 60, align: "center" },
                { name: "authAppliedYn", title: I18N_MNGR_LIST.colAuthApplied, type: "text", width: 90, align: "center" },
                {
                    title: I18N_MNGR_LIST.colActions,
                    width: 140,
                    align: "center",
                    itemTemplate: function(_, item) {
                        var $wrap = $("<div/>");

                        var $btnDetail = $("<button/>")
                            .addClass("btn blue sm")
                            .attr("type", "button")
                            .text(I18N_MNGR_LIST.btnDetail)
                            .on("click", function() {
                                fn_openDetailPop(item.mngrUid);
                            });

                        var $btnDisable = $("<button/>")
                            .addClass("btn gray sm")
                            .attr("type", "button")
                            .css("margin-left", "6px")
                            .text(I18N_MNGR_LIST.btnDisable)
                            .on("click", function() {
                                fn_disableMngr(item.mngrUid);
                            });

                        $wrap.append($btnDetail).append($btnDisable);
                        return $wrap;
                    }
                }
            ]
        });
    }
</script>
</head>
<body>
<div id="wrapper">

    <%-- Common Header/Side Menu --%>
    <jsp:include page="/WEB-INF/jsp/toy/admin/include/adminHeader.jsp" flush="false"/>

    <main id="main">
        <section class="contents-wrap">
            <div class="title-area">
                <h2 class="depth-title"><spring:message code="admin.system.mngr.list.title" /></h2>
            </div>

            <section class="search-form">
                <div class="form-inline2">
                    <form name="searchForm" id="searchForm" method="get" action="${curURL}" onsubmit="fn_Search(); return false;">
                        <input type="hidden" id="pageIndex" name="pageIndex" value="1"/>
                        <input type="hidden" id="useYn" name="useYn" value="true"/>

                        <div class="form-group">
                            <label class="tit"><spring:message code="admin.system.mngr.common.field.id" /></label>
                            <input type="text" placeholder="<spring:message code='admin.system.mngr.list.placeholder.id' />" class="w150" id="sMngrUid" name="mngrUid" value="">
                        </div>

                        <div class="form-group">
                            <label class="tit"><spring:message code="admin.system.mngr.common.field.name" /></label>
                            <input type="text" placeholder="<spring:message code='admin.system.mngr.list.placeholder.name' />" class="w200" id="sMngrNm" name="mngrNm" value="">
                        </div>

                        <div class="form-group">
                            <label class="tit"><spring:message code="admin.system.mngr.list.field.deleted" /></label>
                            <label style="display:flex; align-items:center; gap:6px;">
                                <input type="checkbox" id="chkIncludeDeleted" />
                                <spring:message code="admin.system.mngr.list.field.includeDeleted" />
                            </label>
                        </div>

                        <button type="submit" class="btn block search">
                            <i class="material-icons-outlined">search</i> <spring:message code="admin.common.button.search" />
                        </button>

                        <button type="button" class="btn block gray" onclick="fn_Reset();">
                            <i class="material-icons-outlined">refresh</i> <spring:message code="admin.common.button.reset" />
                        </button>
                    </form>
                </div>
            </section>

            <section class="content-box">
                <h3 class="title2"><spring:message code="admin.common.searchResult" /><small>[<spring:message code="admin.common.total" /> <strong id="itemsCount" class="text-blue">0</strong><spring:message code="admin.common.countUnit" />]</small></h3>

                <div class="table-content-wrapper">
                    <div id="MngrJsGrid"></div>
                    <div id="externalPager" style="text-align:center;"></div>

                    <div class="btn-right st1">
                        <a class="btn blue" href="javascript:fn_openInsertPop();">
                            <i class="material-icons-outlined">add</i> <spring:message code="admin.common.button.create" />
                        </a>
                    </div>
                </div>
            </section>
        </section>
    </main>
</div>
</body>
</html>
