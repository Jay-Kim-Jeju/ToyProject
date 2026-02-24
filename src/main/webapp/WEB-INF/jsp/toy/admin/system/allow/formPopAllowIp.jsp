<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>
<script src="${pageContext.request.contextPath}/js/admin/system.js?jsCssVer=${jsCssVer}" ></script>

<c:set var="isUpdateMode" value="${mode eq 'U'}" />

<script type="text/javascript">
    var IS_UPDATE_MODE = ("${isUpdateMode}" === "true");

    $(function () {
        fn_initAllowIpForm();
    });

    function fn_initAllowIpForm() {
        // DB datetime string -> <input type="date"> expected format (yyyy-MM-dd)
        $("#startDt").val(fn_toDateInputValue($("#startDt").data("raw")));
        $("#endDt").val(fn_toDateInputValue($("#endDt").data("raw")));
    }

    function fn_toDateInputValue(v) {
        if (!v) return "";
        v = String(v).trim();
        return (v.length >= 10) ? v.substring(0, 10) : v;
    }

    function fn_openSelectManagerPopup() {
        var url = "${CONTEXT_PATH}/toy/admin/sys/allow/mngr/selectPop.do";
        window.open(url, "listPopSelectAllowMngr", "resizable=yes,scrollbars=yes,width=980,height=660");
    }

    // Called by manager selection popup (single-select).
    function fn_receiveSelectedAllowMngr(mngrUid, mngrNm) {
        $("#mngrUid").val(mngrUid || "");
        $("#mngrNmView").val(mngrNm || "");
    }

    function fn_validateAllowIpForm() {
        if (!$.trim($("#mngrUid").val())) {
            alert("허용 대상 관리자ID를 선택해주세요.");
            return false;
        }

        if (!IS_UPDATE_MODE && !$.trim($("#allowIp").val())) {
            alert("접속허용IP를 입력해주세요.");
            return false;
        }

        return true;
    }

    function fn_saveAllowIp() {
        if (!fn_validateAllowIpForm()) {
            return;
        }

        var url = IS_UPDATE_MODE
            ? "${CONTEXT_PATH}/toy/admin/sys/allow/update.ac"
            : "${CONTEXT_PATH}/toy/admin/sys/allow/insert.ac";

        var payload = {
            allowIpUuid: $("#allowIpUuid").val(),
            mngrUid: $("#mngrUid").val(),
            allowIp: $("#allowIp").val(),
            cidrPrefix: $("#cidrPrefix").val(),
            useYn: $("#useYn").is(":checked") ? "Y" : "N",
            memo: $("#memo").val(),
            startDt: $("#startDt").val(),
            endDt: $("#endDt").val()
        };

        // Empty CIDR should be treated as null-ish on server side.
        if (!$.trim(payload.cidrPrefix)) {
            payload.cidrPrefix = "";
        }

        if (!confirm(IS_UPDATE_MODE ? "수정하시겠습니까?" : "등록하시겠습니까?")) {
            return;
        }

        $.ajax({
            type: "POST",
            url: url,
            data: payload,
            dataType: "json",
            success: function(res) {
                if (res && res.redirectUrl) {
                    if (window.opener) {
                        window.opener.location.href = res.redirectUrl;
                    } else {
                        window.location.href = res.redirectUrl;
                    }
                    window.close();
                    return;
                }

                if (res.result === "Y") {
                    alert(IS_UPDATE_MODE ? "정상적으로 수정되었습니다." : "정상적으로 등록되었습니다.");
                    if (window.opener && window.opener.fn_allowIpGridRefresh) {
                        window.opener.fn_allowIpGridRefresh();
                    }
                    window.close();
                    return;
                }

                if (res.result === "Duple") {
                    alert(res.errorMessage || "중복된 허용IP 매핑입니다.");
                    return;
                }

                if (res.result === "Invalid") {
                    alert(res.errorMessage || "잘못된 입력값입니다.");
                    return;
                }

                if (res.result === "Forbidden") {
                    alert(res.errorMessage || "삭제된 이력은 재활성화할 수 없습니다. 새로 등록해주세요.");
                    return;
                }

                if (res.result === "None") {
                    alert(res.errorMessage || "대상이 존재하지 않습니다.");
                    if (window.opener && window.opener.fn_allowIpGridRefresh) {
                        window.opener.fn_allowIpGridRefresh();
                    }
                    return;
                }

                alert(res.errorMessage || "저장에 실패했습니다.");
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
        <h3 class="title2">
            <c:choose>
                <c:when test="${isUpdateMode}">접속허용IP 수정</c:when>
                <c:otherwise>접속허용IP 등록</c:otherwise>
            </c:choose>
        </h3>
        <p class="top-caption">
            <i class="required"></i>
            <strong class="txt">Required</strong>
        </p>
    </div>

    <form id="allowIpForm" name="allowIpForm" method="post">
        <input type="hidden" id="allowIpUuid" name="allowIpUuid" value="<c:out value='${detail.allowIpUuid}'/>" />

        <table class="row">
            <colgroup>
                <col style="width: 24%">
                <col style="width: 76%">
            </colgroup>
            <tbody>
            <tr>
                <th><div class="tit required" data-field="mngrUid">허용 대상 관리자ID</div></th>
                <td>
                    <div style="display:flex; gap:8px; align-items:center;">
                        <input type="text" id="mngrUid" name="mngrUid" class="wp100" style="max-width:280px;" readonly
                               value="<c:out value='${detail.mngrUid}'/>" />
                        <a class="btn sm" href="javascript:fn_openSelectManagerPopup();">
                            <i class="material-icons-outlined">search</i> 관리자 선택
                        </a>
                    </div>
                    <div style="margin-top:6px;">
                        <input type="text" id="mngrNmView" class="wp100" style="max-width:280px;" readonly
                               placeholder="선택한 관리자명 (옵션 표시)" />
                    </div>
                </td>
            </tr>

            <tr>
                <th><div class="tit required" data-field="allowIp">접속허용IP</div></th>
                <td>
                    <input type="text" id="allowIp" name="allowIp" class="wp100" style="max-width:420px;"
                           <c:if test="${isUpdateMode}">readonly</c:if>
                           value="<c:out value='${detail.allowIp}'/>"
                           placeholder="예) 192.168.0.10 또는 10.0.0.0" />
                    <c:if test="${isUpdateMode}">
                        <p class="help">IP 변경은 보안 이력 관리를 위해 지원하지 않습니다. 삭제 후 재등록하세요.</p>
                    </c:if>
                </td>
            </tr>

            <tr>
                <th><div class="tit">CIDR Prefix</div></th>
                <td>
                    <input type="number" id="cidrPrefix" name="cidrPrefix" class="wp100" style="max-width:180px;"
                           min="0" max="128"
                           <c:if test="${isUpdateMode}">readonly</c:if>
                           value="<c:out value='${detail.cidrPrefix}'/>"
                           placeholder="32 (단일IP), 24 (대역)" />
                    <p class="help">IPv4 단일IP는 보통 32, IPv6 단일IP는 128입니다. (대역허용 로직은 추후 적용 예정)</p>
                </td>
            </tr>

            <tr>
                <th><div class="tit">사용여부</div></th>
                <td>
                    <label style="display:inline-flex; align-items:center; gap:6px;">
                        <input type="checkbox" id="useYn" name="useYn"
                               <c:if test="${empty detail.useYn or detail.useYn eq 'Y'}">checked="checked"</c:if> />
                        사용
                    </label>
                </td>
            </tr>

            <tr>
                <th><div class="tit">유효 시작일</div></th>
                <td>
                    <div style="display:flex; gap:10px; align-items:center; flex-wrap:wrap;">
                        <input type="date" id="startDt" name="startDt" class="wp100" style="max-width:220px;"
                               data-raw="<c:out value='${detail.startDt}'/>" />
                        <span class="help" style="margin:0;">비우면 즉시 적용</span>
                    </div>
                </td>
            </tr>

            <tr>
                <th><div class="tit">유효 종료일</div></th>
                <td>
                    <div style="display:flex; gap:10px; align-items:center; flex-wrap:wrap;">
                        <input type="date" id="endDt" name="endDt" class="wp100" style="max-width:220px;"
                               data-raw="<c:out value='${detail.endDt}'/>" />
                        <span class="help" style="margin:0;">비우면 종료일 없음</span>
                    </div>
                </td>
            </tr>

            <tr>
                <th><div class="tit">메모</div></th>
                <td>
                    <textarea id="memo" name="memo" class="wp100" rows="3" style="resize:vertical; min-height:90px;"><c:out value="${detail.memo}"/></textarea>
                </td>
            </tr>
            </tbody>
        </table>
    </form>

    <div class="btn-right st1 mb-0">
        <a class="btn blue" href="javascript:fn_saveAllowIp();">
            <i class="material-icons-outlined">save</i>
            <c:choose>
                <c:when test="${isUpdateMode}">저장</c:when>
                <c:otherwise>등록</c:otherwise>
            </c:choose>
        </a>
        <a class="btn gray" href="javascript:window.close();">
            <i class="material-icons-outlined">close</i> 닫기
        </a>
    </div>
</div>
</body>
</html>
