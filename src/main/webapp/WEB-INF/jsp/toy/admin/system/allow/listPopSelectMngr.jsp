<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>

<!-- jsGrid -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid-theme.css" />
<script src="${pageContext.request.contextPath}/js/com/jsgrid/jsgrid.js"></script>
<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>

<script type="text/javascript">
    var selectedManager = null;
    var I18N_ALLOW_SELECT_MNGR = {
        noPermission: "<spring:message code='admin.system.allow.selectMngr.alert.noPermission' javaScriptEscape='true' />",
        loadFailed: "<spring:message code='admin.system.allow.selectMngr.alert.loadFailed' javaScriptEscape='true' />",
        selectRequired: "<spring:message code='admin.system.allow.selectMngr.alert.selectRequired' javaScriptEscape='true' />"
    };

    $(function () {
        fn_setSelectMngrJsGrid();
    });

    function fn_setSelectMngrJsGrid() {
        $("#jsGrid").jsGrid({
            width: "100%",
            height: "auto",
            filtering: true,
            autoload: true,
            sorting: false,
            paging: true,
            pageLoading: true,
            pageSize: 10,
            pageButtonCount: 10,
            pagerContainer: "#externalPager",

            rowClick: function(args) {
                selectedManager = args.item || null;
                $(".jsgrid-pick-row").removeClass("jsgrid-pick-row");
                $(args.event.target).closest("tr").addClass("jsgrid-pick-row");
            },

            controller: {
                loadData: function(filter) {
                    var d = $.Deferred();
                    selectedManager = null;

                    $.ajax({
                        type: "POST",
                        url: "${CONTEXT_PATH}/toy/admin/sys/allow/mngr/select/list.doax",
                        data: filter,
                        dataType: "json"
                    }).done(function(res) {
                        if (res && res.redirectUrl) {
                            alert(res.errorMessage || I18N_ALLOW_SELECT_MNGR.noPermission);
                            if (window.opener) {
                                window.opener.location.href = res.redirectUrl;
                            }
                            window.close();
                            return;
                        }

                        $("#mngrCount").text(res.itemsCount || 0);
                        d.resolve(res || { data: [], itemsCount: 0 });
                    }).fail(function(xhr) {
                        console.error("allow/mngr/select/list.doax failed", xhr.status, xhr.responseText);
                        alert(I18N_ALLOW_SELECT_MNGR.loadFailed);
                        d.resolve({ data: [], itemsCount: 0 });
                    });

                    return d.promise();
                }
            },

            fields: [
                { name: "mngrUid", title: "<spring:message code='admin.system.mngr.common.field.id' javaScriptEscape='true' />", type: "text", width: 120, align: "center" },
                { name: "mngrNm", title: "<spring:message code='admin.system.mngr.common.field.name' javaScriptEscape='true' />", type: "text", width: 120, align: "left" },
                { name: "emlAdres", title: "<spring:message code='admin.system.mngr.common.field.email' javaScriptEscape='true' />", type: "text", width: 180, align: "left", filtering: false },
                { name: "telno", title: "<spring:message code='admin.system.mngr.common.field.phone' javaScriptEscape='true' />", type: "text", width: 120, align: "center", filtering: false },
                { name: "useYn", title: "<spring:message code='admin.system.mngr.common.field.useYn' javaScriptEscape='true' />", type: "checkbox", width: 60, align: "center", filtering: false }
            ]
        });
    }

    function fn_selectManager() {
        if (!selectedManager || !selectedManager.mngrUid) {
            alert(I18N_ALLOW_SELECT_MNGR.selectRequired);
            return;
        }

        if (window.opener && typeof window.opener.fn_receiveSelectedAllowMngr === "function") {
            window.opener.fn_receiveSelectedAllowMngr(selectedManager.mngrUid, selectedManager.mngrNm || "");
        }
        window.close();
    }
</script>
</head>
<body>
<div class="win-popup">
    <div class="flex justify baseline title2-area">
        <h3 class="title2"><spring:message code="admin.system.allow.selectMngr.title" /></h3>
        <div class="title-justify">
            <h3 class="title2"><spring:message code="admin.common.searchResult" /><small>[<spring:message code="admin.common.total" /> <strong id="mngrCount" class="text-blue">0</strong><spring:message code="admin.common.countUnit" />]</small></h3>
        </div>
    </div>

    <div class="text-gray mb-10"><spring:message code="admin.system.allow.selectMngr.help" /></div>

    <div id="jsGrid"></div>
    <div id="externalPager" style="margin: 10px 0; font-size: 14px; color: #262626; font-weight: 300;"></div>

    <div class="btn-right st1 mb-0">
        <a class="btn blue" href="javascript:fn_selectManager();">
            <i class="material-icons-outlined">check</i> <spring:message code="admin.common.button.select" />
        </a>
        <a class="btn gray" href="javascript:window.close();">
            <i class="material-icons-outlined">close</i> <spring:message code="admin.common.button.close" />
        </a>
    </div>
</div>
</body>
</html>
