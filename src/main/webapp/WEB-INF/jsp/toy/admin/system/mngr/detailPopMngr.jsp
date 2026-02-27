<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2026-01-13
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>
<script src="${pageContext.request.contextPath}/js/admin/system.js?jsCssVer=${jsCssVer}" ></script>

<script type="text/javascript">
    $(function () {
        ToyAdminSystem.MngrDetail.init();
    });
</script>
</head>

<body>
<div class="win-popup">
    <div class="flex justify baseline title2-area">
        <h3 class="title2"><spring:message code="admin.system.mngr.detail.title" /></h3>
        <p class="top-caption">
            <i class="required"></i>
            <strong class="txt"><spring:message code="admin.common.requiredFields" /></strong>
        </p>
    </div>

    <form id="detailForm" name="detailForm" method="post">
        <input type="hidden" id="mngrUid" name="mngrUid" value="${detail.mngrUid}" />

        <table class="row">
            <colgroup>
                <col style="width: 30%">
                <col style="width: 70%">
            </colgroup>
            <tbody>
            <tr>
                <th><div class="tit"><spring:message code="admin.system.mngr.common.field.id" /></div></th>
                <td>${detail.mngrUid}</td>
            </tr>

            <tr>
                <th><div class="tit required" data-field="mngrNm"><spring:message code="admin.system.mngr.common.field.name" /></div></th>
                <td>
                    <input type="text" id="mngrNm" name="mngrNm" class="wp100" value="${detail.mngrNm}" />
                </td>
            </tr>

            <tr>
                <th><div class="tit"><spring:message code="admin.system.mngr.common.field.email" /></div></th>
                <td>
                    <input type="text" id="emlAdres" name="emlAdres" class="wp100 email" value="${detail.emlAdres}" />
                </td>
            </tr>

            <tr>
                <th><div class="tit required" data-field="telno"><spring:message code="admin.system.mngr.common.field.phone" /></div></th>
                <td>
                    <input type="text" id="telno" name="telno" class="wp100 tel" value="${detail.telno}" />
                </td>
            </tr>

            <tr>
                <th><div class="tit required" data-field="useYn"><spring:message code="admin.system.mngr.common.field.useYn" /></div></th>
                <td>
                    <div style="display:flex; gap:14px; align-items:center; flex-wrap:wrap;">
                        <label style="display:flex; align-items:center; gap:6px;">
                            <input type="radio" name="useYn" value="Y"
                                   <c:if test="${detail.useYn eq 'Y'}">checked="checked"</c:if> />
                            Y
                        </label>
                        <label style="display:flex; align-items:center; gap:6px;">
                            <input type="radio" name="useYn" value="N"
                                   <c:if test="${detail.useYn eq 'N'}">checked="checked"</c:if> />
                            N
                        </label>
                    </div>
                </td>
            </tr>

            <tr>
                <th><div class="tit"><spring:message code="admin.system.mngr.detail.field.passwordStatus" /></div></th>
                <td>
                    <span id="pwResetStatus">-</span>
                </td>
            </tr>

            <tr>
                <th><div class="tit"><spring:message code="admin.system.mngr.detail.field.authApplied" /></div></th>
                <td>
                    <c:choose>
                        <c:when test="${detail.authAppliedYn eq 'Y'}">
                            <span class="text-blue"><spring:message code="admin.common.status.applied" /></span>
                        </c:when>
                        <c:otherwise>
                            <span class="text-red"><spring:message code="admin.common.status.notApplied" /></span>
                            <div style="margin-top:6px; font-size:12px;">
                                <spring:message code="admin.system.mngr.detail.help.authNotAssigned" />
                            </div>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>

            <tr>
                <th><div class="tit"><spring:message code="admin.system.mngr.detail.field.lastLogin" /></div></th>
                <td>${detail.lastLgnDt}</td>
            </tr>

            <tr>
                <th><div class="tit"><spring:message code="admin.system.mngr.detail.field.registered" /></div></th>
                <td>${detail.regDt}</td>
            </tr>

            </tbody>
        </table>
    </form>

    <div class="btn-right st1 mb-0">
        <a id="btnResetPw" class="btn gray" href="javascript:fn_resetPassword();">
            <i class="material-icons-outlined">refresh</i> <spring:message code="admin.system.mngr.detail.button.resetPassword" />
        </a>

        <a id="btnSave" class="btn blue" href="javascript:fn_updateMngr();">
            <i class="material-icons-outlined">save</i> <spring:message code="admin.common.button.save" />
        </a>

        <a class="btn gray" href="javascript:window.close();">
            <i class="material-icons-outlined">close</i> <spring:message code="admin.common.button.close" />
        </a>
    </div>
</div>
</body>
</html>
