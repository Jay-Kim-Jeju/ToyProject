<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2026-01-02
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>

<!-- jsgrid -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid-theme.css" />
<script src="${pageContext.request.contextPath}/js/com/jsgrid/jsgrid.js"></script>

<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>

<script type="text/javascript">
    var selectedMngrUids = [];
    var I18N_AUTH_ASSIGN_POP = {
        authRequired: "<spring:message code='admin.common.authRequired' javaScriptEscape='true' />",
        noData: "<spring:message code='admin.common.grid.noResults' javaScriptEscape='true' />",
        loadFailed: "<spring:message code='admin.system.auth.assignPop.alert.loadFailed' javaScriptEscape='true' />",
        selectRequired: "<spring:message code='admin.system.auth.assignPop.alert.selectRequired' javaScriptEscape='true' />",
        confirmAssign: "<spring:message code='admin.system.auth.assignPop.confirm.assign' javaScriptEscape='true' />",
        assigned: "<spring:message code='admin.system.auth.assignPop.alert.assigned' javaScriptEscape='true' />",
        invalidData: "<spring:message code='admin.common.invalidData' javaScriptEscape='true' />",
        assignFailed: "<spring:message code='admin.system.auth.assignPop.alert.assignFailed' javaScriptEscape='true' />"
    };

    $(function () {
        fn_setUnassignedMngrJsGrid();
    });

    function selectItem(item) {
        if (!item || !item.mngrUid) return;
        selectedMngrUids.push(item.mngrUid);
    }

    function unselectItem(item) {
        if (!item || !item.mngrUid) return;
        selectedMngrUids = $.grep(selectedMngrUids, function(v) { return v !== item.mngrUid; });
    }

    function fn_setUnassignedMngrJsGrid() {
        $("#jsGrid").jsGrid({
            width: "100%",
            height: "auto",
            filtering: true,
            autoload: true,
            sorting: false,
            paging: true,
            pageLoading: true,
            pageSize: 10,
            pageIndex: 1,
            pageButtonCount: 10,
            pagerContainer: "#externalPager",
            controller: {
                loadData: function(filter) {
                    var d = $.Deferred();
                    selectedMngrUids = [];

                    $.ajax({
                        type: "POST",
                        url: "${CONTEXT_PATH}/toy/admin/sys/auth/mngr/unassigned/list.doax?authUuid=${param.authUuid}",
                        data: filter,
                        dataType: "json",
                        beforeSend: function (xhr) {}
                    }).done(function(res) {
                        if (res && res.redirectUrl) {
                            alert(res.errorMessage || I18N_AUTH_ASSIGN_POP.authRequired);
                            if (window.opener) { window.opener.location.href = res.redirectUrl; }
                            window.close();
                            return;
                        }

                        if (!res || !res.data || res.data.length === 0) {
                            $("#jsGrid").jsGrid({
                                autoload: false,
                                noDataContent: '<div class="not-content"><span class="icon"></span><div class="text">' + I18N_AUTH_ASSIGN_POP.noData + '</div></div>'
                            });

                            $('.jsgrid-filter-row').find('td').eq(1).find('input').val(filter.mngrUid || "");
                            $('.jsgrid-filter-row').find('td').eq(2).find('input').val(filter.mngrNm || "");
                        }

                        $("#unAuthMngrCount").text(res.itemsCount || 0);
                        d.resolve(res);
                    }).fail(function(xhr) {
                        console.error("unassigned/list.doax failed", xhr.status, xhr.responseText);
                        alert(I18N_AUTH_ASSIGN_POP.loadFailed);
                        d.resolve({ data: [], itemsCount: 0 });
                    });

                    return d.promise();
                }
            },
            fields: [
                {
                    itemTemplate: function(_, item) {
                        return $("<input>")
                            .attr("type", "checkbox")
                            .on("change", function() {
                                $(this).is(":checked") ? selectItem(item) : unselectItem(item);
                            });
                    },
                    align: "center",
                    width: 50,
                    filtering: false,
                    sorting: false
                },
                { name: "mngrUid", title: "<spring:message code='admin.system.mngr.common.field.id' javaScriptEscape='true' />", type: "text", width: 110, align: "center" },
                { name: "mngrNm", title: "<spring:message code='admin.system.mngr.common.field.name' javaScriptEscape='true' />", type: "text", width: 120, align: "left" },
                { name: "emlAdres", title: "<spring:message code='admin.system.mngr.common.field.email' javaScriptEscape='true' />", type: "text", width: 170, align: "left", filtering: false },
                { name: "telno", title: "<spring:message code='admin.system.mngr.common.field.phone' javaScriptEscape='true' />", type: "text", width: 120, align: "center", filtering: false },
                { name: "useYn", title: "<spring:message code='admin.system.mngr.common.field.useYn' javaScriptEscape='true' />", type: "checkbox", width: 70, align: "center", filtering: false }
            ]
        });
    }

    function fn_assignSelectedManagers() {
        if (!selectedMngrUids || selectedMngrUids.length === 0) {
            alert(I18N_AUTH_ASSIGN_POP.selectRequired);
            return;
        }
        if (!confirm(I18N_AUTH_ASSIGN_POP.confirmAssign)) {
            return;
        }

        $.ajax({
            type: "POST",
            url: "${CONTEXT_PATH}/toy/admin/sys/auth/mngr/assign.ac?authUuid=${param.authUuid}",
            data: JSON.stringify(selectedMngrUids),
            contentType: "application/json; charset=UTF-8",
            dataType: "json",
            beforeSend: function(xhr) {},
            success: function(res) {
                if (res && res.redirectUrl) {
                    alert(res.errorMessage || I18N_AUTH_ASSIGN_POP.authRequired);
                    if (window.opener) { window.opener.location.href = res.redirectUrl; }
                    window.close();
                    return;
                }

                if (res.result === "Y") {
                    alert(res.errorMessage || I18N_AUTH_ASSIGN_POP.assigned);

                    if (window.opener && window.opener.fn_assignedMngrRefresh) {
                        window.opener.fn_assignedMngrRefresh();
                    } else if (window.opener) {
                        window.opener.location.reload();
                    }
                    window.close();
                    return;
                }

                if (res.result === "Invalid") {
                    alert(res.errorMessage || I18N_AUTH_ASSIGN_POP.invalidData);
                    return;
                }

                alert(res.errorMessage || I18N_AUTH_ASSIGN_POP.assignFailed);
            },
            error: function(request, status, error) {
                fn_AjaxError(request, status, error, "${CONTEXT_PATH}/toy/admin/login.do");
            }
        });
    }
</script>
</head>

<body>
<div class="win-popup">
    <div class="flex justify baseline title2-area">
        <h3 class="title2"><spring:message code="admin.system.auth.assignPop.title" /></h3>
        <div class="title-justify">
            <h3 class="title2"><spring:message code="admin.common.searchResult" /><small>[<spring:message code="admin.common.total" /> <strong id="unAuthMngrCount" class="text-blue">0</strong><spring:message code="admin.common.countUnit" />]</small></h3>
        </div>
    </div>

    <div id="jsGrid"></div>
    <div id="externalPager" style="margin: 10px 0; font-size: 14px; color: #262626; font-weight: 300;"></div>

    <div class="btn-right st1 mb-0">
        <a class="btn blue" href="javascript:fn_assignSelectedManagers();"><i class="material-icons-outlined">add</i> <spring:message code="admin.common.button.add" /></a>
        <a class="btn gray" href="javascript:window.close();"><i class="material-icons-outlined">close</i> <spring:message code="admin.common.button.close" /></a>
    </div>
</div>
</body>
</html>
