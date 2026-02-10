<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>
<script src="${pageContext.request.contextPath}/js/admin/system.js?jsCssVer=${jsCssVer}" ></script>

<script type="text/javascript">
    $(function () {
        $(function () { ToyAdminSystem.MngrInsert.init(); });
    });
</script>
</head>

<body>
    <div class="win-popup">

        <div class="flex justify baseline title2-area">
            <h3 class="title2">Manager Create</h3>
            <p class="top-caption">
                <i class="required"></i>
                <strong class="txt">Required</strong>
            </p>
        </div>



        <form id="insertForm" name="insertForm" method="post">
            <!-- Default: active -->
            <input type="hidden" id="useYn" name="useYn" value="Y"/>

            <table class="row">
                <colgroup>
                    <col style="width: 20%">
                    <col style="width: 80%">
                </colgroup>
                <tbody>
                <tr>
                    <th><div class="tit required" data-field="mngrUid">ID</div></th>
                    <td>
                        <input type="text" id="mngrUid" name="mngrUid" class="wp100" maxlength="20" autocomplete="off"/>
                        <p class="help">6–20 chars, lowercase letters and digits.</p>
                    </td>
                </tr>

                <tr>
                    <th><div class="tit required" data-field="mngrNm">Name</div></th>
                    <td>
                        <input type="text" id="mngrNm" name="mngrNm" class="wp100" maxlength="50" autocomplete="off"/>
                    </td>
                </tr>

                <tr>
                    <th><div class="tit required" data-field="telno">Phone</div></th>
                    <td>
                        <input type="text" id="telno" name="telno" class="wp100 tel" maxlength="20" autocomplete="off"/>
                    </td>
                </tr>

                <tr>
                    <th><div class="tit">Email</div></th>
                    <td>
                        <input type="text" id="emlAdres" name="emlAdres" class="wp100 email" maxlength="100" autocomplete="off"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>

        <div class="btn-right st1 mb-0">
            <a class="btn blue" href="javascript:fn_insertMngr();">
                <i class="material-icons-outlined">add</i> Create
            </a>
            <a class="btn gray" href="javascript:window.close();">
                <i class="material-icons-outlined">close</i> Close
            </a>
        </div>
    </div>

</body>
</html>