<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2026-01-14
  Time: 오전 12:48
  To change this template use File | Settings | File Templates.
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

    $(function () {
        fn_setMngrJsGrid();
    });

    // Popup: insert
    function fn_openInsertPop() {
        window.open("<c:url value='/toy/admin/sys/mngr/insertPop.do'/>", "insertPopMngr",
            "width=560,height=260,scrollbars=yes");
    }

    // Popup: detail
    function fn_openDetailPop(mngrUid) {
        if (!mngrUid) return;
        window.open("<c:url value='/toy/admin/sys/mngr/detailPop.do'/>?mngrUid=" + encodeURIComponent(mngrUid),
            "detailPopMngr", "width=720,height=420,scrollbars=yes");
    }

    // Grid refresh (for popups)
    function fn_refreshMngrGrid() {
        $("#" + jsGridId).jsGrid("loadData");
    }

    // Search submit
    function fn_Search() {
        // move first page then reload
        $("#" + jsGridId).jsGrid({ pageIndex: 1 });
        $("#" + jsGridId).jsGrid("loadData");
    }

    // Reset search
    function fn_Reset() {
        $("#sMngrUid").val("");
        $("#sMngrNm").val("");
        $("#chkIncludeDeleted").prop("checked", false);

        // reset to default filter (active only)
        $("#useYn").val("true");

        $("#" + jsGridId).jsGrid({ pageIndex: 1 });
        $("#" + jsGridId).jsGrid("loadData");
    }

    // Disable manager (soft delete)
    function fn_disableMngr(mngrUid) {
        if (!mngrUid) return;

        if (!confirm("Disable this manager?")) {
            return;
        }

        $.ajax({
            type: "POST",
            url: "<c:url value='/toy/admin/sys/mngr/disable.ac'/>",
            data: { mngrUid: mngrUid },
            success: function (data) {
                var r = (data && data.result) ? String(data.result) : "N";

                if (r === "Y") {
                    alert("Manager disabled.");
                    fn_refreshMngrGrid();
                    return;
                }

                if (r === "None") {
                    alert("Manager does not exist.");
                    fn_refreshMngrGrid();
                    return;
                }

                alert((data && data.errorMessage) ? data.errorMessage : "Disable failed. Please contact the administrator.");
            },
            error: fn_AjaxError
        });
    }

    // Build jsGrid
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

                    // pageIndex is required for PagingDefaultVO
                    $("#pageIndex").val(filter.pageIndex);

                    // Include deleted checked => no useYn filter
                    // Controller normalizeUseYnForSearch expects "true/false"
                    if ($("#chkIncludeDeleted").is(":checked")) {
                        $("#useYn").val("false"); // -> normalized to null (no filter)
                    } else {
                        $("#useYn").val("true");  // -> normalized to "Y"
                    }

                    $.ajax({
                        type : "POST",
                        url: "<c:url value='/toy/admin/sys/mngr/list.doax'/>",
                        data: $("#searchForm").serialize(),
                        dataType: "json"
                    }).done(function(response) {

                        var itemsCount = response.itemsCount || 0;
                        $("#itemsCount").text(gfn_commas(itemsCount));

                        // No data content
                        if (!response.data || response.data.length === 0) {
                            $("#" + jsGridId).jsGrid("option", "noDataContent",
                                '<div class="not-content"><span class="icon"></span><div class="text">No results found.</div></div>'
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
                { name: "rn", title: "No", type: "number", width: 40, align: "center" },
                { name: "mngrUid", title: "ID", type: "text", width: 110, align: "left" },
                { name: "mngrNm", title: "Name", type: "text", width: 140, align: "left" },
                { name: "telno", title: "Phone", type: "text", width: 130, align: "center" },
                { name: "emlAdres", title: "Email", type: "text", width: 180, align: "left" },
                { name: "useYn", title: "Use", type: "text", width: 60, align: "center" },
                { name: "authAppliedYn", title: "Auth Applied", type: "text", width: 90, align: "center" },
                {
                    title: "Actions",
                    width: 140,
                    align: "center",
                    itemTemplate: function(_, item) {
                        var $wrap = $("<div/>");

                        var $btnDetail = $("<button/>")
                            .addClass("btn blue sm")
                            .attr("type", "button")
                            .text("Detail")
                            .on("click", function() {
                                fn_openDetailPop(item.mngrUid);
                            });

                        var $btnDisable = $("<button/>")
                            .addClass("btn gray sm")
                            .attr("type", "button")
                            .css("margin-left", "6px")
                            .text("Disable")
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
                    <h2 class="depth-title">관리자관리</h2>
                </div>

                <!-- Search -->
                <section class="search-form">
                    <div class="form-inline2">
                        <form name="searchForm" id="searchForm" method="get" action="${curURL}" onsubmit="fn_Search(); return false;">
                            <input type="hidden" id="pageIndex" name="pageIndex" value="1"/>
                            <input type="hidden" id="useYn" name="useYn" value="true"/>

                            <div class="form-group">
                                <label class="tit">ID</label>
                                <input type="text" placeholder="Manager ID" class="w150" id="sMngrUid" name="mngrUid" value="">
                            </div>

                            <div class="form-group">
                                <label class="tit">Name</label>
                                <input type="text" placeholder="Manager name" class="w200" id="sMngrNm" name="mngrNm" value="">
                            </div>

                            <div class="form-group">
                                <label class="tit">Deleted</label>
                                <label style="display:flex; align-items:center; gap:6px;">
                                    <input type="checkbox" id="chkIncludeDeleted" />
                                    Include deleted
                                </label>
                            </div>

                            <button type="submit" class="btn block search">
                                <i class="material-icons-outlined">search</i> Search
                            </button>

                            <button type="button" class="btn block gray" onclick="fn_Reset();">
                                <i class="material-icons-outlined">refresh</i> Reset
                            </button>
                        </form>
                    </div>
                </section>

                <section class="content-box">
                    <h3 class="title2">검색결과 <small>[총 <strong id="itemsCount" class="text-blue">0</strong>건]</small></h3>

                    <!-- 내용 -->
                    <div class="table-content-wrapper">
                        <div id="MngrJsGrid"></div>
                        <div id="externalPager" style="text-align:center;"></div>

                        <div class="btn-right st1">
                            <a class="btn blue" href="javascript:fn_openInsertPop();">
                                <i class="material-icons-outlined">add</i> Create
                            </a>
                        </div>

                    </div>
                    <!-- 내용 -->

                </section>

            </section>
        </main>
    </div>
</body>
</html>
