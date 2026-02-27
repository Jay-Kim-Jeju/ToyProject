<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>
<script src="${pageContext.request.contextPath}/js/admin/system.js?jsCssVer=${jsCssVer}" ></script>

<c:set var="isUpdateMode" value="${mode eq 'U'}" />

<script type="text/javascript">
    var IS_UPDATE_MODE = ("${isUpdateMode}" === "true");
    var I18N_ALLOW_FORM = {
        selectManagerRequired: "<spring:message code='admin.system.allow.form.alert.selectManagerRequired' javaScriptEscape='true' />",
        allowIpRequired: "<spring:message code='admin.system.allow.form.alert.allowIpRequired' javaScriptEscape='true' />",
        confirmInsert: "<spring:message code='admin.system.allow.form.confirm.insert' javaScriptEscape='true' />",
        confirmUpdate: "<spring:message code='admin.system.allow.form.confirm.update' javaScriptEscape='true' />",
        successInsert: "<spring:message code='admin.system.allow.form.alert.inserted' javaScriptEscape='true' />",
        successUpdate: "<spring:message code='admin.system.allow.form.alert.updated' javaScriptEscape='true' />",
        duple: "<spring:message code='admin.system.allow.form.alert.duple' javaScriptEscape='true' />",
        invalid: "<spring:message code='admin.system.allow.form.alert.invalid' javaScriptEscape='true' />",
        forbidden: "<spring:message code='admin.system.allow.form.alert.forbidden' javaScriptEscape='true' />",
        notFound: "<spring:message code='admin.system.allow.form.alert.notFound' javaScriptEscape='true' />",
        failed: "<spring:message code='admin.system.allow.form.alert.failed' javaScriptEscape='true' />"
    };

    $(function () {
        fn_initAllowIpForm();
    });

    function fn_initAllowIpForm() {
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

    function fn_receiveSelectedAllowMngr(mngrUid, mngrNm) {
        $("#mngrUid").val(mngrUid || "");
        $("#mngrNmView").val(mngrNm || "");
    }

    function fn_validateAllowIpForm() {
        if (!$.trim($("#mngrUid").val())) {
            alert(I18N_ALLOW_FORM.selectManagerRequired);
            return false;
        }

        if (!IS_UPDATE_MODE && !$.trim($("#allowIp").val())) {
            alert(I18N_ALLOW_FORM.allowIpRequired);
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

        if (!$.trim(payload.cidrPrefix)) {
            payload.cidrPrefix = "";
        }

        if (!confirm(IS_UPDATE_MODE ? I18N_ALLOW_FORM.confirmUpdate : I18N_ALLOW_FORM.confirmInsert)) {
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
                    alert(IS_UPDATE_MODE ? I18N_ALLOW_FORM.successUpdate : I18N_ALLOW_FORM.successInsert);
                    if (window.opener && window.opener.fn_allowIpGridRefresh) {
                        window.opener.fn_allowIpGridRefresh();
                    }
                    window.close();
                    return;
                }

                if (res.result === "Duple") {
                    alert(res.errorMessage || I18N_ALLOW_FORM.duple);
                    return;
                }

                if (res.result === "Invalid") {
                    alert(res.errorMessage || I18N_ALLOW_FORM.invalid);
                    return;
                }

                if (res.result === "Forbidden") {
                    alert(res.errorMessage || I18N_ALLOW_FORM.forbidden);
                    return;
                }

                if (res.result === "None") {
                    alert(res.errorMessage || I18N_ALLOW_FORM.notFound);
                    if (window.opener && window.opener.fn_allowIpGridRefresh) {
                        window.opener.fn_allowIpGridRefresh();
                    }
                    return;
                }

                alert(res.errorMessage || I18N_ALLOW_FORM.failed);
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
                <c:when test="${isUpdateMode}"><spring:message code="admin.system.allow.form.title.update" /></c:when>
                <c:otherwise><spring:message code="admin.system.allow.form.title.insert" /></c:otherwise>
            </c:choose>
        </h3>
        <p class="top-caption">
            <i class="required"></i>
            <strong class="txt"><spring:message code="admin.common.required" /></strong>
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
                <th><div class="tit required" data-field="mngrUid"><spring:message code="admin.system.allow.common.field.targetManagerId" /></div></th>
                <td>
                    <div style="display:flex; gap:8px; align-items:center;">
                        <input type="text" id="mngrUid" name="mngrUid" class="wp100" style="max-width:280px;" readonly
                               value="<c:out value='${detail.mngrUid}'/>" />
                        <a class="btn sm" href="javascript:fn_openSelectManagerPopup();">
                            <i class="material-icons-outlined">search</i> <spring:message code="admin.system.allow.form.button.selectManager" />
                        </a>
                    </div>
                    <div style="margin-top:6px;">
                        <input type="text" id="mngrNmView" class="wp100" style="max-width:280px;" readonly
                               placeholder="<spring:message code='admin.system.allow.form.placeholder.selectedManagerName' />" />
                    </div>
                </td>
            </tr>

            <tr>
                <th><div class="tit required" data-field="allowIp"><spring:message code="admin.system.allow.common.field.allowIp" /></div></th>
                <td>
                    <input type="text" id="allowIp" name="allowIp" class="wp100" style="max-width:420px;"
                           <c:if test="${isUpdateMode}">readonly</c:if>
                           value="<c:out value='${detail.allowIp}'/>"
                           placeholder="<spring:message code='admin.system.allow.form.placeholder.allowIp' />" />
                    <c:if test="${isUpdateMode}">
                        <p class="help"><spring:message code="admin.system.allow.form.help.allowIpReadonly" /></p>
                    </c:if>
                </td>
            </tr>

            <tr>
                <th><div class="tit"><spring:message code="admin.system.allow.common.field.cidrPrefix" /></div></th>
                <td>
                    <input type="number" id="cidrPrefix" name="cidrPrefix" class="wp100" style="max-width:180px;"
                           min="0" max="128"
                           <c:if test="${isUpdateMode}">readonly</c:if>
                           value="<c:out value='${detail.cidrPrefix}'/>"
                           placeholder="<spring:message code='admin.system.allow.form.placeholder.cidrPrefix' />" />
                    <p class="help"><spring:message code="admin.system.allow.form.help.cidrPrefix" /></p>
                </td>
            </tr>

            <tr>
                <th><div class="tit"><spring:message code="admin.system.allow.common.field.useYn" /></div></th>
                <td>
                    <label style="display:inline-flex; align-items:center; gap:6px;">
                        <input type="checkbox" id="useYn" name="useYn"
                               <c:if test="${empty detail.useYn or detail.useYn eq 'Y'}">checked="checked"</c:if> />
                        <spring:message code="admin.common.status.use" />
                    </label>
                </td>
            </tr>

            <tr>
                <th><div class="tit"><spring:message code="admin.system.allow.common.field.startDate" /></div></th>
                <td>
                    <div style="display:flex; gap:10px; align-items:center; flex-wrap:wrap;">
                        <input type="date" id="startDt" name="startDt" class="wp100" style="max-width:220px;"
                               data-raw="<c:out value='${detail.startDt}'/>" />
                        <span class="help" style="margin:0;"><spring:message code="admin.system.allow.form.help.startDate" /></span>
                    </div>
                </td>
            </tr>

            <tr>
                <th><div class="tit"><spring:message code="admin.system.allow.common.field.endDate" /></div></th>
                <td>
                    <div style="display:flex; gap:10px; align-items:center; flex-wrap:wrap;">
                        <input type="date" id="endDt" name="endDt" class="wp100" style="max-width:220px;"
                               data-raw="<c:out value='${detail.endDt}'/>" />
                        <span class="help" style="margin:0;"><spring:message code="admin.system.allow.form.help.endDate" /></span>
                    </div>
                </td>
            </tr>

            <tr>
                <th><div class="tit"><spring:message code="admin.system.allow.common.field.memo" /></div></th>
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
                <c:when test="${isUpdateMode}"><spring:message code="admin.common.button.save" /></c:when>
                <c:otherwise><spring:message code="admin.common.button.create" /></c:otherwise>
            </c:choose>
        </a>
        <a class="btn gray" href="javascript:window.close();">
            <i class="material-icons-outlined">close</i> <spring:message code="admin.common.button.close" />
        </a>
    </div>
</div>
</body>
</html>
