<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>

<!-- jsGrid -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid-theme.css" />
<script src="${pageContext.request.contextPath}/js/com/jsgrid/jsgrid.js"></script>
<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>

<script type="text/javascript">
    var selectedManager = null;

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
                            alert(res.errorMessage || "권한이 없습니다.");
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
                        alert("관리자 목록 조회에 실패했습니다.");
                        d.resolve({ data: [], itemsCount: 0 });
                    });

                    return d.promise();
                }
            },

            fields: [
                { name: "mngrUid", title: "관리자ID", type: "text", width: 120, align: "center" },
                { name: "mngrNm", title: "관리자명", type: "text", width: 120, align: "left" },
                { name: "emlAdres", title: "이메일", type: "text", width: 180, align: "left", filtering: false },
                { name: "telno", title: "전화번호", type: "text", width: 120, align: "center", filtering: false },
                { name: "useYn", title: "사용", type: "checkbox", width: 60, align: "center", filtering: false }
            ]
        });
    }

    function fn_selectManager() {
        if (!selectedManager || !selectedManager.mngrUid) {
            alert("관리자를 선택해주세요.");
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
        <h3 class="title2">허용 대상 관리자 선택</h3>
        <div class="title-justify">
            <h3 class="title2">검색결과 <small>[총 <strong id="mngrCount" class="text-blue">0</strong>건]</small></h3>
        </div>
    </div>

    <div class="text-gray mb-10">활성(Y) 관리자만 조회됩니다. 행을 클릭한 뒤 선택 버튼을 눌러주세요.</div>

    <div id="jsGrid"></div>
    <div id="externalPager" style="margin: 10px 0; font-size: 14px; color: #262626; font-weight: 300;"></div>

    <div class="btn-right st1 mb-0">
        <a class="btn blue" href="javascript:fn_selectManager();">
            <i class="material-icons-outlined">check</i> 선택
        </a>
        <a class="btn gray" href="javascript:window.close();">
            <i class="material-icons-outlined">close</i> 닫기
        </a>
    </div>
</div>
</body>
</html>
