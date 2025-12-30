<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2025-12-23
  Time: 오후 2:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>


<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>
<script src="${pageContext.request.contextPath}/js/comm/json2.js" ></script>


<script type="text/javascript">
    $(function (){

    });

    function fn_validateUpdateCdForm(){
        // Minimal client validation (server-side Bean Validation is the source of truth)
        var cdNm = $.trim($("#cdNm").val());
        if (!cdNm) {
            alert("코드 명은 필수입력값입니다.");
            $("#cdNm").focus();
            return false;
        }
        return true;
    }

    //수정
    function fn_updateCd(){
        if (!fn_validateUpdateCdForm()) {
            return;
        }

        if(!confirm("코드를 수정하시겠습니까?")) {
            return;
        }

        $.ajax({
            type: 'POST',
            url: "${CONTEXT_PATH}/toy/admin/sys/code/cd/update.doax",
            data: $("#frmCd").serialize(),
            dataType: "json",
            beforeSend: function (xhr) {
                // Your custom logic only (loading spinner etc.)
            },
            success: function(res) {
                // If auth failed, server returns redirectUrl for AJAX calls.
                if (res && res.redirectUrl) {
                    alert(res.errorMessage || "Authentication required.");
                    if (window.opener) { window.opener.location.href = res.redirectUrl;}
                    window.close();
                    return;
                }
                if (res.result === "Y") {
                    alert("정상적으로 수정되었습니다.");
                    // Refresh opener grid safely
                    if (window.opener && window.opener.fn_setCdJsGrid) {
                        window.opener.fn_setCdJsGrid("${code.groupCd}");
                    } else if (window.opener) {
                        window.opener.location.reload();
                    }
                    window.close();
                    return;
                }
                if (res.result === "N") {
                    // Bean Validation / parameter validation failed
                    alert(res.errorMessage || "Validation failed.");
                    //  focus likely field
                    $("#cdNm").focus();
                    return;
                }
                if (res.result === "None") {
                    alert(res.errorMessage || "Code does not exist (maybe deleted by another admin).");
                    if (window.opener && window.opener.fn_setCdJsGrid) {
                        window.opener.fn_setCdJsGrid("${code.groupCd}");
                    }
                    window.close();
                    return;
                }
                if (res.result === "Invalid") {
                    alert(res.errorMessage || "Invalid data.");
                    return;
                }
                alert(res.errorMessage || "Update failed.");
            },
            error: function(request, status, error) {
                // Keep your existing common handler signature style
                fn_AjaxError(request, status, error, "${CONTEXT_PATH}/toy/admin/login.do");
            }
        });
    }
</script>
</head>
<body>
<div class="win-popup">
    <div class="flex justify baseline title2-area">
        <h3 class="title2">코드 상세</h3>
        <p class="top-caption">
            <i class="required"></i>
            <strong class="txt">표시 필수 입력사항</strong>
        </p>
    </div>
    <!-- If code is null (deleted concurrently), show message and close safely -->
    <c:if test="${empty code}">
        <script type="text/javascript">
            alert("해당 코드를 찾을 수 없습니다. (다른 관리자에 의해 삭제되었을 수 있습니다.)");
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
                <th>코드그룹 아이디</th>
                <td>${code.groupCd}</td>
                <th>코드그룹 명</th>
                <td>${code.cdGroupNm}</td>
            </tr>
            <tr>
                <th>코드 아이디</th>
                <td>${code.cd}</td>
                <th><img src="${pageContext.request.contextPath}/images/admin/icon/check.png" alt="체크"> 코드 명</th>
                <td><input type="text" id="cdNm" name="cdNm" value="${code.cdNm}" style="width:300px;"/></td>
            </tr>
            <tr>
                <th>코드정보1</th>
                <td><input type="text" name="aditInfo1" value="${code.aditInfo1}" style="width:300px;"/></td>

                <th>코드정보2</th>
                <td><input type="text" name="aditInfo2" value="${code.aditInfo2}" style="width:300px;"/></td>

            </tr>
            <tr>
                <th>사용여부</th>
                <td>
                    <label for="useYnY" class="lb-ch">
                        <input id="useYnY"
                               type="radio"
                               name="useYn"
                               value="Y"
                               <c:if test="${code.useYn == 'Y'}">checked="checked"</c:if>>
                        <span class="text">사용</span>
                    </label>

                    <label for="useYnN" class="lb-ch">
                        <input id="useYnN"
                               type="radio"
                               name="useYn"
                               value="N"
                               <c:if test="${code.useYn == null || code.useYn == 'N'}">checked="checked"</c:if>>
                        <span class="text">사용안함</span>
                    </label>
                </td>


                <th>생성일시(아이디)</th>
                <td>${code.regDt} (${code.regUid})</td>
            </tr>

            <tr>
                <th>최근수정여부</th>
                <td>
                    <c:choose>
                        <c:when test="${not empty code.updDt or not empty code.updUid}">
                            수정됨
                        </c:when>
                        <c:otherwise>
                            미수정
                        </c:otherwise>
                    </c:choose>
                </td>
                <th>최근수정일시(아이디)</th>
                <td>
                    <c:choose>
                        <c:when test="${not empty code.updDt or not empty code.updUid}">
                            ${code.updDt} (${code.updUid})
                        </c:when>
                        <c:otherwise>
                            -
                        </c:otherwise>
                    </c:choose>
                </td>

            </tr>
            </tbody>
        </table>
    </form>

    <div class="btn-right st1 mb-0">
        <a class="btn black" href="javascript:fn_updateCd();"><i class="material-icons-outlined">edit</i> 수 정</a>
        <a class="btn gray" href="javascript:window.close();"><i class="material-icons-outlined">clear</i> 닫 기</a>
        </span>

    </div>

</div>
</body>
</html>