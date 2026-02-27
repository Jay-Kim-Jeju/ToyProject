<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2025-12-23
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>

<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>

<script type="text/javascript">
    var I18N_CODE_DETAIL = {
        codeNameRequired: "<spring:message code='admin.system.code.detail.alert.codeNameRequired' javaScriptEscape='true' />",
        confirmUpdate: "<spring:message code='admin.system.code.detail.confirm.update' javaScriptEscape='true' />",
        authRequired: "<spring:message code='admin.common.authRequired' javaScriptEscape='true' />",
        updated: "<spring:message code='admin.system.code.detail.alert.updated' javaScriptEscape='true' />",
        validationFailed: "<spring:message code='admin.system.code.detail.alert.validationFailed' javaScriptEscape='true' />",
        notFound: "<spring:message code='admin.system.code.detail.alert.notFound' javaScriptEscape='true' />",
        invalidData: "<spring:message code='admin.common.invalidData' javaScriptEscape='true' />",
        updateFailed: "<spring:message code='admin.system.code.detail.alert.updateFailed' javaScriptEscape='true' />",
        codeMissingOnLoad: "<spring:message code='admin.system.code.detail.alert.codeMissingOnLoad' javaScriptEscape='true' />"
    };

    function fn_validateUpdateCdForm(){
        var cdNm = $.trim($("#cdNm").val());
        if (!cdNm) {
            alert(I18N_CODE_DETAIL.codeNameRequired);
            $("#cdNm").focus();
            return false;
        }
        return true;
    }

    function fn_updateCd(){
        if (!fn_validateUpdateCdForm()) {
            return;
        }

        if(!confirm(I18N_CODE_DETAIL.confirmUpdate)) {
            return;
        }

        $.ajax({
            type: 'POST',
            url: "${CONTEXT_PATH}/toy/admin/sys/code/cd/update.ac",
            data: $("#frmCd").serialize(),
            dataType: "json",
            beforeSend: function (xhr) {},
            success: function(res) {
                if (res && res.redirectUrl) {
                    alert(res.errorMessage || I18N_CODE_DETAIL.authRequired);
                    if (window.opener) { window.opener.location.href = res.redirectUrl; }
                    window.close();
                    return;
                }
                if (res.result === "Y") {
                    alert(I18N_CODE_DETAIL.updated);
                    if (window.opener && window.opener.fn_setCdJsGrid) {
                        window.opener.fn_setCdJsGrid("${code.groupCd}");
                    } else if (window.opener) {
                        window.opener.location.reload();
                    }
                    window.close();
                    return;
                }
                if (res.result === "N") {
                    alert(res.errorMessage || I18N_CODE_DETAIL.validationFailed);
                    $("#cdNm").focus();
                    return;
                }
                if (res.result === "None") {
                    alert(res.errorMessage || I18N_CODE_DETAIL.notFound);
                    if (window.opener && window.opener.fn_setCdJsGrid) {
                        window.opener.fn_setCdJsGrid("${code.groupCd}");
                    }
                    window.close();
                    return;
                }
                if (res.result === "Invalid") {
                    alert(res.errorMessage || I18N_CODE_DETAIL.invalidData);
                    return;
                }
                alert(res.errorMessage || I18N_CODE_DETAIL.updateFailed);
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
        <h3 class="title2"><spring:message code="admin.system.code.detail.title" /></h3>
        <p class="top-caption">
            <i class="required"></i>
            <strong class="txt"><spring:message code="admin.common.required" /></strong>
        </p>
    </div>

    <c:if test="${empty code}">
        <script type="text/javascript">
            alert(I18N_CODE_DETAIL.codeMissingOnLoad);
            window.close();
        </script>
    </c:if>

    <form name="frmCd" id="frmCd" method="post" >
        <input type="hidden" name="groupCd" value="${code.groupCd}" />
        <input type="hidden" name="cd" value="${code.cd}" />
        <table class="row">
            <colgroup>
                <col style="width: 15%">
                <col style="width: 35%">
                <col style="width: 15%">
                <col style="width: 35%">
            </colgroup>
            <tbody>
            <tr>
                <th><spring:message code="admin.system.code.common.field.groupCd" /></th>
                <td>${code.groupCd}</td>
                <th><spring:message code="admin.system.code.common.field.groupName" /></th>
                <td>${code.cdGroupNm}</td>
            </tr>
            <tr>
                <th><spring:message code="admin.system.code.common.field.code" /></th>
                <td>${code.cd}</td>
                <th><img src="${pageContext.request.contextPath}/images/admin/icon/check.png" alt="check"> <spring:message code="admin.system.code.common.field.codeName" /></th>
                <td><input type="text" id="cdNm" name="cdNm" value="${code.cdNm}" style="width:300px;"/></td>
            </tr>
            <tr>
                <th><spring:message code="admin.system.code.detail.field.addInfo1" /></th>
                <td><input type="text" name="aditInfo1" value="${code.aditInfo1}" style="width:300px;"/></td>
                <th><spring:message code="admin.system.code.detail.field.addInfo2" /></th>
                <td><input type="text" name="aditInfo2" value="${code.aditInfo2}" style="width:300px;"/></td>
            </tr>
            <tr>
                <th><spring:message code="admin.system.code.common.field.useYn" /></th>
                <td>
                    <label for="useYnY" class="lb-ch">
                        <input id="useYnY" type="radio" name="useYn" value="Y" <c:if test="${code.useYn == 'Y'}">checked="checked"</c:if>>
                        <span class="text"><spring:message code="admin.common.status.use" /></span>
                    </label>
                    <label for="useYnN" class="lb-ch">
                        <input id="useYnN" type="radio" name="useYn" value="N" <c:if test="${code.useYn == null || code.useYn == 'N'}">checked="checked"</c:if>>
                        <span class="text"><spring:message code="admin.common.status.notUse" /></span>
                    </label>
                </td>
                <th><spring:message code="admin.system.code.detail.field.createdInfo" /></th>
                <td>${code.regDt} (${code.regUid})</td>
            </tr>
            <tr>
                <th><spring:message code="admin.system.code.detail.field.modifiedYn" /></th>
                <td>
                    <c:choose>
                        <c:when test="${not empty code.updDt or not empty code.updUid}">
                            <spring:message code="admin.common.status.modified" />
                        </c:when>
                        <c:otherwise>
                            <spring:message code="admin.common.status.notModified" />
                        </c:otherwise>
                    </c:choose>
                </td>
                <th><spring:message code="admin.system.code.detail.field.modifiedInfo" /></th>
                <td>
                    <c:choose>
                        <c:when test="${not empty code.updDt or not empty code.updUid}">
                            ${code.updDt} (${code.updUid})
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </td>
            </tr>
            </tbody>
        </table>
    </form>

    <div class="btn-right st1 mb-0">
        <a class="btn black" href="javascript:fn_updateCd();"><i class="material-icons-outlined">edit</i> <spring:message code="admin.common.button.edit" /></a>
        <a class="btn gray" href="javascript:window.close();"><i class="material-icons-outlined">clear</i> <spring:message code="admin.common.button.close" /></a>
    </div>

</div>
</body>
</html>
