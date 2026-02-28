/****************************************************************
 * common.js
 *
 *
 * 	gfn_startLodingBar			==> ??泥? 濡??⑸??????
 * 	gfn_finishLodingBar			==> ??泥? 濡??⑸??醫?猷?
 *	adddash						==> ??吏? ?????????? ?쎌??
 *	fn_AjaxError				==> Ajax ???? 泥?由?
 *	fn_AjaxErrorAdmin			==> Ajax ???? 泥?由?(admin)
 *  beforeSend : function(xhr)	==> Global Ajax CSRF Injection(ajaxSetup)
 *	fn_checkNumber				==> ?レ??留????? 媛??? *	fn_checkNumerBlur			==> ?대?? object ?レ???몄? 泥댄??
 *	isNumber					==> ??移?????猷??멸? 寃??? *	getCookie					==> cookie 媛?媛??몄?ㅺ?? *	setCookie					==> cookie 媛??ㅼ??
 *	deleteCookie				==> cookie 媛?????
 *	leadingZeros				==> ??由???梨??곌?? *	fn_chkAll					==> checkbox ??泥? ????
 *	fn_chkEach					==> checkbox 媛?蹂? ???? ?? *	fn_chkForm					==> ??媛?泥댄??
 *	fn_validMsg					==> validation 硫???吏? 諛???
 *	gfn_chkEmail				==> ?대??????? 寃??? *	gfn_chkSpace				==> 怨듬갚??????吏? 寃??? *	gfn_chkSpecial				==> ?뱀??臾몄?? ?ы?? ?щ? ????
 *	gfn_chkTel					==> ????踰??? 寃???('-' ?ы??)
 *	gfn_chkTel2					==> ????踰??? 寃??? ('-' ????)
 *	gfn_chkInt					==> ????寃??? *	gfn_chkBsnmRegNo			==> ?ъ?????깅?踰??? 寃??? *	gfn_chkTextNullByName		==>	null?몄? 寃???(name?쇰?)
 *	gfn_chkTextNullByNameMsg	==>	null?몄? 寃???(name?쇰?)
 *	gfn_chkTextNullById			==> null?몄? 寃???(ID?쇰?)
 *	gfn_chkTextNullByIdMsg		==> null?몄? 寃???(ID?쇰?)
 *	gfn_chkTextNullBySelect		==> null?몄? 寃???by name (select)
 *	gfn_chkTextEmailByName		==> ?대???寃???by name
 *	gfn_chkTextSpaceByName		==> 怨듬갚 寃???by name
 *	gfn_chkTextTelByName		==> ????踰??? 寃???by name
 *	gfn_chkTextTelByNameMsg		==> ????踰??? 寃???by name
 *	gfn_chkTextTel2ByNameMsg	==> ????踰??? 寃??? by name
 *	gfn_chkTextIntByName		==> ???? 寃???by name
 *	gfn_chkCheckBoxByName		==> null?몄? 寃???by name (check box)
 * 	gfn_chkCheckBoxById			==> null?몄? 寃???by id (check box)
 * 	gfn_chkCheckBoxByIdMsg		==> null?몄? 寃???by id (check box)
 * 	gfn_telHyphen				==> ????踰??? ??????異???
 * 	gfn_telDynamicHyphen		==> ????踰??? ??????????異???
 * 	gfn_onlyNumber              ==> ?レ?? ????嫄?
 *  gfn_bsnmRegNoHyphen			==> ?ъ?????깅?踰?????????異???
 * 	gfn_bsnmRegNoDynamicHyphen	==> ?ъ?????깅?踰?????????????異???
 * 	gfn_commas					==> ?몄??由?留??? 肄ㅻ? 異???
 * 	gfn_chkImg					==> ?대?吏? ???????몄? 寃??? * 	gfn_chkImgCtrl				==>
 * 	gfn_chkImgByIdMsg			==>
 * 	gfn_checkPassword			==> 鍮???踰??? 洹?移? : ??臾? ??/??臾??? ?レ??, ?뱀??臾몄?? 議고?? 10??由? ?댁??
 * 	gfn_removeExceptNumber		==> ?レ?? ????嫄?
 * 	gfn_inputNumberFormat		==> ???? 3??由???肄ㅻ?
 * 	gfn_dateAddDash				==> yyyyMMdd -> yyyy-MM-dd 濡?蹂?寃? *  gfn_changeRcmmGoodsStatus	==> 異?泥????? ?깅?/?댁??
 *******************************************************************************************************/

// 濡?????硫? ???? -
function gfn_startLodingBar() {
    var html =
        '<div class="loading-bar">' +
        '<img src="/images/admin/icon/basic/loading.gif" alt="濡??⑹??>' +
        '</div>';

    $('body').append(html);
}

// 濡?????硫? 醫?猷? - 
function gfn_finishLodingBar() {
    $('.loading-bar').remove();
}


/* ??吏? ?????????? ?쎌?? */
function adddash(gap, a1, a2) {
    if ( event.keyCode != 8 ) {
        if ( gap.value.length==a1 ) gap.value=gap.value+"-";
        if ( gap.value.length==a2 ) gap.value=gap.value+"-";
    }
}

/**
 * Ajax ???? 泥?由?
 * @param request
 * @param status
 * @param error
 */
function fn_AjaxError(request, status, error, loginPath){
    if (fn_HandleAdminAuthRedirect(request, loginPath)) {
        return;
    }

    if (request.status == "510"){
        alert(msgMap.get('string.errorOccurredLoginSessionExpired'));	/*???ш? 諛???????????. 濡?洹??몄?????? 留?猷??????듬????*/
        location.replace(loginPath);
        return;
    }

    // ??遺?遺??? 移댄??怨?由????? ???? ?ㅻ? 硫???吏?媛? ??二? 諛???????濡??쇰?? 二쇱?? 泥?由???    // alert("???????ㅻ?\n愿?由ъ????寃? 臾몄???댁＜?몄??." );

    console.log("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
}

function fn_HandleAdminAuthRedirect(request, loginPath) {
    if (!request) {
        return false;
    }

    var statusCode = Number(request.status || 0);
    if (statusCode !== 401 && statusCode !== 403) {
        return false;
    }

    var res = null;
    if (request.responseJSON && typeof request.responseJSON === "object") {
        res = request.responseJSON;
    } else if (request.responseText) {
        try {
            res = JSON.parse(request.responseText);
        } catch (e) {
            res = null;
        }
    }

    if (!res || !res.redirectUrl) {
        return false;
    }

    var reason = ((res.reason || "") + "").toUpperCase();
    if (reason === "LOGIN_REQUIRED") {
        alert("濡?洹????몄????留?猷?????嫄곕?? 濡?洹??몄?? ?????⑸????");
    } else if (reason === "AUTH_CHANGED") {
        alert("沅??? ??蹂닿? 蹂?寃쎈?????ㅼ?? 濡?洹??명??????.");
    } else if (reason === "FORBIDDEN") {
        alert("?대?? 湲곕????????沅?????????????.");
    }

    location.replace(res.redirectUrl || loginPath);
    return true;
}

/**
 * Global CSRF header injection for all jQuery AJAX requests.
 * Works even if individual $.ajax() defines its own beforeSend.
 *
 * Requires meta tags:
 *  - <meta name="_csrf" content="..."/>
 *  - <meta name="_csrf_header" content="..."/>
 */
(function (window, $) {
    "use strict";

    function getCsrfMeta() {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        return { token: token, header: header };
    }

    $(function () {
        if (!$ || !$.ajaxPrefilter) return;

        var csrf = getCsrfMeta();
        if (!csrf.token || !csrf.header) return;

        $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
            jqXHR.setRequestHeader(csrf.header, csrf.token);
        });
    });

    window.ToyCsrf = {
        get: getCsrfMeta
    };

})(window, window.jQuery);


/**
 * Ajax ???? 泥?由?(admin)
 * @param request
 * @param status
 * @param error
 */
function fn_AjaxErrorAdmin(request, status, error){
    if (fn_HandleAdminAuthRedirect(request)) {
        return;
    }

    if(request.status == "500"){
        alert("濡?洹?????蹂닿? ????????. 濡?洹?????吏???????湲?諛???????.");
        //location.reload(true);
    } else {
        //alert("???ш? 諛???????????!");
        alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
    }
}

/**
 * ?レ??留????? 媛??? */
function fn_checkNumber() {
    //醫??? 諛⑺???? 諛깆???????? ??由??? ??????????????
    if(event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 37 || event.keyCode == 39|| event.keyCode == 46 ) return;

    if( (event.keyCode < 48 ) || ((event.keyCode > 57) && (event.keyCode < 96)) || (event.keyCode > 105 )) {
        event.returnValue = false;
    }
}

/**
 * ?대?? object ?レ???몄? 泥댄??
 * @param obj
 * @returns {Boolean}
 */
function fn_checkNumerBlur(obj){
    if(!isNumber(obj.value)){
        alert("?レ??留????? 媛??ν??????.");
        obj.focus();
        return false;
    }
}

/* ??移?????猷??멸? 寃???*/
function isNumber(str){
    var i;
    if (str.length == 0) return true;
    for (i = 0; i < str.length; i++) {
        if (str.charAt(i) < '0' || str.charAt(i) > '9') return false;
    }

    return true;
}

// getCookie
function getCookie(key){
    var cook = document.cookie + ";";
    var idx =  cook.indexOf(key, 0);
    var val = "";

    if(idx != -1){
        cook = cook.substring(idx, cook.length);
        begin = cook.indexOf("=", 0) + 1;
        end = cook.indexOf(";", begin);
        val = unescape( cook.substring(begin, end) );
    }
    return val;
}

// setCookie
function setCookie(cookieName, value, exdays){
    var exdate = new Date();
    exdate.setDate(exdate.getDate() + exdays);
    var cookieValue = escape(value) + ((exdays==null) ? "" : "; expires=" + exdate.toGMTString());
    document.cookie = cookieName + "=" + cookieValue;
}

// deleteCookie
function deleteCookie(cookieName){
    var expireDate = new Date();
    expireDate.setDate(expireDate.getDate() - 1);
    document.cookie = cookieName + "= " + "; expires=" + expireDate.toGMTString();
}
function leadingZeros(n, digits) {
    var zero = '';
    n = n.toString();

    if (n.length < digits) {
        for (var i=0; i<digits - n.length; i++)
            zero += '0';
    }
    return zero + n;
}

// ??泥? ????
function fn_chkAll(chkObj) {
    if (chkObj.checked)
        $(".chkSel").prop("checked", true);
    else
        $(".chkSel").prop("checked", false);
}
function fn_chkEach(chkObj) {
    var chkFlag = true;
    $(".chkSel").each(function() {
        if ($(this).prop("checked") == false) {
            chkFlag = false;
            return;
        }
    });

    $("#chkAll").prop("checked", chkFlag);
}

/***
 * ??媛?泥댄?? (2025-12-07)
 * note : ?????댄???'required' ?ы????怨? data-field="媛?泥대?? ?ㅼ??
 ?レ??留?泥댄?? ??input class??'number' 異???
 ?대???泥댄?? ??input class??'email' 異???
 * ???? : <div class="tit required" data-field="psncpa">????</div>
 *        <input class="width40 number" type="text" name="psncpa">
 ***/
function fn_chkForm() {
    var msg_str = '';

    // null ???? 怨듬갚 泥댄??
    $('.required').each(function() {
        var obj = $('[name="' + $(this).data('field') + '"]');  // 諛곗??name ??諛??? 紐삵???????? - 

        if (obj.attr('type') == 'text' || obj.attr('type') == 'tel' || obj.attr('type') == 'password' || obj.prop('tagName') == 'TEXTAREA' || obj.attr('type') == 'number' || obj.attr('type') == 'email') {	// text ???????? textarea ?????대㈃...
            if ($.trim(obj.val()) == '') {
                msg_str = '"' + $(this).text() + '" ??瑜? ??????二쇱????';
                obj.focus();
                return false;
            }
        }
        else if (obj.prop('tagName') == 'SELECT' && obj.val() == '') {	// select ?????대㈃...
            msg_str = '"' + $(this).text() + '" ??瑜? ??????二쇱????';
            obj.focus();
            return false;
        }
        else if (obj.attr('type') == 'file' && obj.val() == '' && obj.is(":disabled") == false) {	// file ????寃???- 
            msg_str = '"' + $(this).text() + '" ??瑜? ??????二쇱????';
            obj.focus();
            return false;
        }
        else if (obj.attr('type') == 'radio' && obj.is(':checked') == false) {	// radio ?????대㈃...
            msg_str = '"' + $(this).text() + '" ??瑜? ??????二쇱????';
            obj.focus();
            return false;
        }
        else if (obj.attr('type') == 'checkbox' && obj.is(':checked') == false) {	// checkbox ?????대㈃...
            msg_str = '"' + $(this).text() + '" ??瑜? 泥댄????二쇱????';
            obj.focus();
            return false;
        }
    });

    // ?レ?? 泥댄??
    if (msg_str == '') {	// 異??? 硫??몄?媛? ???쇰??..
        $('.number').each(function() {
            if ($(this).val() != '') {
                var chkVal = $(this).val().replace(/,/g, '');
                if ($.isNumeric(chkVal) == false) {
                    msg_str = fn_validMsg($(this), '?レ??留???????二쇱????');
                    $(this).focus();
                    return false;
                } else if ($(this).data('min') != undefined && $(this).data('max') != undefined && (chkVal > $(this).data('max') || chkVal < $(this).data('min'))) {
                    msg_str = fn_validMsg($(this), $(this).data('min') + ' ~ ' + $(this).data('max') + ' 踰???濡???????二쇱????');
                    $(this).focus();
                    return false;
                } else if ($(this).data('min') != undefined && chkVal < $(this).data('min')) {
                    msg_str = fn_validMsg($(this), $(this).data('min') + ' ?댁???쇰? ??????二쇱????');
                    $(this).focus();
                    return false;
                } else if ($(this).data('max') != undefined && chkVal < $(this).data('max')) {
                    msg_str = fn_validMsg($(this), $(this).data('max') + ' ?댄??濡???????二쇱????');
                    $(this).focus();
                    return false;
                }
            }
        });
    }

    // ????踰??? 泥댄??
    if (msg_str == '') {	// 異??? 硫??몄?媛? ???쇰??..
        $('.tel').each(function() {
            var telNum = $(this).val()
            if ($(this).val() != '' && gfn_chkTel(telNum) == false) {
                msg_str = msg_str = fn_validMsg($(this), '????????????二쇱????\n(ex. 012-3456-7890)');
                $(this).focus();
                return false;
            }
        });
    }

    // ?대???泥댄??
    if (msg_str == '') {	// 異??? 硫??몄?媛? ???쇰??..
        $('.email').each(function() {
            if ($(this).val() != '' && gfn_chkEmail($(this).val()) == false) {
                msg_str = msg_str = fn_validMsg($(this), '?대???????????????二쇱????\n(ex. abs@abc.com)');
                $(this).focus();
                return false;
            }
        });
    }

    // ?ъ?????깅?踰??? 泥댄??
    if (msg_str == '') {	// 異??? 硫??몄?媛? ???쇰??..
        $('.bsnmRegNo').each(function() {
            if ($(this).val() != '' && gfn_chkBsnmRegNo($(this).val()) == false) {
                msg_str = msg_str = fn_validMsg($(this), '????????????二쇱????\n(ex. 000-00-00000)');
                $(this).focus();
                return false;
            }
        });
    }

    // 洹??? 泥댄??
    if (msg_str == '') {	// 異??? 硫??몄?媛? ???쇰??..
        $('.chkRequiredRule').each(function() {
            if ($(this).prop("checked") == false) {
                msg_str = $(this).data('title') + '??瑜? ???? ??泥댄????二쇱????';
                $("#" + $(this).data('chkid')).attr("tabindex", -1).focus();
                return false;
            }
        });
    }

    if (msg_str != '') {	// 異??? 硫??몄?媛? ???쇰??..
        alert(msg_str);
        return false;
    } else {
        return true;
    }
}

// validation 硫???吏? 諛???
function fn_validMsg(obj, msg) {
    var titStr = "";
    var compStr = obj.attr("name");
    var $strObj;

    if (obj.closest("tr").find('.tit').length != 0) {
        $strObj = obj.closest("tr").find('.tit');
    } else if (obj.closest("div").find('.tit').length != 0) {
        $strObj = obj.closest("div").find('.tit');
    }

    $strObj.each(function() {
        if ($(this).data('field') == compStr) {
            titStr = $(this).text();
        }
    });

    return '"' + titStr + '" ??(?? ' + msg;
}
function gfn_chkEmail(email) {
    var exptext = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/;
    if (exptext.test(email) == false) {
        return false;
    }
    return true;
}
function gfn_chkSpace(textVal) {
    var blank_pattern = /[\s]/g;
    if (blank_pattern.test(textVal) == true) {
        return false;
    }
    return true;
}

//?뱀??臾몄?? ?ы?? ?щ?
function gfn_chkSpecial(str) {
    var special_pattern = /[`~!@#$%^&*|\\\'\";:\/?]/gi;
    if (special_pattern.test(str) == true) {
        return true;
    } else {
        return false;
    }
}


//????踰??? 寃???('-' ?ы??)
function gfn_chkTel(textVal) {
    var mobile_pattern = /^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})-[0-9]{3,4}-[0-9]{4}$/;		// ex) 010-1234-5678
    var tel_pattern = /^(02|0[3-6]{1}[1-5]{1})-?([0-9]{3,4})-?[0-9]{4}$/;					// ex) 02-1234-5678
    var rep_pattern = /^(15|16|18)[0-9]{2}-?[0-9]{4}$/;										// ex) 1588-1588
    var rep2_pattern = /^(02|0[3-6]{1}[1-5]{1})-?(15|16|18)[0-9]{2}-?[0-9]{4}$/;			// ex) 02-1588-1588
    var rep3_pattern = /^(070|(050[2-8]{0,1})|080|013)-?([0-9]{3,4})-?[0-9]{4}$/;			// ex) 070-1234-5678


    if (mobile_pattern.test(textVal)) {			// ?몃????泥댄??...
        return true;
    } else if (tel_pattern.test(textVal)) {		// ?쇰????? 泥댄??...
        return true;
    } else if (rep_pattern.test(textVal)) {		// ???????? (ex. 1588-1588) 泥댄??...
        return true;
    } else if (rep2_pattern.test(textVal)) { 	// ???????? (ex. 02-1588-1588) 泥댄??...
        return true;
    } else if (rep3_pattern.test(textVal)) {	// ???????? (ex. 070-1234-5678) 泥댄??....
        return true;
    }

    return false;
}

//????踰??? 寃???('-' ????)
function gfn_chkTel2(textVal) {
    var mobile_pattern = /^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})[0-9]{3,4}[0-9]{4}$/;	// ex) 01012345678
    var tel_pattern = /^(02|0[3-6]{1}[1-5]{1})?([0-9]{3,4})?[0-9]{4}$/;					// ex) 0212345678
    var rep_pattern = /^(15|16|18)[0-9]{2}?[0-9]{4}$/;									// ex) 15881588
    var rep2_pattern = /^(02|0[3-6]{1}[1-5]{1})?(15|16|18)[0-9]{2}?[0-9]{4}$/;			// ex) 0215881588
    var rep3_pattern = /^(070|(050[2-8]{0,1})|080|013)?([0-9]{3,4})?[0-9]{4}$/;			// ex) 07012345678

    if (mobile_pattern.test(textVal)) {			// ?몃????泥댄??...
        return true;
    } else if (tel_pattern.test(textVal)) {		// ?쇰????? 泥댄??...
        return true;
    } else if (rep_pattern.test(textVal)) {		// ???????? (ex. 1588-1588) 泥댄??...
        return true;
    } else if (rep2_pattern.test(textVal)) { 	// ???????? (ex. 02-1588-1588) 泥댄??...
        return true;
    } else if (rep3_pattern.test(textVal)) {	// ???????? (ex. 070-1234-5678) 泥댄??....
        return true;
    }

    return false;
}
function gfn_chkInt(str) {
    var special_pattern = /^[0-9]{1,}$/;
    if (special_pattern.test(str) == true) {
        return true;
    } else {
        return false;
    }
}
function gfn_chkBsnmRegNo(bsnmRegNo) {
    var numberMap = bsnmRegNo.replace(/-/gi, '').split('').map(function (d){
        return parseInt(d, 10);
    });

    if(numberMap.length == 10){
        var keyArr = [1, 3, 7, 1, 3, 7, 1, 3, 5];
        var chk = 0;

        keyArr.forEach(function(d, i){
            chk += d * numberMap[i];
        });

        chk += parseInt((keyArr[8] * numberMap[8])/ 10, 10);

        return Math.floor(numberMap[9]) === ( (10 - (chk % 10) ) % 10);
    }

    return false;
}

//null?몄? 寃???(name?쇰?)
function gfn_chkTextNullByName(name, text) {
    //return gfn_chkTextNullByNameMsg(name, text + "??(/?? ???????κ???????.");
    if ($("[name='" + name + "']").val() == "") {
        alert(text + "??(/?? ???????κ???????.");
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
}

//null?몄? 寃???(name?쇰?)
function gfn_chkTextNullByNameMsg(name, msg) {
    if ($("[name='" + name + "']").val() == "") {
        alert(msg);
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
}

function gfn_chkTextNullById(id, text) {
    if ($("#" + id).val() == "") {
        alert(text + "??(/?? ???????κ???????.");
        $("#" + id).focus();
        return false;
    }
    return true;
}

function gfn_chkTextNullByIdMsg(id, msg) {
    if ($("#" + id).val() == "") {
        alert(msg);
        $("#" + id).focus();
        return false;
    }
    return true;
}

function gfn_chkTextNullBySelect(name, text) {
    if ($("[name='" + name + "']").val() == "") {
        alert(text + "??/瑜? ??????二쇱????");
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
}
function gfn_chkTextEmailByName(name, text) {
    var val = $("[name='" + name + "']").val();
    if(val==''){
        return true;
    }

    if (gfn_chkEmail(val) == false) {
        alert(text + " ???????щ?瑜댁? ????????.");
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
}
function gfn_chkTextSpaceByName(name, text) {
    var val = $("[name='" + name + "']").val();
    if(val==''){
        return true;
    }

    if (gfn_chkSpace(val) == false) {
        alert(text + "??(/?? 怨듬갚???ъ??????????????.");
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
}
function gfn_chkTextTelByName(name, text) {
    return gfn_chkTextTelByNameMsg(name, text + "??(/?? ????踰??? ????????????二쇱????\n(ex. 000-0000-0000)");
    /*
    var val = $("[name='" + name + "']").val();
    if(val==''){
        return true;
    }

    if (gfn_chkTel(val) == false) {
        alert(text + "??(/?? ????踰??? ??????????????\n(000-0000-0000 ????)");
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
    */
}

function gfn_chkTextTelByNameMsg(name, msg) {
    var val = $("[name='" + name + "']").val();
    if(val==''){
        return true;
    }

    if (gfn_chkTel(val) == false) {
        alert(msg);
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
}


function gfn_chkTextTel2ByNameMsg(name, msg) {
    var val = $("[name='" + name + "']").val();
    if(val==''){
        return true;
    }

    if (gfn_chkTel2(val) == false) {
        alert(msg);
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
}
function gfn_chkTextIntByName(name, text) {
    var val = $("[name='" + name + "']").val();
    if(val==''){
        return true;
    }

    if (gfn_chkInt(val) == false) {
        alert(text + "??(/?? ?レ??留????? ??????");
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
}


function gfn_chkCheckBoxByName(name, text) {
    if ($("[name='" + name + "']").is(":checked") == false) {
        alert(text + "??(/?? ?????ы?? ??????");
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
}

function gfn_chkCheckBoxById(id, text) {
    return gfn_chkCheckBoxByIdMsg(id, text + "??(/?? ?????ы?? ??????");
}

function gfn_chkCheckBoxByIdMsg(id, msg) {
    if ($("#" + id ).is(":checked") == false) {
        alert(msg);
        $("#" + id ).focus();
        return false;
    }
    return true;
}

//????踰??? ??????異??? - 
function gfn_telHyphen(x) {
    return x != '' && x != null ? x.toString().replace(/(^02|^0505|^1[0-9]{3}|^0[0-9]{2})([0-9]+)?([0-9]{4})$/,"$1-$2-$3").replace("--", "-") : '';
}

//????踰??? ??????????異??? - 
function gfn_telDynamicHyphen(th) {
    var telVal = $(th).val().replace(/[^0-9]/g, "").replace(/(^02|^0505|^1[0-9]{3}|^0[0-9]{2})([0-9]+)?([0-9]{4})$/,"$1-$2-$3").replace("--", "-");
    if (telVal.length > 7 && telVal.indexOf("-") == -1) {
        telVal = "";
    }
    $(th).val( telVal );
}

//?レ?? ????嫄? - 
function gfn_onlyNumber(str) {
    var regex = /[^0-9]/g;
    return str.replace(regex, "");
}

//?ъ?????깅?踰??? ??????異???
function gfn_bsnmRegNoHyphen(x) {
    return x != '' && x != null ? x.toString().replace(/([0-9]{3})([0-9]+)?([0-9]{5})$/,"$1-$2-$3").replace("--", "-") : '';
}

//?ъ?????깅?踰??? ??????異???
function gfn_bsnmRegNoDynamicHyphen(th) {
    $(th).val( $(th).val().replace(/[^0-9]/g, "").replace(/([0-9]{3})([0-9]+)?([0-9]{5})$/,"$1-$2-$3").replace("--", "-") );
}


//?몄??由?留??? 肄ㅻ?
function gfn_commas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}
function gfn_chkImg(filePath) {
    var _fileLen = filePath.length;
    var _lastDot = filePath.lastIndexOf('.');
    var ext = filePath.substring(_lastDot+1, _fileLen).toLowerCase();

    if(ext=="jpg" ||ext=="jpeg"|| ext=="gif"|| ext=="png"){
        return true;
    }
    return false;

}

function gfn_chkImgCtrl(name, msg){

    var list = new Array();
    var listCtrl = new Array();
    $("input[name="+name+"]").each(function(index, item){
        list.push($(item).val());
        listCtrl.push(item);
    });

    for (var i in list) {
        if(list[i] == ""){
            //???쇰???듦낵
        }else{
            if(gfn_chkImg(list[i]) == false){
                alert(msg);
                listCtrl[i].focus();
                return false;
            }

        }
    }
    return true;


}

function gfn_chkImgByIdMsg(id, Msg) {
    if( !gfn_chkImg($("#"+id).val()) ){
        alert(Msg);
        return false;
    }
    return true;
}

// Password policy:
// - At least 8 characters
// - Must include: uppercase, lowercase, digit, special character (each at least one)
// - No spaces
function gfn_checkPassword(str){
    // Special chars allowed: $`~!@$!%*#^?&()\-_=+
    var reg = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$`~!@$!%*#^?&\\(\\)\-_=+]).{8,}$/;

    if (gfn_chkSpace(str) == false) {
        alert("Password cannot contain spaces.");
        return false;
    }

    if (reg.test(str) == false) {
        alert("Password must be at least 8 characters and include uppercase, lowercase, number, and special character.");
        return false;
    }

    return true;
}

// 鍮???踰??? 洹?移? : ??臾? ??臾??? ?レ??, ?뱀??臾몄?? 議고?? 6??由? ?댁??
function gfn_checkPassword2(str) {
    var reg = /^(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+{}\[\]:;<>,.?~\\/-]).{6,}$/;

    if (gfn_chkSpace(str) == false) {
        alert("鍮???踰?????怨듬갚???ъ??????????????.");
        return false;
    }

    if (reg.test(str) == false) {
        alert("鍮???踰???????臾? ??臾??? ?レ??, ?뱀??臾몄?? 議고???쇰? 6??由? ?댁???댁?????⑸????");
        return false;
    }

    return true;
}


// ?レ?? ????嫄?
function gfn_removeExceptNumber(obj) {
    var i = obj;
    var startPosition = i.value.length - i.selectionEnd;
    i.value = i.value.replace(/^\D+/g, '');
    var len = Math.max(i.value.length - startPosition, 0);
    return i.setSelectionRange(len, len);
}

// ?レ?? ????嫄? 諛?3??由??????? 肄ㅻ? - 
function gfn_inputNumberFormat(obj) {
    var i = obj;

    if(i.value == "0") return i.value = 0;
    if(i.value == "00") return i.value = 0;

    var startPosition = i.value.length - i.selectionEnd;
    i.value = i.value.replace(/^0+|\D+/g, '').replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
    var len = Math.max(i.value.length - startPosition, 0);
    return i.setSelectionRange(len, len);

}
function gfn_dateAddDash(date) {
    if (date == undefined || date == null || date == "") {	// 媛??? ???쇰??..
        return "";
    } else {
        return date.substr(0, 4) + "-" + date.substr(4, 2) + "-" + date.substr(6, 2);
    }
}


/**
 * JsGrid ??吏? ?щ㎎ (20220601 -> 2022-06-01)
 * ?ъ?⑸??
 *  1. jquery 珥?湲????⑥?????? ???⑥?? ?몄?
 *  2. JsGrid fields???? [type:"date"] ?쇰? ????
 */
function gfn_initJsGridDateType() {
    var MyDateField = function(config) {
        jsGrid.Field.call(this, config);
    };

    MyDateField.prototype = new jsGrid.Field({

        sorter: function(date1, date2) {
            var d1 = new Date(parseInt(date1.substr(0, 4), 10), parseInt(date1.substr(4, 2) - 1, 10), parseInt(date1.substr(6, 2), 10));
            var d2 = new Date(parseInt(date2.substr(0, 4), 10), parseInt(date2.substr(4, 2) - 1, 10), parseInt(date2.substr(6, 2), 10));
            return d1 - d2;
        },

        itemTemplate: function(value) {
            var d = new Date(parseInt(value.substr(0, 4), 10), parseInt(value.substr(4, 2) - 1, 10), parseInt(value.substr(6, 2), 10));
            return d.getFullYear() + "-" + ((d.getMonth() + 1) > 9 ? (d.getMonth() + 1).toString() : "0" + (d.getMonth() + 1)) + "-" + (d.getDate() > 9 ? d.getDate().toString() : "0" + d.getDate().toString());
        },

        insertTemplate: function(value) {
            return this._insertPicker = $("<input>").datepicker({ defaultDate: new Date() });
        },

        editTemplate: function(value) {
            return this._editPicker = $("<input>").datepicker().datepicker("setDate", new Date(value));
        },

        insertValue: function() {
            return this._insertPicker.datepicker("getDate").toISOString();
        },

        editValue: function() {
            return this._editPicker.datepicker("getDate").toISOString();
        }
    });

    jsGrid.fields.date = MyDateField;
}


/**
 * JsGrid ????踰??? ?щ㎎ (01012341234 -> 010-1234-1234)
 * ?ъ?⑸??
 *  1. jquery 珥?湲????⑥?????? ???⑥?? ?몄?
 *  2. JsGrid fields???? [type:"phone"] ?쇰? ????
 */
function gfn_initJsGridPhoneType() {
    var MyPhoneField = function(config) {
        jsGrid.Field.call(this, config);
    };

    MyPhoneField.prototype = new jsGrid.Field({

        sorter: function(d1, d2) {
            return d1 - d2;
        },

        itemTemplate: function(value) {
            return new libphonenumber.AsYouType('KR').input(value);
        }

    });

    jsGrid.fields.phone = MyPhoneField;
}

/**
 * ????踰??? ?щ㎎
 * 湲곗〈 ????踰??? 媛??? '-'??遺??? ????濡?蹂?寃? * 紐⑤?? ???????????? ?쇨? ???? ????濡?泥?由?
 * ?ъ????libphonenumber-js.min.js 異?????二쇱???? */
function gfn_formatPhoneNumber(obj) {
    var target = $(obj);
    if (target.is('input')) {
        var oldValue = target.val();
        var newValue = new libphonenumber.AsYouType('KR').input(oldValue);
        target.val(newValue);
    } else {
        var oldValue = target.text().trim();
        var newValue = new libphonenumber.AsYouType('KR').input(oldValue);
        target.text(newValue);
    }
}

/**
 * ????愿?由?> 異?泥????? ?깅?|?댁??
 * ???? 紐⑸????? 異?泥????? ?щ? checkbox ?????쇰? ?깅?|?댁?? ?ㅼ??
 */
/*
function gfn_changeRcmmGoodsStatus(chk, goodsUuid, goodsSe){

    var isChecked = $(chk).is(':checked');
    var goodsRcmmYn = (isChecked) ? "Y":"N";

    console.log(goodsSe);

    // 異?泥? ???? ????蹂?寃?    var url = CONTEXT_PATH + "/toy/goods/updateGoodsRcmmStatusAjax.doax";

    $.ajax({
        type: 'GET',
        async:false,
        url: url,
        data: {goodsRcmmYn : goodsRcmmYn, goodsUuid : goodsUuid, goodsSe : goodsSe},
        success: function(response) {
            if (response.result == "${Constant.FLAG_N}"){
                alert(response.errorMessage);
                return;
            }

            var statusMsg = (goodsRcmmYn == "Y") ? "?쇰? ?ㅼ??" : " ?ㅼ?? ?댁??";
            alert("異?泥?????"+statusMsg+" ?????듬????);
        },
        error: fn_AjaxError
    });
}
*/


/****************************
 * ???? 濡?洹??? */
function fn_autoLogin(userUid){
        console.log("--fn_autoLogin already logged in");

    if(userUid!=''){
        //濡?洹????????쇰㈃
        console.log("--fn_autoLogin already logged in");
        return false;
    }

    if(localStorage.getItem("autoLogin_yn") != "Y"){
        console.log("--fn_autoLogin already logged in");

        return false;
    }



    var mberEmailAdres = localStorage.getItem("autoLogin_email");
    var scrNoEncpt = encodeURI( localStorage.getItem("autoLogin_pw") );

    var tknCertkey = "";
    var cntnStorageSe = "pc";


    var hw = fn_chkOS();
    if (hw == "and") {
        //tknCertkey = android.getTokenId();
        cntnStorageSe = "and";
    }else if (hw == "ios") {
        //泥?由? ??爰?!

        cntnStorageSe = "ios";
    }


    console.log("---fn_autoLogin>"+mberEmailAdres + " " + scrNoEncpt+ " " + tknCertkey + " "+ cntnStorageSe);





    $.ajax({
        type: 'GET',
        url: CONTEXT_PATH + "/user/member/loginAuto.doax?mberEmailAdres="+mberEmailAdres+"&pwdEncpt="+scrNoEncpt+"&tknCertkey="+tknCertkey+"&cntnStorageSe="+cntnStorageSe,
        //data: $("#login_form").serialize(),
        beforeSend : function(xhr){
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            xhr.setRequestHeader(header, token);
        },
        success: function(data) {
            //濡?洹????깃났?대㈃
            if(data.result == "Y") {

                //localStorage.setItem("autoLogin_yn", "Y");
                //localStorage.setItem("autoLogin_email", data.mberEmlAdresEncpt);
                //localStorage.setItem("autoLogin_pw", data.pwdEncpt);

                location.href = $("#returnURL").val();



                //location.href = data.returnURL;
                //location.href = $("#returnURL").val();
                //console.log("???? 濡?洹????깃났");
                window.location.reload();


            }
            //濡?洹????ㅽ???대㈃
            else {
                //alert(data.message);
                console.log("???? 濡?洹????ㅽ??");
            }
        },
        error: function(request, status, error, loginPath) {
            if (request.status == "510") {
                alert(msgMap.get('string.errorOccurredLoginSessionExpired'));	/*???ш? 諛???????????. 濡?洹??몄?????? 留?猷??????듬????*/
                location.replace(loginPath);
                return false;
            } else if (request.status == "403") {
                alert(msgMap.get('string.dontHaveAccess'));	/*??洹? 沅?????????????.*/
                return false;
            }

            alert(msgMap.get('string.systemErrorPleaseContactAdministrator'));	/*???????ㅻ?\n愿?由ъ????寃? 臾몄???댁＜?몄??.*/
        }
    });
}


function fn_chkOS(){

    var hw = "";
    var varUA = navigator.userAgent.toLowerCase(); //userAgent 媛??산린

    /*
    if("${Constant.APP_MODE}" == "test"){
        //??????紐⑤??
        hw = "";

    }else{
	*/

    if ( varUA.indexOf('android') > -1) {
        //????濡?????        hw = "and";
    } else if ( varUA.indexOf("iphone") > -1||varUA.indexOf("ipad") > -1||varUA.indexOf("ipod") > -1 ) {
        //IOS
        hw = "ios";
    } else {
        //?????? ????濡???????        hw = "";
    }

    //}

    return hw;
}

