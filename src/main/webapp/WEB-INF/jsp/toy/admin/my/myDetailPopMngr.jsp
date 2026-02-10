<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>


<script type="text/javascript">
    var g_verifTimer = null;
    var g_expireAtMs = 0;
    var g_locked = false;

    $(function () {
        // Initial lock state from server
        var verifiedYn = "${verifiedYn}";
        applyVerifiedUI(verifiedYn === "Y");

        // Ensure verification input area is hidden on first load (until Send code success)
        $("#verifArea").hide();
    });

    // Applies disabled look + click blocking for <a> buttons.
    function setLinkDisabled(selector, disabled) {
        var $el = $(selector);
        $el.toggleClass("btn-disabled", disabled);
        $el.attr("aria-disabled", disabled ? "true" : "false");
        $el.data("disabled", disabled ? "Y" : "N");
        if (disabled) {
            $el.attr("tabindex", "-1");
        } else {
            $el.removeAttr("tabindex");
        }
    }

    // Applies UI lock/unlock based on verification status.
    function applyVerifiedUI(isVerified) {
        if (isVerified) {
            $("#verifiedBadge").removeClass("badge-muted").addClass("badge-verified").text("Verified");
            $("#btnSendCode").hide();
            $("#verifArea").hide(); // Hide expire timer + input area after verified
            unlockEditableFields(true);
            stopTimer();
            return;
        }

        $("#verifiedBadge").removeClass("badge-verified").addClass("badge-muted").text("Not verified");
        $("#btnSendCode").show();
        $("#verifArea").hide();
        unlockEditableFields(false);
    }

    // Unlocks editable fields after verification.
    function unlockEditableFields(unlock) {
        $("#mngrNm").prop("disabled", !unlock);
        $("#emlAdres").prop("disabled", !unlock);

        // Before verification: hide action buttons completely
        if (unlock) {
            $("#btnSave").show();
            $("#btnChangePw").show();
            setLinkDisabled("#btnSave", false);
            setLinkDisabled("#btnChangePw", false);
        } else {
            $("#btnSave").hide();
            $("#btnChangePw").hide();
            setLinkDisabled("#btnSave", true);
            setLinkDisabled("#btnChangePw", true);
        }

    }

    // Sends verification code via SMS.
    function fn_sendVerificationNumber() {
        if ($("#btnSendCode").data("disabled") === "Y") {
            return;
        }

        // Telno must exist to send SMS
        var telno = "${detail.telno}";
        if (!telno || telno.trim() === "") {
            alert("Please set your phone number first. Please contact the administrator.");
            return;
        }

        $.ajax({
            type: "POST",
            url: "<c:url value='/toy/admin/my/sendVerificationNumber.doax'/>",
            data: {},
            success: function(data) {
                var r = parseInt(data.result, 10);
                if (r !== 1) {
                    // FAIL: do not show input row
                    alert("Please contact the administrator.");
                    return;
                }

                // Show verification input row
                $("#verifArea").show();
                $("#verifCode").val("").prop("disabled", false);
                setLinkDisabled("#btnVerify", false);

                // Resend is disabled until timer ends or locked condition triggers
                setLinkDisabled("#btnResend", true);
                g_locked = false;

                // Local skip notice (optional but useful)
                if (data.smsStatus === "SKIPPED") {
                    alert("Skipped in local environment.");
                }

                // IMPORTANT: Do NOT unlock Save/ChangePw here. Only after Verify success.
                unlockEditableFields(false);

                // Timer: prefer expireAt from server if present, otherwise fallback to 5 minutes
                if (data.expireAt) {
                    g_expireAtMs = parseInt(data.expireAt, 10);
                } else {
                    g_expireAtMs = new Date().getTime() + (5 * 60 * 1000);
                }
                startTimer();

            },
            error: fn_AjaxError
        });
    }

    // Checks verification code.
    function fn_checkVerificationNumber() {
        if ($("#btnVerify").data("disabled") === "Y") {
            return;
        }

        var code = $("#verifCode").val();
        if (!code) {
            alert("Please enter the verification code.");
            $("#verifCode").focus();
            return;
        }

        $.ajax({
            type: "POST",
            url: "<c:url value='/toy/admin/my/checkVerificationNumber.doax'/>",
            data: { code: code },
            success: function(data) {
                var r = parseInt(data.result, 10);
                if (r === 1) {
                    applyVerifiedUI(true);
                    return;
                }

                // Invalid code
                // If locked, immediately enable resend and disable input/verify
                if (data.lockedYn === "Y") {
                    g_locked = true;
                    $("#verifCode").prop("disabled", true);
                    setLinkDisabled("#btnVerify", true);
                    setLinkDisabled("#btnResend", false);
                    alert("Invalid code. Please resend.");
                    return;
                }

                alert("Invalid code.");
            },
            error: fn_AjaxError
        });
    }

    // Resends code (resets fail count server-side per your policy).
    function fn_resendVerificationNumber() {
        if ($("#btnResend").data("disabled") === "Y") {
            return;
        }
        fn_sendVerificationNumber();
    }

    // Starts countdown timer and updates UI.
    function startTimer() {
        stopTimer();
        tickTimer();
        g_verifTimer = setInterval(tickTimer, 1000);
    }

    // Stops timer.
    function stopTimer() {
        if (g_verifTimer) {
            clearInterval(g_verifTimer);
            g_verifTimer = null;
        }
    }

    // Timer tick: updates remain time and enables resend when expired.
    function tickTimer() {
        var now = new Date().getTime();
        var remainMs = g_expireAtMs - now;
        if (remainMs <= 0) {
            $("#remainTime").text("00:00");
            $("#verifCode").prop("disabled", true);
            setLinkDisabled("#btnVerify", true);
            setLinkDisabled("#btnResend", false);

            stopTimer();
            return;
        }

        var totalSec = Math.floor(remainMs / 1000);
        var mm = String(Math.floor(totalSec / 60)).padStart(2, "0");
        var ss = String(totalSec % 60).padStart(2, "0");
        $("#remainTime").text(mm + ":" + ss);

        // If locked, resend should be enabled immediately regardless of remaining time
        if (g_locked) {
            setLinkDisabled("#btnResend", false);
        }
    }

    // Saves my account info (requires verified).
    function fn_updateMyMngrInfo() {
        if ($("#btnSave").data("disabled") === "Y") {
            return;
        }

        $.ajax({
            type: "POST",
            url: "<c:url value='/toy/admin/my/updateMyMngrInfo.ac'/>",
            data: $("#myForm").serialize(),
            dataType: "json",
            beforeSend: function (xhr) {
                // Your custom logic only (loading spinner etc.)
            },
            success: function(data) {
                var r = parseInt(data.result, 10);
                if (r === 1) {
                    alert("Saved.");
                    if (window.opener && !window.opener.closed) {
                        if (typeof window.opener.fn_refreshMyHeader === "function") {
                            window.opener.fn_refreshMyHeader();
                        }
                    }
                    window.close();
                    return;
                }
                alert("Failed to save.");
            },
            error: fn_AjaxError
        });
    }

    // Opens change password popup (requires verified).
    function fn_openChangePwPop() {
        if ($("#btnChangePw").data("disabled") === "Y") {
            return;
        }
        window.open("<c:url value='/toy/admin/my/changePWPopMngr.do'/>", "myChangePwPop",
            "width=500,height=260,scrollbars=yes");
    }
</script>

<body>
<div class="win-popup">
    <div class="flex justify baseline title2-area">
        <h3 class="title2">My Account</h3>
    </div>

    <form id="myForm" name="myForm" method="post">
        <input type="hidden" name="mngrUid" value="${detail.mngrUid}" />
        <table class="row">
            <colgroup>
                <col style="width: 30%">
                <col style="width: 70%">
            </colgroup>
            <tbody>
            <tr>
                <th><div class="tit">Status</div></th>
                <td>
                    <span id="verifiedBadge" class="badge-muted">Not verified</span>
                </td>
            </tr>
            <tr>
                <th><div class="tit">ID</div></th>
                <td>${detail.mngrUid}</td>
            </tr>

            <tr>
                <th><div class="tit required">Phone</div></th>
                <td>
                    <span>${detail.telno}</span>
                    <a id="btnSendCode" class="btn blue" href="javascript:fn_sendVerificationNumber();" style="margin-left:10px;">
                        <i class="material-icons-outlined">sms</i> Send code
                    </a>
                </td>
            </tr>

            <!-- Verification input area (hidden until send success) -->
            <tr id="verifArea" style="display:none;">
                <th><div class="tit required">Verification</div></th>
                <td>
                    <div style="display:flex; gap:8px; align-items:center; flex-wrap:wrap;">
                        <input type="text" id="verifCode" class="wp30" placeholder="Enter code" maxlength="6"/>
                        <span>Expire in: <strong id="remainTime">05:00</strong></span>
                        <a id="btnVerify" class="btn blue" href="javascript:fn_checkVerificationNumber();">
                            <i class="material-icons-outlined">check</i> Verify
                        </a>
                        <a id="btnResend" class="btn gray" href="javascript:fn_resendVerificationNumber();" disabled="disabled">
                            <i class="material-icons-outlined">refresh</i> Resend
                        </a>
                    </div>
                    <div style="margin-top:6px;">
                        <!-- After verified, we keep this area as "test state" (disabled inputs + Verified text). -->
                    </div>
                </td>
            </tr>

            <tr>
                <th><div class="tit required">Name</div></th>
                <td>
                    <input type="text" id="mngrNm" name="mngrNm" class="wp100" value="${detail.mngrNm}" disabled="disabled"/>
                </td>
            </tr>

            <tr>
                <th><div class="tit">Email</div></th>
                <td>
                    <input type="text" id="emlAdres" name="emlAdres" class="wp100" value="${detail.emlAdres}" disabled="disabled"/>
                </td>
            </tr>
            </tbody>
        </table>
    </form>

    <div class="btn-right st1 mb-0">
        <a id="btnSave" class="btn blue" href="javascript:fn_updateMyMngrInfo();" disabled="disabled">
            <i class="material-icons-outlined">save</i> Save
        </a>
        <a id="btnChangePw" class="btn blue" href="javascript:fn_openChangePwPop();" disabled="disabled">
            <i class="material-icons-outlined">lock</i> Change Password
        </a>
        <a class="btn gray" href="javascript:window.close();">
            <i class="material-icons-outlined">close</i> Close
        </a>
    </div>
</div>
</body>
</html>
