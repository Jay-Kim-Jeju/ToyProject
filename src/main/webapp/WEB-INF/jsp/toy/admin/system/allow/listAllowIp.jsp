<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>

<!-- jsGrid -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid-theme.css" />
<script src="${pageContext.request.contextPath}/js/com/jsgrid/jsgrid.js"></script>
<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>

<script type="text/javascript">
    var I18N_ALLOW_LIST = {
        loadFailed: "<spring:message code='admin.system.allow.list.alert.loadFailed' javaScriptEscape='true' />",
        requiredParam: "<spring:message code='admin.system.allow.list.alert.requiredParam' javaScriptEscape='true' />",
        confirmDelete: "<spring:message code='admin.system.allow.list.confirm.delete' javaScriptEscape='true' />",
        deleted: "<spring:message code='admin.system.allow.list.alert.deleted' javaScriptEscape='true' />",
        notFound: "<spring:message code='admin.system.allow.list.alert.notFound' javaScriptEscape='true' />",
        invalidRequest: "<spring:message code='admin.system.allow.list.alert.invalidRequest' javaScriptEscape='true' />",
        deleteFailed: "<spring:message code='admin.system.allow.list.alert.deleteFailed' javaScriptEscape='true' />",
        btnEditDisabled: "<spring:message code='admin.system.allow.list.button.editDisabled' javaScriptEscape='true' />",
        statusDeleted: "<spring:message code='admin.system.allow.list.status.deleted' javaScriptEscape='true' />"
    };

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
                        alert(I18N_ALLOW_LIST.loadFailed);
                        d.resolve({ data: [], itemsCount: 0 });
                    });

                    return d.promise();
                }
            },
            fields: [
                { name: "allowIp", title: "<spring:message code='admin.system.allow.common.field.allowIp' javaScriptEscape='true' />", type: "text", width: 140, align: "center" },
                { name: "mngrUid", title: "<spring:message code='admin.system.allow.common.field.mngrUid' javaScriptEscape='true' />", type: "text", width: 120, align: "center" },
                { name: "useYn", title: "<spring:message code='admin.system.allow.common.field.useYn' javaScriptEscape='true' />", type: "checkbox", width: 70, align: "center" },
                { name: "memo", title: "<spring:message code='admin.system.allow.common.field.memo' javaScriptEscape='true' />", type: "text", width: 220, align: "left", filtering: false },
                {
                    type: "control", width: 180,
                    itemTemplate: function(_, item) {
                        var isActive = fn_isActiveUseYn(item && item.useYn);
                        var editBtn;
                        var delBtn;

                        if (isActive) {
                            editBtn = $("<a>").addClass("btn black sm")
                                .append("<i class=\"material-icons-outlined\">edit</i>")
                                .append(" <spring:message code='admin.common.button.edit' javaScriptEscape='true' />")
                                .click(function(e) {
                                    fn_openAllowIpFormPopup(item.allowIpUuid);
                                    e.stopPropagation();
                                });

                            delBtn = $("<a>").addClass("btn orange sm")
                                .append("<i class=\"material-icons-outlined\">delete</i>")
                                .append(" <spring:message code='admin.common.button.delete' javaScriptEscape='true' />")
                                .click(function(e) {
                                    fn_softDeleteAllowIp(item.allowIpUuid);
                                    e.stopPropagation();
                                });
                        } else {
                            editBtn = $("<a>").addClass("btn gray sm")
                                .css({ "pointer-events": "none", "opacity": "0.7", "cursor": "default" })
                                .append("<i class=\"material-icons-outlined\">lock</i>")
                                .append(" " + I18N_ALLOW_LIST.btnEditDisabled);

                            delBtn = $("<a>").addClass("btn gray sm")
                                .css({ "pointer-events": "none", "opacity": "0.7", "cursor": "default" })
                                .append("<i class=\"material-icons-outlined\">block</i>")
                                .append(" " + I18N_ALLOW_LIST.statusDeleted);
                        }

                        return $("<div>").append(editBtn).append("&nbsp;").append(delBtn);
                    },
                    filterTemplate: function() {
                        var searchBtn = $("<a>").addClass("btn sm")
                            .append("<i class=\"material-icons-outlined\">search</i>")
                            .append(" <spring:message code='admin.common.button.search' javaScriptEscape='true' />")
                            .click(function(e) {
                                $("#allowIpGrid").jsGrid("search");
                                e.stopPropagation();
                            });

                        var clearBtn = $("<a>").addClass("btn gray sm")
                            .append("<i class=\"material-icons-outlined\">clear</i>")
                            .append(" <spring:message code='admin.common.button.clear' javaScriptEscape='true' />")
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
            alert(I18N_ALLOW_LIST.requiredParam);
            return;
        }
        if (!confirm(I18N_ALLOW_LIST.confirmDelete)) {
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
                    alert(I18N_ALLOW_LIST.deleted);
                    fn_allowIpGridRefresh();
                    return;
                }
                if (res.result === "None") {
                    alert(res.errorMessage || I18N_ALLOW_LIST.notFound);
                    fn_allowIpGridRefresh();
                    return;
                }
                if (res.result === "Invalid") {
                    alert(res.errorMessage || I18N_ALLOW_LIST.invalidRequest);
                    return;
                }
                alert(res.errorMessage || I18N_ALLOW_LIST.deleteFailed);
            },
            error: function(request, status, error) {
                fn_AjaxError(request, status, error, "${CONTEXT_PATH}/toy/admin/login.do");
            }
        });
    }

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
                <h2 class="depth-title"><spring:message code="admin.system.allow.list.title" /></h2>
            </div>

            <section class="content-box">
                <h3 class="title2">
                    <spring:message code="admin.common.searchResult" />
                    <small>[<spring:message code="admin.common.total" /> <strong id="allowIpCount" class="text-blue">0</strong><spring:message code="admin.common.countUnit" />]</small>
                </h3>

                <div id="allowIpGrid"></div>

                <div class="paging-group">
                    <div id="externalPager" style="text-align:center;"></div>
                </div>

                <div class="btn-right st1">
                    <a class="btn blue" href="javascript:fn_openAllowIpFormPopup();">
                        <i class="material-icons-outlined">add</i> <spring:message code="admin.common.button.create" />
                    </a>
                </div>
            </section>
        </section>
    </main>
</div>
</body>
</html>
