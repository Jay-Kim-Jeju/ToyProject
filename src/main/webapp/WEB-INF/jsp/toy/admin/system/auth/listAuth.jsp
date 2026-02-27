<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2026-01-02
  Time: ?ㅽ썑 4:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="toy.com.util.CmConstants" %>

<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>

<!-- jsgrid -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid-theme.css" />

<script src="${pageContext.request.contextPath}/js/com/jsgrid/jsgrid.js"></script>
<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>

<script type="text/javascript">
    $(function () {
        fn_setAuthRoleJsGrid();
    });

    // Administrator role must not be disabled to prevent breaking global auth behavior.
    var ROLE_ADMIN = "<%=CmConstants.ROLE_ADMINISTRATOR%>";

    // Selected authUuid (used for right grid + popup)
    var publicAuthUuid = "";

    function normalizeAuthUuid(v) {
        // Normalize role IDs for safe comparisons and consistent requests.
        return (v == null) ? "" : String(v).trim().toUpperCase();
    }

    function fn_setAuthRoleJsGrid() {
        $("#authRoleGrid").jsGrid({
            width: "750px",
            height: "auto",

            filtering: true,
            autoload: true,
            sorting: false,
            editing: true,
            inserting: true,

            paging: true,
            pageLoading: true,
            pageSize: 10,
            pageButtonCount: 10,
            pagerContainer: "#externalPagerLeft",

            controller: {
                loadData: function(filter) {
                    var d = $.Deferred();

                    $.ajax({
                        type: "POST",
                        url: "${CONTEXT_PATH}/toy/admin/sys/auth/role/list.doax",
                        data: filter,
                        beforeSend: function (xhr) {
                            // Your custom logic only (loading spinner etc.)
                        },
                        dataType: "json"
                    }).done(function(res) {
                        if (res && res.redirectUrl) {
                            window.location.href = res.redirectUrl;
                            return;
                        }

                        if (!res || !res.data || res.data.length === 0) {
                            $("#authRoleGrid").jsGrid({
                                autoload: false,
                                noDataContent: '<div class="not-content"><span class="icon"></span><div class="text"><spring:message code="admin.common.grid.noResults" /></div></div>'
                            });

                            // Restore filter inputs for better UX.
                            $('.jsgrid-filter-row').find('td').eq(0).find('input').val(filter.authUuid || "");
                            $('.jsgrid-filter-row').find('td').eq(1).find('input').val(filter.authorDc || "");
                            if (typeof filter.useYn !== "undefined") {
                                $('.jsgrid-filter-row').find('td').eq(2).find('input').prop({
                                    checked: filter.useYn,
                                    readOnly: false,
                                    indeterminate: false
                                });
                            }
                        }

                        $("#roleCount").text(res.itemsCount || 0);
                        d.resolve(res);
                    }).fail(function(xhr) {
                        console.error("auth/role/list.doax failed", xhr.status, xhr.responseText);
                        alert("<spring:message code='admin.system.auth.list.alert.loadRoleFailed' javaScriptEscape='true' />");
                        d.resolve({ data: [], itemsCount: 0 });
                    });

                    return d.promise();
                },

                insertItem: function(data) {
                    data.authUuid = normalizeAuthUuid(data.authUuid);
                    if (!data.authUuid || !data.authorDc) {
                        alert("<spring:message code='admin.system.auth.list.alert.requiredRoleFields' javaScriptEscape='true' />");
                        return;
                    }
                    if (!confirm("<spring:message code='admin.system.auth.list.confirm.insertRole' javaScriptEscape='true' />")) {
                        return;
                    }

                    $.ajax({
                        type: "POST",
                        url: "${CONTEXT_PATH}/toy/admin/sys/auth/role/insert.ac",
                        data: data,
                        beforeSend: function (xhr) {
                            // Your custom logic only (loading spinner etc.)
                        },
                        dataType: "json",
                        success: function(res) {
                            if (res && res.redirectUrl) { window.location.href = res.redirectUrl; return; }
                            if (res.result === "Y") { alert("<spring:message code='admin.system.auth.list.alert.insertedRole' javaScriptEscape='true' />"); fn_setAuthRoleJsGrid(); return; }
                            if (res.result === "Duple") { alert(res.errorMessage || "<spring:message code='admin.system.auth.list.alert.duplicateRole' javaScriptEscape='true' />"); return; }
                            if (res.result === "Invalid") { alert(res.errorMessage || "<spring:message code='admin.common.invalidData' javaScriptEscape='true' />"); return; }
                            alert(res.errorMessage || "<spring:message code='admin.system.auth.list.alert.insertFailed' javaScriptEscape='true' />");
                        },
                        error: function(request, status, error) {
                            fn_AjaxError(request, status, error, "${CONTEXT_PATH}/toy/admin/login.do");
                        }
                    });
                },

                updateItem: function(data) {
                    var authUuidNorm = normalizeAuthUuid(data.authUuid);
                    if (!data.authUuid || !data.authorDc) {
                        alert("<spring:message code='admin.system.auth.list.alert.requiredRoleFields' javaScriptEscape='true' />");
                        return;
                    }
                    if (!confirm("<spring:message code='admin.system.auth.list.confirm.updateRole' javaScriptEscape='true' />")) {
                        return;
                    }

                    var payload = {
                        authUuid: authUuidNorm,
                        authorDc: data.authorDc,
                        // Always send Y/N (jsGrid checkbox can be boolean true/false).
                        // Administrator role must stay enabled.
                        useYn: (authUuidNorm === ROLE_ADMIN) ? "Y" : (data.useYn ? "Y" : "N")
                    };
                    console.log("useYn typeof=", typeof data.useYn, "value=", data.useYn);

                    $.ajax({
                        type: "POST",
                        url: "${CONTEXT_PATH}/toy/admin/sys/auth/role/update.ac",
                        data: payload,
                        beforeSend: function (xhr) {
                            // Your custom logic only (loading spinner etc.)
                        },
                        dataType: "json",
                        success: function(res) {
                            if (res && res.redirectUrl) { window.location.href = res.redirectUrl; return; }
                            if (res.result === "Y") { alert("<spring:message code='admin.system.auth.list.alert.updatedRole' javaScriptEscape='true' />"); fn_setAuthRoleJsGrid(); return; }
                            if (res.result === "None") { alert(res.errorMessage || "<spring:message code='admin.system.auth.list.alert.roleNotFound' javaScriptEscape='true' />"); fn_setAuthRoleJsGrid(); return; }
                            if (res.result === "Invalid") { alert(res.errorMessage || "<spring:message code='admin.common.invalidData' javaScriptEscape='true' />"); return; }
                            alert(res.errorMessage || "<spring:message code='admin.system.auth.list.alert.updateFailed' javaScriptEscape='true' />");
                        },
                        error: function(request, status, error) {
                            fn_AjaxError(request, status, error, "${CONTEXT_PATH}/toy/admin/login.do");
                        }
                    });
                }
            },

            rowClick: function(args) {
                publicAuthUuid = normalizeAuthUuid(args.item.authUuid || "");
                fn_setAssignedMngrJsGrid(publicAuthUuid);

                $(".jsgrid-pick-row").removeClass("jsgrid-pick-row");
                $(".jsgrid-selected-row").addClass("jsgrid-pick-row");
            },

            fields: [
                { name: "authUuid", title: "<spring:message code='admin.system.auth.list.grid.roleId' javaScriptEscape='true' />", type: "text", width: 120, align: "left", editing: false },
                { name: "authorDc", title: "<spring:message code='admin.system.auth.list.grid.roleName' javaScriptEscape='true' />", type: "text", width: 160, align: "left", sorting: false },
                { name: "useYn", title: "<spring:message code='admin.system.auth.list.grid.useYn' javaScriptEscape='true' />", type: "checkbox", width: 60, align: "center", inserting: false,
                    editTemplate: function(value, item) {
                        // Render checkbox and disable it for default roles.
                        var $cb = $("<input>").attr("type", "checkbox").prop("checked", value === true || value === "Y");
                        if (item && normalizeAuthUuid(item.authUuid) === ROLE_ADMIN) {
                            $cb.prop("checked", true);
                            $cb.prop("disabled", true);
                        }
                        // jsGrid uses this.editControl.is(":checked") internally for checkbox fields.
                        // If editControl is not set, updateItem will crash with "Cannot read properties of undefined (reading 'is')".
                        // conclu : jsGrid expects editControl for checkbox fields (uses editControl.is(":checked")).
                        this.editControl = $cb;

                        return $cb;
                    }
                },

                { type: "control", width: 160,
                    filterTemplate: function() {
                        var filterBtn = $("<a>").addClass("btn sm")
                            .append("<i class=\"material-icons-outlined\">search</i>")
                            .append(" <spring:message code='admin.common.button.search' javaScriptEscape='true' />")
                            .click(function(e) {
                                $("#authRoleGrid").jsGrid("search");
                                e.stopPropagation();
                            });

                        var clearBtn = $("<a>").addClass("btn gray sm")
                            .append("<i class=\"material-icons-outlined\">clear</i>")
                            .append(" <spring:message code='admin.common.button.clear' javaScriptEscape='true' />")
                            .click(function(e) {
                                $("#authRoleGrid").jsGrid("clearFilter");
                                e.stopPropagation();
                            });

                        return $("<div>").append(filterBtn).append("&nbsp;").append(clearBtn);
                    },

                    insertTemplate: function(_, item) {
                        var insertBtn = $("<a>").addClass("btn blue sm")
                            .append("<i class=\"material-icons-outlined\">add</i>")
                            .append(" <spring:message code='admin.common.button.add' javaScriptEscape='true' />")
                            .click(function(e) {
                                $("#authRoleGrid").jsGrid("insertItem", item);
                                e.stopPropagation();
                            });

                        var clearBtn = $("<a>").addClass("btn gray sm")
                            .append("<i class=\"material-icons-outlined\">clear</i>")
                            .append(" <spring:message code='admin.common.button.cancel' javaScriptEscape='true' />")
                            .click(function(e) {
                                $("#authRoleGrid").jsGrid("clearInsert");
                                e.stopPropagation();
                            });

                        return $("<div>").append(insertBtn).append("&nbsp;").append(clearBtn);
                    },

                    editTemplate: function(_, item) {
                        var saveBtn = $("<a>").addClass("btn blue sm")
                            .append("<i class=\"material-icons-outlined\">check</i>")
                            .append(" <spring:message code='admin.common.button.save' javaScriptEscape='true' />")
                            .click(function(e) {
                                $("#authRoleGrid").jsGrid("updateItem");
                                e.stopPropagation();
                            });

                        var cancelBtn = $("<a>").addClass("btn gray sm")
                            .append("<i class=\"material-icons-outlined\">clear</i>")
                            .append(" <spring:message code='admin.common.button.cancel' javaScriptEscape='true' />")
                            .click(function(e) {
                                $("#authRoleGrid").jsGrid("cancelEdit");
                                e.stopPropagation();
                            });

                        return $("<div>").append(saveBtn).append("&nbsp;").append(cancelBtn);
                    },

                    itemTemplate: function(_, item) {
                        var editBtn = $("<a>").addClass("btn black sm")
                            .append("<i class=\"material-icons-outlined\">edit</i>")
                            .append(" <spring:message code='admin.common.button.edit' javaScriptEscape='true' />")
                            .click(function(e) {
                                $("#authRoleGrid").jsGrid("editItem", item);
                                e.stopPropagation();
                            });

                        // Disable action for administrator role (UI guard).
                        var disableBtn;

                        var isAdminRole = (item && normalizeAuthUuid(item.authUuid) === ROLE_ADMIN);
                        if (isAdminRole) {
                            disableBtn = $("<a>").addClass("btn gray sm")
                                .append("<i class=\"material-icons-outlined\">lock</i>")
                                .append(" <spring:message code='admin.common.button.disable' javaScriptEscape='true' />")
                                .click(function(e) {
                                    alert("<spring:message code='admin.system.auth.list.alert.defaultRoleCannotDisable' javaScriptEscape='true' />");
                                    e.stopPropagation();
                                });
                        } else {
                            disableBtn = $("<a>").addClass("btn orange sm")
                                .append("<i class=\"material-icons-outlined\">delete</i>")
                                .append(" <spring:message code='admin.common.button.disable' javaScriptEscape='true' />")
                                .click(function(e) {
                                    fn_disableAuthRole(item.authUuid);
                                    e.stopPropagation();
                                });
                        }

                        return $("<div>").append(editBtn).append("&nbsp;").append(disableBtn);
                    }
                }
            ]
        });
    }

    function fn_disableAuthRole(authUuid) {
        authUuid = normalizeAuthUuid(authUuid);
        if (!authUuid) {
            alert("<spring:message code='admin.system.auth.list.alert.roleIdRequired' javaScriptEscape='true' />");
            return;
        }

        // Client-side guard (server also blocks in controller).
        if (authUuid === ROLE_ADMIN) {
            alert("<spring:message code='admin.system.auth.list.alert.defaultRoleCannotDisable' javaScriptEscape='true' />");
            return;
        }

        if (!confirm("<spring:message code='admin.system.auth.list.confirm.disableRole' javaScriptEscape='true' />")) {
            return;
        }

        $.ajax({
            type: "POST",
            url: "${CONTEXT_PATH}/toy/admin/sys/auth/role/disable.ac",
            data: { authUuid: authUuid },
            beforeSend: function(xhr) {
                // Your custom logic only (loading spinner etc.)
            },
            dataType: "json",
            success: function(res) {
                if (res && res.redirectUrl) { window.location.href = res.redirectUrl; return; }

                if (res.result === "Y") {
                    alert("<spring:message code='admin.system.auth.list.alert.disabledRole' javaScriptEscape='true' />");
                    fn_setAuthRoleJsGrid();

                    // Refresh right grid if the current selected role was disabled.
                    if (publicAuthUuid && publicAuthUuid === authUuid) {
                        fn_assignedMngrRefresh();
                    }
                    return;
                }

                if (res.result === "Forbidden") {
                    alert(res.errorMessage || "<spring:message code='admin.system.auth.list.alert.defaultRoleCannotDisable' javaScriptEscape='true' />");
                    return;
                }

                if (res.result === "None") {
                    alert(res.errorMessage || "<spring:message code='admin.system.auth.list.alert.roleNotFound' javaScriptEscape='true' />");
                    fn_setAuthRoleJsGrid();
                    return;
                }

                if (res.result === "Invalid") {
                    alert(res.errorMessage || "<spring:message code='admin.common.invalidData' javaScriptEscape='true' />");
                    return;
                }

                alert(res.errorMessage || "<spring:message code='admin.system.auth.list.alert.disableFailed' javaScriptEscape='true' />");
            },
            error: function(request, status, error) {
                fn_AjaxError(request, status, error, "${CONTEXT_PATH}/toy/admin/login.do");
            }
        });
    }

    function fn_setAssignedMngrJsGrid(authUuid) {
        authUuid = normalizeAuthUuid(authUuid);
        if (!authUuid) {
            $("#assignedBox").hide();
            $("#assignedGrid").empty();
            return;
        }

        $("#assignedBox").show();
        $("#selectedAuthUuid").text(authUuid);

        $("#assignedGrid").jsGrid({
            width: "100%",
            height: "630px",

            filtering: false,
            autoload: true,
            sorting: false,
            selecting: false,

            paging: true,
            pageLoading: true,
            pageSize: 10,
            pageButtonCount: 10,
            pagerContainer: "#externalPagerRight",

            controller: {
                loadData: function(filter) {
                    var d = $.Deferred();
                    $.ajax({
                        type: "POST",
                        url: "${CONTEXT_PATH}/toy/admin/sys/auth/mngr/assigned/list.doax?authUuid=" + encodeURIComponent(authUuid),
                        data: filter,
                        beforeSend: function(xhr) {
                            // Your custom logic only (loading spinner etc.)
                        },
                        dataType: "json"
                    }).done(function(res) {
                        if (res && res.redirectUrl) {
                            window.location.href = res.redirectUrl;
                            return;
                        }

                        if (!res || !res.data || res.data.length === 0) {
                            $("#assignedGrid").jsGrid({
                                autoload: false,
                                noDataContent: '<div class="not-content"><span class="icon"></span><div class="text"><spring:message code="admin.common.grid.noResults" /></div></div>'
                            });
                        }

                        $("#assignedCount").text(res.itemsCount || 0);
                        d.resolve(res);
                    }).fail(function(xhr) {
                        console.error("assigned/list.doax failed", xhr.status, xhr.responseText);
                        alert("<spring:message code='admin.system.auth.list.alert.loadAssignedManagersFailed' javaScriptEscape='true' />");
                        d.resolve({ data: [], itemsCount: 0 });
                    });

                    return d.promise();
                }
            },

            fields: [
                { name: "mngrUid", title: "<spring:message code='admin.system.mngr.common.field.id' javaScriptEscape='true' />", type: "text", width: 110, align: "center" },
                { name: "mngrNm", title: "<spring:message code='admin.system.mngr.common.field.name' javaScriptEscape='true' />", type: "text", width: 120, align: "left" },
                { name: "emlAdres", title: "<spring:message code='admin.system.mngr.common.field.email' javaScriptEscape='true' />", type: "text", width: 170, align: "left" },
                { name: "telno", title: "<spring:message code='admin.system.mngr.common.field.phone' javaScriptEscape='true' />", type: "text", width: 120, align: "center" },
                { name: "useYn", title: "<spring:message code='admin.system.mngr.common.field.useYn' javaScriptEscape='true' />", type: "checkbox", width: 60, align: "center", sorting: false, filtering: false, inserting: false },

                { type: "control", width: 110,
                    itemTemplate: function(_, item) {
                        var delBtn = $("<a>").addClass("btn orange sm")
                            .append("<i class=\"material-icons-outlined\">delete</i>")
                            .append(" <spring:message code='admin.system.auth.list.button.unassign' javaScriptEscape='true' />")
                            .click(function(e) {
                                fn_unassignMngr(authUuid, item.mngrUid);
                                e.stopPropagation();
                            });

                        return $("<div>").append(delBtn);
                    }
                }
            ]
        });
    }

    function fn_unassignMngr(authUuid, mngrUid) {
        if (!authUuid || !mngrUid) {
            alert("<spring:message code='admin.system.auth.list.alert.requiredParam' javaScriptEscape='true' />");
            return;
        }
        if (!confirm("<spring:message code='admin.system.auth.list.confirm.unassignManager' javaScriptEscape='true' />")) {
            return;
        }

        $.ajax({
            type: "POST",
            url: "${CONTEXT_PATH}/toy/admin/sys/auth/mngr/unassign.ac",
            data: { authUuid: authUuid, mngrUid: mngrUid },
            beforeSend: function(xhr) {
                // Your custom logic only (loading spinner etc.)
            },
            dataType: "json",
            success: function(res) {
                if (res && res.redirectUrl) { window.location.href = res.redirectUrl; return; }

                if (res.result === "Y") {
                    alert("<spring:message code='admin.system.auth.list.alert.unassigned' javaScriptEscape='true' />");
                    fn_assignedMngrRefresh();
                    return;
                }
                if (res.result === "None") {
                    alert(res.errorMessage || "<spring:message code='admin.system.auth.list.alert.assignmentNotFound' javaScriptEscape='true' />");
                    fn_assignedMngrRefresh();
                    return;
                }
                if (res.result === "Invalid") {
                    alert(res.errorMessage || "<spring:message code='admin.common.invalidData' javaScriptEscape='true' />");
                    return;
                }
                alert(res.errorMessage || "<spring:message code='admin.system.auth.list.alert.unassignFailed' javaScriptEscape='true' />");
            },
            error: function(request, status, error) {
                fn_AjaxError(request, status, error, "${CONTEXT_PATH}/toy/admin/login.do");
            }
        });
    }

    function fn_openAssignMngrPopup() {
        if (!publicAuthUuid) {
            alert("<spring:message code='admin.system.auth.list.alert.selectRoleFirst' javaScriptEscape='true' />");
            return;
        }
        var url = "${CONTEXT_PATH}/toy/admin/sys/auth/mngr/assignPop.do?authUuid=" + encodeURIComponent(publicAuthUuid);
        window.open(url, "listPopAssignMngr", "resizable=yes,scrollbars=yes,width=1000,height=660");
    }

    // Called by popup after successful assignment.
    function fn_assignedMngrRefresh() {
        if ($("#assignedGrid").data("JSGrid")) {
            $("#assignedGrid").jsGrid("loadData");
        }
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
                <h2 class="depth-title"><spring:message code="admin.system.auth.list.title" /></h2>
            </div>

            <section class="content-box">
                <table id="content_table">
                    <tr style="vertical-align: top;">
                        <td>
                            <h3 class="title2">
                                <spring:message code="admin.common.searchResult" /><small>[<spring:message code="admin.common.total" /> <strong id="roleCount" class="text-blue">0</strong><spring:message code="admin.common.countUnit" />]</small>
                            </h3>

                            <div id="authRoleGrid"></div>

                            <div class="paging-group">
                                <div id="externalPagerLeft" style="text-align:center;"></div>
                            </div>
                        </td>

                        <td style="padding-left: 20px;">
                            <span id="assignedBox" style="display: none;">
                                <h3 class="title2">
                                    <spring:message code="admin.system.auth.list.selectedRole" />: <strong id="selectedAuthUuid"></strong>
                                    <small>[<spring:message code="admin.common.total" /> <strong id="assignedCount" class="text-blue">0</strong><spring:message code="admin.common.countUnit" />]</small>

                                    <a class="btn blue ml-0" href="javascript:fn_openAssignMngrPopup();">
                                        <i class="material-icons-outlined">add</i> <spring:message code="admin.system.auth.list.button.addManager" />
                                    </a>
                                </h3>

                                <div id="assignedGrid"></div>

                                <div class="paging-group">
                                    <div id="externalPagerRight" style="text-align:center;"></div>
                                </div>
                            </span>
                        </td>
                    </tr>
                </table>
            </section>
        </section>
    </main>
</div>
</body>
</html>
