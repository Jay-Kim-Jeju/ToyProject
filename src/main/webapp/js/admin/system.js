/* =========================================================
 * Admin > System > Manager (popup scripts)
 * ========================================================= */
(function (window, $) {
    "use strict";

    window.ToyAdminSystem = window.ToyAdminSystem || {};
    var NS = window.ToyAdminSystem;

    function nvl(v) {
        return (v === undefined || v === null) ? "" : String(v);
    }

    function refreshOpenerMngrGrid() {
        try {
            if (window.opener && !window.opener.closed && typeof window.opener.fn_refreshMngrGrid === "function") {
                window.opener.fn_refreshMngrGrid();
            }
        } catch (e) {
            // ignore
        }
    }

    function setPwResetStatus(text, isError) {
        var $el = $("#pwResetStatus");
        if ($el.length === 0) return;

        $el.removeClass("text-blue text-red");
        $el.addClass(isError ? "text-red" : "text-blue");
        $el.text(text);
    }

    NS.bindTelDynamicHyphen = function () {
        $(document)
            .off("keyup.toyTel blur.toyTel", ".tel")
            .on("keyup.toyTel blur.toyTel", ".tel", function () {
                // common.js function
                gfn_telDynamicHyphen(this);
            });
    };

    // -----------------------------
    // Admin > System > Manager > Detail Popup
    // -----------------------------
    NS.MngrDetail = {
        init: function () {
            NS.bindTelDynamicHyphen();
        },

        normalizeInputs: function () {
            var $nm = $("#mngrNm");
            var $tel = $("#telno");
            var $eml = $("#emlAdres");

            if ($nm.length) $nm.val($.trim($nm.val()));
            if ($tel.length) $tel.val($.trim($tel.val()));
            if ($eml.length) $eml.val($.trim($eml.val()));
        },

        validate: function () {
            // common.js: required + tel/email validation based on classes and data-field
            return fn_chkForm();
        }
    };
    // Keep these as global functions because JSP buttons are already calling them.
    window.fn_updateMngr = function () {
        NS.MngrDetail.normalizeInputs();

        if (!NS.MngrDetail.validate()) {
            return;
        }

        $.ajax({
            type: "POST",
            url: "/toy/admin/sys/mngr/update.ac",
            data: $("#detailForm").serialize(),
            dataType: "json",
            success: function (data) {
                var r = (data && data.result) ? String(data.result) : "N";

                if (r === "Y") {
                    alert("Saved.");
                    refreshOpenerMngrGrid();
                    window.close();
                    return;
                }

                if (r === "Invalid") {
                    alert(nvl(data.errorMessage) || "Invalid data.");
                    return;
                }

                if (r === "None") {
                    alert("Manager does not exist.");
                    refreshOpenerMngrGrid();
                    return;
                }

                alert(nvl(data.errorMessage) || "Update failed. Please contact the administrator.");
            },
            error: fn_AjaxErrorAdmin
        });
    };

    // -----------------------------
    // Admin > System > Manager > Insert Popup
    // -----------------------------
    NS.MngrInsert = {
        init: function () {
            NS.bindTelDynamicHyphen();
            },
        normalizeInputs: function () {
            var $uid = $("#mngrUid");
            var $nm = $("#mngrNm");
            var $tel = $("#telno");
            var $eml = $("#emlAdres");
            if ($uid.length) $uid.val($.trim($uid.val()));
            if ($nm.length) $nm.val($.trim($nm.val()));
            if ($tel.length) $tel.val($.trim($tel.val()));
            if ($eml.length) $eml.val($.trim($eml.val()));
            },
        validate: function () {
            // 1) Required/format base check via common.js
            if (!fn_chkForm()) return false;
            // 2) ID rule (project-specific): lowercase letters + digits, 6~20
            var uid = nvl($("#mngrUid").val());
            var uidReg = /^[a-z0-9]{6,20}$/;
            if (!uidReg.test(uid)) {
                alert("ID must be 6–20 chars (lowercase letters and digits).");
                $("#mngrUid").focus();
                return false;
            }
            // 3) Phone/email strict checks (reuse common.js helpers if available)
            var tel = nvl($("#telno").val());
            if (typeof gfn_chkTel === "function" && !gfn_chkTel(tel)) {
                alert("Invalid phone format.");
                $("#telno").focus();
                return false;
            }
            var eml = nvl($("#emlAdres").val());
                   if (eml && typeof gfn_chkEmail === "function" && !gfn_chkEmail(eml)) {
                       alert("Invalid email format.");
                       $("#emlAdres").focus();
                       return false;
                   }
                   return true;
        }
    };
    window.fn_insertMngr = function () {
        NS.MngrInsert.normalizeInputs();
        if (!NS.MngrInsert.validate()) {
            return;
        }
        $.ajax({
            type: "POST",
            url: "/toy/admin/sys/mngr/insert.ac",
            data: $("#insertForm").serialize(),
            dataType: "json",
            success: function (data) {
                var r = (data && data.result) ? String(data.result) : "N";
                // Insert result rule:
                // - server result: "Y" or "SmsFail"
                // - alerts (fixed order):
                //   1) "Manager created."
                //   2) "SMS sent." OR "SMS failed. RequestId: ..."
                // - if Y: close popup (after refreshing opener grid)
                // - if SmsFail: keep popup open (no extra navigation)
                if (r === "Y") {
                    alert("Manager created.");
                    alert("SMS sent.");
                    refreshOpenerMngrGrid();
                    window.close();
                    return;
                }
                if (r === "SmsFail") {
                    alert("Manager created.");
                    var reqId = nvl(data.requestId);
                    alert("SMS failed. RequestId: " + (reqId ? reqId : "-"));
                    // Keep popup open (per UX)
                    return;
                }
                if (r === "Invalid") {
                    alert(nvl(data.errorMessage) || "Invalid data.");
                    return;
                }
                if (r === "Duple") {
                    alert(nvl(data.errorMessage) || "ID already exists.");
                    $("#mngrUid").focus();
                    return;
                }
                alert(nvl(data.errorMessage) || "Create failed. Please contact the administrator.");
                },
            error: fn_AjaxErrorAdmin
        });
    };



    window.fn_resetPassword = function () {
        var mngrUid = $("#mngrUid").val();
        if (!mngrUid) {
            alert("mngrUid is required.");
            return;
        }

        $.ajax({
            type: "POST",
            url: "/toy/admin/sys/mngr/resetPassword.ac",
            data: { mngrUid: mngrUid },
            dataType: "json",
            success: function (data) {
                var r = (data && data.result) ? String(data.result) : "N";

                // Result/Alert rule (fixed):
                // - server result: "Y" or "SmsFail"
                // - alert step 1: "Password has been reset."
                // - if SmsFail: alert step 2: "SMS sending failed. RequestId: ..."
                // - popup stays open always
                if (r === "Y" || r === "SmsFail") {
                    alert("Password has been reset.");

                    // Status label update WITHOUT fetching tempPwIssuedDt
                    if (r === "Y") {
                        setPwResetStatus("Temporary password issued.", false);
                    } else {
                        setPwResetStatus("Temporary password issued (SMS failed).", true);
                        var reqId = nvl(data.requestId);
                        alert("SMS sending failed. RequestId: " + (reqId ? reqId : "-"));
                    }

                    // Grid refresh is OK even though popup stays open
                    refreshOpenerMngrGrid();
                    return;
                }

                if (r === "Invalid") {
                    alert(nvl(data.errorMessage) || "Invalid data.");
                    return;
                }

                if (r === "None") {
                    alert("Manager does not exist.");
                    refreshOpenerMngrGrid();
                    return;
                }

                alert(nvl(data.errorMessage) || "Reset password failed. Please contact the administrator.");
            },
            error: fn_AjaxErrorAdmin
        });
    };

})(window, window.jQuery);

