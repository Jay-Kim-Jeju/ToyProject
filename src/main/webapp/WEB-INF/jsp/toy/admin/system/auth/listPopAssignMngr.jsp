<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2026-01-02
  Time: 오후 4:04
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
        fn_setUnassignedMngrJsGrid();
    });

    // Store selected manager IDs only (service expects List<String>).
    var selectedMngrUids = [];

    function selectItem(item) {
        if (!item || !item.mngrUid) {
            return;
        }
        selectedMngrUids.push(item.mngrUid);
    }

    function unselectItem(item) {
        if (!item || !item.mngrUid) {
            return;
        }
        selectedMngrUids = $.grep(selectedMngrUids, function(v) {
            return v !== item.mngrUid;
        });
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

                    // Reset selection on each load to avoid stale selection across pages.
                    selectedMngrUids = [];

                    $.ajax({
                        type: "POST",
                        url: "${CONTEXT_PATH}/toy/admin/sys/auth/mngr/unassigned/list.doax?authUuid=${param.authUuid}",
                        data: filter,
                        dataType: "json",
                        beforeSend: function (xhr) {
                            // Your custom logic only (loading spinner etc.)
                        }
                    }).done(function(res) {
                        if (res && res.redirectUrl) {
                            alert(res.errorMessage || "Authentication required.");
                            if (window.opener) { window.opener.location.href = res.redirectUrl; }
                            window.close();
                            return;
                        }

                        if (!res || !res.data || res.data.length === 0) {
                            $("#jsGrid").jsGrid({
                                autoload: false,
                                noDataContent: '<div class="not-content"><span class="icon"></span><div class="text">검색 결과가 없습니다.</div></div>'
                            });

                            // Restore filter inputs for better UX.
                            $('.jsgrid-filter-row').find('td').eq(1).find('input').val(filter.mngrUid || "");
                            $('.jsgrid-filter-row').find('td').eq(2).find('input').val(filter.mngrNm || "");
                        }

                        $("#unAuthMngrCount").text(res.itemsCount || 0);
                        d.resolve(res);
                    }).fail(function(xhr) {
                        console.error("unassigned/list.doax failed", xhr.status, xhr.responseText);
                        alert("Failed to load managers. Please check server logs.");
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
                { name: "mngrUid", title: "아이디", type: "text", width: 110, align: "center" },
                { name: "mngrNm", title: "이름", type: "text", width: 120, align: "left" },
                { name: "emlAdres", title: "이메일", type: "text", width: 170, align: "left", filtering: false },
                { name: "telno", title: "전화번호", type: "text", width: 120, align: "center", filtering: false },
                { name: "useYn", title: "사용여부", type: "checkbox", width: 70, align: "center", filtering: false }
            ]
        });
    }

    function fn_assignSelectedManagers() {
        if (!selectedMngrUids || selectedMngrUids.length === 0) {
            alert("선택된 관리자가 없습니다.");
            return;
        }
        if (!confirm("선택한 관리자를 권한에 추가하시겠습니까?")) {
            return;
        }

        $.ajax({
            type: "POST",
            url: "${CONTEXT_PATH}/toy/admin/sys/auth/mngr/assign.ac?authUuid=${param.authUuid}",
            data: JSON.stringify(selectedMngrUids),
            contentType: "application/json; charset=UTF-8",
            dataType: "json",
            beforeSend: function(xhr) {
                // Your custom logic only (loading spinner etc.)
            },
            success: function(res) {
                if (res && res.redirectUrl) {
                    alert(res.errorMessage || "Authentication required.");
                    if (window.opener) { window.opener.location.href = res.redirectUrl; }
                    window.close();
                    return;
                }

                if (res.result === "Y") {
                    // errorMessage contains a friendly batch summary message.
                    alert(res.errorMessage || "정상적으로 추가되었습니다.");

                    if (window.opener && window.opener.fn_assignedMngrRefresh) {
                        window.opener.fn_assignedMngrRefresh();
                    } else if (window.opener) {
                        window.opener.location.reload();
                    }
                    window.close();
                    return;
                }

                if (res.result === "Invalid") {
                    alert(res.errorMessage || "Invalid data.");
                    return;
                }

                alert(res.errorMessage || "Assign failed.");
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
        <h3 class="title2">관리자 추가</h3>
        <div class="title-justify">
            <h3 class="title2">검색결과 <small>[총 <strong id="unAuthMngrCount" class="text-blue">0</strong>건]</small></h3>
        </div>
    </div>

    <div id="jsGrid"></div>
    <div id="externalPager" style="margin: 10px 0; font-size: 14px; color: #262626; font-weight: 300;"></div>

    <div class="btn-right st1 mb-0">
        <a class="btn blue" href="javascript:fn_assignSelectedManagers();"><i class="material-icons-outlined">add</i> 등 록</a>
        <a class="btn gray" href="javascript:window.close();"><i class="material-icons-outlined">close</i> 닫 기</a>
    </div>
</div>
</body>
</html>