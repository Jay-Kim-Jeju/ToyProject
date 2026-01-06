<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2026-01-02
  Time: 오후 4:01
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
    $(function () {
        fn_setAuthRoleJsGrid();
    });

    // Default roles must not be disabled to prevent breaking global auth behavior.
    var ROLE_ADMIN = "ADMINISTRATOR";

    // Selected authUuid (used for right grid + popup)
    var publicAuthUuid = "";

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
                                noDataContent: '<div class="not-content"><span class="icon"></span><div class="text">검색 결과가 없습니다.</div></div>'
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
                        alert("Failed to load auth roles. Please check server logs.");
                        d.resolve({ data: [], itemsCount: 0 });
                    });

                    return d.promise();
                },

                insertItem: function(data) {
                    if (!data.authUuid || !data.authorDc) {
                        alert("권한 아이디와 권한 설명은 필수입력값입니다.");
                        return;
                    }
                    if (!confirm("권한을 등록하시겠습니까?")) {
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
                            if (res.result === "Y") { alert("정상적으로 등록되었습니다."); fn_setAuthRoleJsGrid(); return; }
                            if (res.result === "Duple") { alert(res.errorMessage || "Duplicate role."); return; }
                            if (res.result === "Invalid") { alert(res.errorMessage || "Invalid data."); return; }
                            alert(res.errorMessage || "Insert failed.");
                        },
                        error: function(request, status, error) {
                            fn_AjaxError(request, status, error, "${CONTEXT_PATH}/toy/admin/login.do");
                        }
                    });
                },

                updateItem: function(data) {
                    if (!data.authUuid || !data.authorDc) {
                        alert("권한 아이디와 권한 설명은 필수입력값입니다.");
                        return;
                    }
                    if (!confirm("정말로 수정하시겠습니까?")) {
                        return;
                    }

                    var payload = {
                        authUuid: data.authUuid,
                        authorDc: data.authorDc,
                        // Always send Y/N (jsGrid checkbox can be boolean true/false).
                        // Administrator role must stay enabled.
                        useYn: (data && data.authUuid === ROLE_ADMIN) ? "Y" : (data.useYn ? "Y" : "N")
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
                            if (res.result === "Y") { alert("정상적으로 수정되었습니다."); fn_setAuthRoleJsGrid(); return; }
                            if (res.result === "None") { alert(res.errorMessage || "Role does not exist."); fn_setAuthRoleJsGrid(); return; }
                            if (res.result === "Invalid") { alert(res.errorMessage || "Invalid data."); return; }
                            alert(res.errorMessage || "Update failed.");
                        },
                        error: function(request, status, error) {
                            fn_AjaxError(request, status, error, "${CONTEXT_PATH}/toy/admin/login.do");
                        }
                    });
                }
            },

            rowClick: function(args) {
                publicAuthUuid = args.item.authUuid || "";
                fn_setAssignedMngrJsGrid(publicAuthUuid);

                $(".jsgrid-pick-row").removeClass("jsgrid-pick-row");
                $(".jsgrid-selected-row").addClass("jsgrid-pick-row");
            },

            fields: [
                { name: "authUuid", title: "권한 아이디", type: "text", width: 120, align: "left", editing: false },
                { name: "authorDc", title: "권한 설명", type: "text", width: 160, align: "left", sorting: false },
                { name: "useYn", title: "사용여부", type: "checkbox", width: 60, align: "center", inserting: false,
                    editTemplate: function(value, item) {
                        // Render checkbox and disable it for default roles.
                        var $cb = $("<input>").attr("type", "checkbox").prop("checked", value === true || value === "Y");
                        if (item && item.authUuid === ROLE_ADMIN) {
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
                            .append(" 검 색")
                            .click(function(e) {
                                $("#authRoleGrid").jsGrid("search");
                                e.stopPropagation();
                            });

                        var clearBtn = $("<a>").addClass("btn gray sm")
                            .append("<i class=\"material-icons-outlined\">clear</i>")
                            .append(" 초기화")
                            .click(function(e) {
                                $("#authRoleGrid").jsGrid("clearFilter");
                                e.stopPropagation();
                            });

                        return $("<div>").append(filterBtn).append("&nbsp;").append(clearBtn);
                    },

                    insertTemplate: function(_, item) {
                        var insertBtn = $("<a>").addClass("btn blue sm")
                            .append("<i class=\"material-icons-outlined\">add</i>")
                            .append(" 등 록")
                            .click(function(e) {
                                $("#authRoleGrid").jsGrid("insertItem", item);
                                e.stopPropagation();
                            });

                        var clearBtn = $("<a>").addClass("btn gray sm")
                            .append("<i class=\"material-icons-outlined\">clear</i>")
                            .append(" 취 소")
                            .click(function(e) {
                                $("#authRoleGrid").jsGrid("clearInsert");
                                e.stopPropagation();
                            });

                        return $("<div>").append(insertBtn).append("&nbsp;").append(clearBtn);
                    },

                    editTemplate: function(_, item) {
                        var saveBtn = $("<a>").addClass("btn blue sm")
                            .append("<i class=\"material-icons-outlined\">check</i>")
                            .append(" 저 장")
                            .click(function(e) {
                                $("#authRoleGrid").jsGrid("updateItem");
                                e.stopPropagation();
                            });

                        var cancelBtn = $("<a>").addClass("btn gray sm")
                            .append("<i class=\"material-icons-outlined\">clear</i>")
                            .append(" 취 소")
                            .click(function(e) {
                                $("#authRoleGrid").jsGrid("cancelEdit");
                                e.stopPropagation();
                            });

                        return $("<div>").append(saveBtn).append("&nbsp;").append(cancelBtn);
                    },

                    itemTemplate: function(_, item) {
                        var editBtn = $("<a>").addClass("btn black sm")
                            .append("<i class=\"material-icons-outlined\">edit</i>")
                            .append(" 수 정")
                            .click(function(e) {
                                $("#authRoleGrid").jsGrid("editItem", item);
                                e.stopPropagation();
                            });

                        // Disable action for administrator role (UI guard).
                        var disableBtn;

                        var isAdminRole = (item && item.authUuid === ROLE_ADMIN);
                        if (isAdminRole) {
                            disableBtn = $("<a>").addClass("btn gray sm")
                                .append("<i class=\"material-icons-outlined\">lock</i>")
                                .append(" 비활성화")
                                .click(function(e) {
                                    alert("기본 권한은 비활성화할 수 없습니다.");
                                    e.stopPropagation();
                                });
                        } else {
                            disableBtn = $("<a>").addClass("btn orange sm")
                                .append("<i class=\"material-icons-outlined\">delete</i>")
                                .append(" 비활성화")
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
        if (!authUuid) {
            alert("권한 아이디가 없습니다.");
            return;
        }

        // Client-side guard (server also blocks in controller).
        if (authUuid === ROLE_ADMIN) {
            alert("기본 권한은 비활성화할 수 없습니다.");
            return;
        }

        if (!confirm("해당 권한을 비활성화하시겠습니까?")) {
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
                    alert("비활성화 처리되었습니다.");
                    fn_setAuthRoleJsGrid();

                    // Refresh right grid if the current selected role was disabled.
                    if (publicAuthUuid && publicAuthUuid === authUuid) {
                        fn_assignedMngrRefresh();
                    }
                    return;
                }

                if (res.result === "Forbidden") {
                    alert(res.errorMessage || "Default roles cannot be disabled.");
                    return;
                }

                if (res.result === "None") {
                    alert(res.errorMessage || "Role does not exist.");
                    fn_setAuthRoleJsGrid();
                    return;
                }

                if (res.result === "Invalid") {
                    alert(res.errorMessage || "Invalid data.");
                    return;
                }

                alert(res.errorMessage || "Disable failed.");
            },
            error: function(request, status, error) {
                fn_AjaxError(request, status, error, "${CONTEXT_PATH}/toy/admin/login.do");
            }
        });
    }

    function fn_setAssignedMngrJsGrid(authUuid) {
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
                                noDataContent: '<div class="not-content"><span class="icon"></span><div class="text">검색 결과가 없습니다.</div></div>'
                            });
                        }

                        $("#assignedCount").text(res.itemsCount || 0);
                        d.resolve(res);
                    }).fail(function(xhr) {
                        console.error("assigned/list.doax failed", xhr.status, xhr.responseText);
                        alert("Failed to load assigned managers. Please check server logs.");
                        d.resolve({ data: [], itemsCount: 0 });
                    });

                    return d.promise();
                }
            },

            fields: [
                { name: "mngrUid", title: "아이디", type: "text", width: 110, align: "center" },
                { name: "mngrNm", title: "이름", type: "text", width: 120, align: "left" },
                { name: "emlAdres", title: "이메일", type: "text", width: 170, align: "left" },
                { name: "telno", title: "전화번호", type: "text", width: 120, align: "center" },
                { name: "useYn", title: "사용여부", type: "checkbox", width: 60, align: "center", sorting: false, filtering: false, inserting: false },

                { type: "control", width: 110,
                    itemTemplate: function(_, item) {
                        var delBtn = $("<a>").addClass("btn orange sm")
                            .append("<i class=\"material-icons-outlined\">delete</i>")
                            .append(" 해제")
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
            alert("필수 파라미터가 없습니다.");
            return;
        }
        if (!confirm("해당 관리자의 권한을 해제하시겠습니까?")) {
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
                    alert("정상적으로 해제되었습니다.");
                    fn_assignedMngrRefresh();
                    return;
                }
                if (res.result === "None") {
                    alert(res.errorMessage || "Assignment does not exist.");
                    fn_assignedMngrRefresh();
                    return;
                }
                if (res.result === "Invalid") {
                    alert(res.errorMessage || "Invalid data.");
                    return;
                }
                alert(res.errorMessage || "Unassign failed.");
            },
            error: function(request, status, error) {
                fn_AjaxError(request, status, error, "${CONTEXT_PATH}/toy/admin/login.do");
            }
        });
    }

    function fn_openAssignMngrPopup() {
        if (!publicAuthUuid) {
            alert("권한을 먼저 선택해주세요.");
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
                <h2 class="depth-title">권한 관리</h2>
            </div>

            <section class="content-box">
                <table id="content_table">
                    <tr style="vertical-align: top;">
                        <td>
                            <h3 class="title2">
                                검색결과 <small>[총 <strong id="roleCount" class="text-blue">0</strong>건]</small>
                            </h3>

                            <div id="authRoleGrid"></div>

                            <div class="paging-group">
                                <div id="externalPagerLeft" style="text-align:center;"></div>
                            </div>
                        </td>

                        <td style="padding-left: 20px;">
                            <span id="assignedBox" style="display: none;">
                                <h3 class="title2">
                                    권한: <strong id="selectedAuthUuid"></strong>
                                    <small>[총 <strong id="assignedCount" class="text-blue">0</strong>건]</small>

                                    <a class="btn blue ml-0" href="javascript:fn_openAssignMngrPopup();">
                                        <i class="material-icons-outlined">add</i> 관리자 추가
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
