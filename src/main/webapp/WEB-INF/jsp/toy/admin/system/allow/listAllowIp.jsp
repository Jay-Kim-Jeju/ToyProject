<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>

<!-- jsGrid -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid-theme.css" />
<script src="${pageContext.request.contextPath}/js/com/jsgrid/jsgrid.js"></script>
<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>

<script type="text/javascript">
    $(function () {
        fn_setAllowIpJsGrid();
    });

    function fn_setAllowIpJsGrid() {
        $("#allowIpGrid").jsGrid({
            width: "100%",
            height: "auto",

            filtering: true,
            autoload: true,
            sorting: false,
            inserting: false,
            editing: false,

            paging: true,
            pageLoading: true,
            pageSize: 10,
            pageButtonCount: 10,
            pagerContainer: "#externalPager",

            controller: {
                loadData: function(filter) {
                    var d = $.Deferred();

                    $.ajax({
                        type: "POST",
                        url: "${CONTEXT_PATH}/toy/admin/sys/allow/list.doax",
                        data: filter,
                        dataType: "json"
                    }).done(function(res) {
                        if (res && res.redirectUrl) {
                            window.location.href = res.redirectUrl;
                            return;
                        }

                        $("#allowIpCount").text(res.itemsCount || 0);
                        d.resolve(res || { data: [], itemsCount: 0 });
                    }).fail(function(xhr) {
                        console.error("allow/list.doax failed", xhr.status, xhr.responseText);
                        alert("접속허용IP 목록 조회에 실패했습니다.");
                        d.resolve({ data: [], itemsCount: 0 });
                    });

                    return d.promise();
                }
            },

            fields: [
                { name: "allowIp", title: "접속허용IP", type: "text", width: 140, align: "center" },
                { name: "mngrUid", title: "등록된 관리자ID", type: "text", width: 120, align: "center" },
                { name: "useYn", title: "사용여부", type: "checkbox", width: 70, align: "center" },
                { name: "memo", title: "메모", type: "text", width: 220, align: "left", filtering: false },
                {
                    type: "control", width: 180,
                    itemTemplate: function(_, item) {
                        var isActive = fn_isActiveUseYn(item && item.useYn);
                        var editBtn;
                        var delBtn;

                        if (isActive) {
                            editBtn = $("<a>").addClass("btn black sm")
                                .append("<i class=\"material-icons-outlined\">edit</i>")
                                .append(" 수정")
                                .click(function(e) {
                                    fn_openAllowIpFormPopup(item.allowIpUuid);
                                    e.stopPropagation();
                                });

                            delBtn = $("<a>").addClass("btn orange sm")
                                .append("<i class=\"material-icons-outlined\">delete</i>")
                                .append(" 삭제")
                                .click(function(e) {
                                    fn_softDeleteAllowIp(item.allowIpUuid);
                                    e.stopPropagation();
                                });
                        } else {
                            // Terminal-state policy: soft-deleted rows cannot be edited or re-deleted.
                            editBtn = $("<a>").addClass("btn gray sm")
                                .css({ "pointer-events": "none", "opacity": "0.7", "cursor": "default" })
                                .append("<i class=\"material-icons-outlined\">lock</i>")
                                .append(" 수정불가");

                            delBtn = $("<a>").addClass("btn gray sm")
                                .css({ "pointer-events": "none", "opacity": "0.7", "cursor": "default" })
                                .append("<i class=\"material-icons-outlined\">block</i>")
                                .append(" Deleted");
                        }

                        return $("<div>").append(editBtn).append("&nbsp;").append(delBtn);
                    },
                    filterTemplate: function() {
                        var searchBtn = $("<a>").addClass("btn sm")
                            .append("<i class=\"material-icons-outlined\">search</i>")
                            .append(" Search")
                            .click(function(e) {
                                $("#allowIpGrid").jsGrid("search");
                                e.stopPropagation();
                            });

                        var clearBtn = $("<a>").addClass("btn gray sm")
                            .append("<i class=\"material-icons-outlined\">clear</i>")
                            .append(" Clear")
                            .click(function(e) {
                                $("#allowIpGrid").jsGrid("clearFilter");
                                e.stopPropagation();
                            });

                        return $("<div>").append(searchBtn).append("&nbsp;").append(clearBtn);
                    }
                }
            ]
        });
    }

    function fn_openAllowIpFormPopup(allowIpUuid) {
        var url = "${CONTEXT_PATH}/toy/admin/sys/allow/formPop.do";
        if (allowIpUuid) {
            url += "?allowIpUuid=" + encodeURIComponent(allowIpUuid);
        }
        window.open(url, "formPopAllowIp", "resizable=yes,scrollbars=yes,width=860,height=560");
    }

    function fn_softDeleteAllowIp(allowIpUuid) {
        if (!allowIpUuid) {
            alert("필수 파라미터가 없습니다.");
            return;
        }
        if (!confirm("해당 허용IP를 삭제(비활성화)하시겠습니까?")) {
            return;
        }

        $.ajax({
            type: "POST",
            url: "${CONTEXT_PATH}/toy/admin/sys/allow/delete.ac",
            data: { allowIpUuid: allowIpUuid },
            dataType: "json",
            success: function(res) {
                if (res && res.redirectUrl) {
                    window.location.href = res.redirectUrl;
                    return;
                }
                if (res.result === "Y") {
                    alert("정상적으로 삭제(비활성화) 처리되었습니다.");
                    fn_allowIpGridRefresh();
                    return;
                }
                if (res.result === "None") {
                    alert(res.errorMessage || "대상이 존재하지 않습니다.");
                    fn_allowIpGridRefresh();
                    return;
                }
                if (res.result === "Invalid") {
                    alert(res.errorMessage || "잘못된 요청입니다.");
                    return;
                }
                alert(res.errorMessage || "삭제 처리에 실패했습니다.");
            },
            error: function(request, status, error) {
                fn_AjaxError(request, status, error, "${CONTEXT_PATH}/toy/admin/login.do");
            }
        });
    }

    // Popup calls this after insert/update succeeds.
    function fn_allowIpGridRefresh() {
        if ($("#allowIpGrid").data("JSGrid")) {
            $("#allowIpGrid").jsGrid("loadData");
        }
    }

    function fn_isActiveUseYn(v) {
        if (v === true) return true;
        if (v === false || v == null) return false;
        v = String(v).trim().toUpperCase();
        return (v === "Y" || v === "TRUE" || v === "1");
    }
</script>
</head>
<body>
<div id="wrapper">
    <jsp:include page="/WEB-INF/jsp/toy/admin/include/adminHeader.jsp" flush="false" />

    <main id="main">
        <section class="contents-wrap">
            <div class="title-area">
                <h2 class="depth-title">접속허용IP 관리</h2>
            </div>

            <section class="content-box">
                <h3 class="title2">
                    검색결과
                    <small>[총 <strong id="allowIpCount" class="text-blue">0</strong>건]</small>
                </h3>

                <div id="allowIpGrid"></div>

                <div class="paging-group">
                    <div id="externalPager" style="text-align:center;"></div>
                </div>

                <div class="btn-right st1">
                    <a class="btn blue" href="javascript:fn_openAllowIpFormPopup();">
                        <i class="material-icons-outlined">add</i> 등록
                    </a>
                </div>
            </section>
        </section>
    </main>
</div>
</body>
</html>
