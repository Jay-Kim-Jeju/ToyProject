<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>

<script type="text/javascript">
    $(function () {
        // No-op
    });

    // Change password (My Account)
    function fn_changeMyPassword() {
        var curPw = $('input[name=currentPassword]').val();
        var newPw = $('input[name=newPassword]').val();
        var newPw2 = $('input[name=newPasswordConfirm]').val();

        if (!curPw) {
            alert('Please enter your current password.');
            $('input[name=currentPassword]').focus();
            return;
        }

        if (!newPw) {
            alert('Please enter a new password.');
            $('input[name=newPassword]').focus();
            return;
        }

        if (!newPw2) {
            alert('Please confirm your new password.');
            $('input[name=newPasswordConfirm]').focus();
            return;
        }

        if (newPw !== newPw2) {
            alert('New passwords do not match.');
            $('input[name=newPassword]').focus();
            return;
        }

        // Client-side password policy check (common.js)
        if (typeof gfn_checkPassword === "function") {
            if (gfn_checkPassword(newPw) === false) {
                $('input[name=newPassword]').focus();
                return;
            }
        }

        if (!confirm("Change your password?")) {
            return;
        }

        $.ajax({
            type: 'POST',
            url: "<c:url value='/toy/admin/my/changeMyPassword.ac'/>",
            data: $("#password_form").serialize(),
            success: function(data) {
                // We rely on result code only. UI decides messages.
                var r = parseInt(data.result, 10);

                // NOTE: Adjust these constants to your CmConstants values if needed.
                // RESULT_OK
                if (r === 1) {
                    alert("Password updated.");
                    window.close();
                    return;
                }

                // RESULT_FORBIDDEN (e.g., not verified)
                if (r === -3) {
                    alert("Verification required.");
                    return;
                }

                // RESULT_INVALID (e.g., wrong current password / policy fail)
                if (r === -2) {
                    alert("Invalid input. Please check your passwords.");
                    return;
                }

                alert("Failed to change password. Please try again.");
            },
            error: fn_AjaxError
        });
    }
</script>
</head>

<body>
<div class="win-popup">
    <div class="flex justify baseline title2-area">
        <h3 class="title2">Change Password</h3>
        <p class="top-caption">
            <i class="required"></i>
            <strong class="txt">Required fields</strong>
        </p>
    </div>

    <form id="password_form" name="password_form" method="post">
        <table class="row">
            <colgroup>
                <col style="width: 30%">
                <col style="width: 70%">
            </colgroup>
            <tbody>
            <tr>
                <th><div class="tit">Policy</div></th>
                <td>
                    <div style="line-height:1.5;">
                        Password must be at least 8 characters and include uppercase, lowercase, number, and special character.
                    </div>
                </td>
            </tr>
            <tr>
                <th><div class="tit required">Current Password</div></th>
                <td><input type="password" name="currentPassword" class="wp100" autocomplete="current-password"/></td>
            </tr>
            <tr>
                <th><div class="tit required">New Password</div></th>
                <td><input type="password" name="newPassword" class="wp100" autocomplete="new-password"/></td>
            </tr>
            <tr>
                <th><div class="tit required">Confirm New Password</div></th>
                <td><input type="password" name="newPasswordConfirm" class="wp100" autocomplete="new-password"/></td>
            </tr>
            </tbody>
        </table>
    </form>

    <div class="btn-right st1 mb-0">
        <a class="btn blue" href="javascript:fn_changeMyPassword();">
            <i class="material-icons-outlined">check</i> Change
        </a>
        <a class="btn gray" href="javascript:window.close();">
            <i class="material-icons-outlined">close</i> Close
        </a>
    </div>
</div>
</body>
</html>
