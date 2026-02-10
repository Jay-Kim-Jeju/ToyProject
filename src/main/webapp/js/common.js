/****************************************************************
 * common.js
 *
 *
 * 	gfn_startLodingBar			==> 전체 로딩바 시작
 * 	gfn_finishLodingBar			==> 전체 로딩바 종료
 *	adddash						==> 날짜 하이픈 자동 삽입
 *	fn_AjaxError				==> Ajax 에러 처리
 *	fn_AjaxErrorAdmin			==> Ajax 에러 처리(admin)
 *  beforeSend : function(xhr)	==> Global Ajax CSRF Injection(ajaxSetup)
 *	fn_checkNumber				==> 숫자만 입력 가능
 *	fn_checkNumerBlur			==> 해당 object 숫자인치 체크
 *	isNumber					==> 수치형 자료인가 검사
 *	getCookie					==> cookie 값 가져오기
 *	setCookie					==> cookie 값 설정
 *	deleteCookie				==> cookie 값 삭제
 *	leadingZeros				==> 자릿수 채우기
 *	fn_chkAll					==> checkbox 전체 선택
 *	fn_chkEach					==> checkbox 개별 선택 시
 *	fn_chkForm					==> 폼 값 체크
 *	fn_validMsg					==> validation 메시지 반환
 *	gfn_chkEmail				==> 이메일 양식 검사
 *	gfn_chkSpace				==> 공백이 있는지 검사
 *	gfn_chkSpecial				==> 특수문자 포함 여부 확인
 *	gfn_chkTel					==> 전화번호 검사 ('-' 포함)
 *	gfn_chkTel2					==> 전화번호 검사2 ('-' 제외)
 *	gfn_chkInt					==> 정수검사
 *	gfn_chkBsnmRegNo			==> 사업자 등록번호 검사
 *	gfn_chkTextNullByName		==>	null인지 검사 (name으로)
 *	gfn_chkTextNullByNameMsg	==>	null인지 검사 (name으로)
 *	gfn_chkTextNullById			==> null인지 검사 (ID으로)
 *	gfn_chkTextNullByIdMsg		==> null인지 검사 (ID으로)
 *	gfn_chkTextNullBySelect		==> null인지 검사 by name (select)
 *	gfn_chkTextEmailByName		==> 이메일 검사 by name
 *	gfn_chkTextSpaceByName		==> 공백 검사 by name
 *	gfn_chkTextTelByName		==> 전화번호 검사 by name
 *	gfn_chkTextTelByNameMsg		==> 전화번호 검사 by name
 *	gfn_chkTextTel2ByNameMsg	==> 전화번호 검사2 by name
 *	gfn_chkTextIntByName		==> 정수 검사 by name
 *	gfn_chkCheckBoxByName		==> null인지 검사 by name (check box)
 * 	gfn_chkCheckBoxById			==> null인지 검사 by id (check box)
 * 	gfn_chkCheckBoxByIdMsg		==> null인지 검사 by id (check box)
 * 	gfn_telHyphen				==> 전화번호 하이픈 추가
 * 	gfn_telDynamicHyphen		==> 전화번호 하이픈 동적추가
 * 	gfn_onlyNumber              ==> 숫자 외 제거
 *  gfn_bsnmRegNoHyphen			==> 사업자등록번호 하이픈 추가
 * 	gfn_bsnmRegNoDynamicHyphen	==> 사업자등록번호 하이픈 동적추가
 * 	gfn_commas					==> 세자리 마다 콤마 추가
 * 	gfn_chkImg					==> 이미지 확장자인지 검사
 * 	gfn_chkImgCtrl				==>
 * 	gfn_chkImgByIdMsg			==>
 * 	gfn_checkPassword			==> 비밀번호 규칙 : 영문 대/소문자, 숫자, 특수문자 조합 10자리 이상
 * 	gfn_removeExceptNumber		==> 숫자 외 제거
 * 	gfn_inputNumberFormat		==> 동적 3자리수 콤마
 * 	gfn_dateAddDash				==> yyyyMMdd -> yyyy-MM-dd 로 변경
 *  gfn_changeRcmmGoodsStatus	==> 추천상품 등록/해제
 *******************************************************************************************************/

// 로딩화면 시작 -
function gfn_startLodingBar() {
    var html =
        '<div class="loading-bar">' +
        '<img src="/images/admin/icon/basic/loading.gif" alt="로딩중">' +
        '</div>';

    $('body').append(html);
}

// 로딩화면 종료 - 
function gfn_finishLodingBar() {
    $('.loading-bar').remove();
}


/* 날짜 하이픈 자동 삽입 */
function adddash(gap, a1, a2) {
    if ( event.keyCode != 8 ) {
        if ( gap.value.length==a1 ) gap.value=gap.value+"-";
        if ( gap.value.length==a2 ) gap.value=gap.value+"-";
    }
}

/**
 * Ajax 에러 처리
 * @param request
 * @param status
 * @param error
 */
function fn_AjaxError(request, status, error, loginPath){
    if (request.status == "510"){
        alert(msgMap.get('string.errorOccurredLoginSessionExpired'));	/*에러가 발생했습니다. 로그인세션이 만료되었습니다.*/
        location.replace(loginPath);
        return;
    }

    // 대부분의 카테고리에서 아래 오류 메시지가 자주 발생되므로 일단 주석 처리함
    // alert("시스템 오류\n관리자에게 문의해주세요." );

    console.log("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
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
 * Ajax 에러 처리(admin)
 * @param request
 * @param status
 * @param error
 */
function fn_AjaxErrorAdmin(request, status, error){
    if(request.status == "500"){
        alert("로그인 정보가 없습니다. 로그인 후 진행하시기 바랍니다.");
        //location.reload(true);
    } else {
        //alert("에러가 발생했습니다!");
        alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
    }
}

/**
 * 숫자만 입력 가능
 */
function fn_checkNumber() {
    //좌우 방향키, 백스페이스, 딜리트, 탭키에 대한 예외
    if(event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 37 || event.keyCode == 39|| event.keyCode == 46 ) return;

    if( (event.keyCode < 48 ) || ((event.keyCode > 57) && (event.keyCode < 96)) || (event.keyCode > 105 )) {
        event.returnValue = false;
    }
}

/**
 * 해당 object 숫자인치 체크
 * @param obj
 * @returns {Boolean}
 */
function fn_checkNumerBlur(obj){
    if(!isNumber(obj.value)){
        alert("숫자만 입력 가능합니다.");
        obj.focus();
        return false;
    }
}

/* 수치형 자료인가 검사 */
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

// 자릿수 채우기
function leadingZeros(n, digits) {
    var zero = '';
    n = n.toString();

    if (n.length < digits) {
        for (var i=0; i<digits - n.length; i++)
            zero += '0';
    }
    return zero + n;
}

// 전체 선택
function fn_chkAll(chkObj) {
    if (chkObj.checked)
        $(".chkSel").prop("checked", true);
    else
        $(".chkSel").prop("checked", false);
}

//개별 선택 시
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
 * 폼 값 체크 (2025-12-07)
 * note : 폼 타이틀에 'required' 포함되고 data-field="개체명" 설정
 숫자만 체크 시 input class에 'number' 추가
 이메일 체크 시 input class에 'email' 추가
 * 예제 : <div class="tit required" data-field="psncpa">정원</div>
 *        <input class="width40 number" type="text" name="psncpa">
 ***/
function fn_chkForm() {
    var msg_str = '';

    // null 또는 공백 체크
    $('.required').each(function() {
        var obj = $('[name="' + $(this).data('field') + '"]');  // 배열name 을 받지 못해서 수정 - 

        if (obj.attr('type') == 'text' || obj.attr('type') == 'tel' || obj.attr('type') == 'password' || obj.prop('tagName') == 'TEXTAREA' || obj.attr('type') == 'number' || obj.attr('type') == 'email') {	// text 타입 또는 textarea 타입 이면...
            if ($.trim(obj.val()) == '') {
                msg_str = '"' + $(this).text() + '" 을(를) 입력해 주세요.';
                obj.focus();
                return false;
            }
        }
        else if (obj.prop('tagName') == 'SELECT' && obj.val() == '') {	// select 타입 이면...
            msg_str = '"' + $(this).text() + '" 을(를) 선택해 주세요.';
            obj.focus();
            return false;
        }
        else if (obj.attr('type') == 'file' && obj.val() == '' && obj.is(":disabled") == false) {	// file 타입 검사 - 
            msg_str = '"' + $(this).text() + '" 을(를) 선택해 주세요.';
            obj.focus();
            return false;
        }
        else if (obj.attr('type') == 'radio' && obj.is(':checked') == false) {	// radio 타입 이면...
            msg_str = '"' + $(this).text() + '" 을(를) 선택해 주세요.';
            obj.focus();
            return false;
        }
        else if (obj.attr('type') == 'checkbox' && obj.is(':checked') == false) {	// checkbox 타입 이면...
            msg_str = '"' + $(this).text() + '" 을(를) 체크해 주세요.';
            obj.focus();
            return false;
        }
    });

    // 숫자 체크
    if (msg_str == '') {	// 출력 메세지가 없으면...
        $('.number').each(function() {
            if ($(this).val() != '') {
                var chkVal = $(this).val().replace(/,/g, '');
                if ($.isNumeric(chkVal) == false) {
                    msg_str = fn_validMsg($(this), '숫자만 입력해 주세요.');
                    $(this).focus();
                    return false;
                } else if ($(this).data('min') != undefined && $(this).data('max') != undefined && (chkVal > $(this).data('max') || chkVal < $(this).data('min'))) {
                    msg_str = fn_validMsg($(this), $(this).data('min') + ' ~ ' + $(this).data('max') + ' 범위로 입력해 주세요.');
                    $(this).focus();
                    return false;
                } else if ($(this).data('min') != undefined && chkVal < $(this).data('min')) {
                    msg_str = fn_validMsg($(this), $(this).data('min') + ' 이상으로 입력해 주세요.');
                    $(this).focus();
                    return false;
                } else if ($(this).data('max') != undefined && chkVal < $(this).data('max')) {
                    msg_str = fn_validMsg($(this), $(this).data('max') + ' 이하로 입력해 주세요.');
                    $(this).focus();
                    return false;
                }
            }
        });
    }

    // 전화번호 체크
    if (msg_str == '') {	// 출력 메세지가 없으면...
        $('.tel').each(function() {
            var telNum = $(this).val()
            if ($(this).val() != '' && gfn_chkTel(telNum) == false) {
                msg_str = msg_str = fn_validMsg($(this), '형식을 확인해 주세요.\n(ex. 012-3456-7890)');
                $(this).focus();
                return false;
            }
        });
    }

    // 이메일 체크
    if (msg_str == '') {	// 출력 메세지가 없으면...
        $('.email').each(function() {
            if ($(this).val() != '' && gfn_chkEmail($(this).val()) == false) {
                msg_str = msg_str = fn_validMsg($(this), '이메일 형식을 확인해 주세요.\n(ex. abs@abc.com)');
                $(this).focus();
                return false;
            }
        });
    }

    // 사업자 등록번호 체크
    if (msg_str == '') {	// 출력 메세지가 없으면...
        $('.bsnmRegNo').each(function() {
            if ($(this).val() != '' && gfn_chkBsnmRegNo($(this).val()) == false) {
                msg_str = msg_str = fn_validMsg($(this), '형식을 확인해 주세요.\n(ex. 000-00-00000)');
                $(this).focus();
                return false;
            }
        });
    }

    // 규정 체크
    if (msg_str == '') {	// 출력 메세지가 없으면...
        $('.chkRequiredRule').each(function() {
            if ($(this).prop("checked") == false) {
                msg_str = $(this).data('title') + '을(를) 확인 후 체크해 주세요.';
                $("#" + $(this).data('chkid')).attr("tabindex", -1).focus();
                return false;
            }
        });
    }

    if (msg_str != '') {	// 출력 메세지가 있으면...
        alert(msg_str);
        return false;
    } else {
        return true;
    }
}

// validation 메시지 반환
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

    return '"' + titStr + '" 은(는) ' + msg;
}


//이메일 양식 검사
function gfn_chkEmail(email) {
    var exptext = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/;
    if (exptext.test(email) == false) {
        return false;
    }
    return true;
}

//공백이 있는지 검사
function gfn_chkSpace(textVal) {
    var blank_pattern = /[\s]/g;
    if (blank_pattern.test(textVal) == true) {
        return false;
    }
    return true;
}

//특수문자 포함 여부
function gfn_chkSpecial(str) {
    var special_pattern = /[`~!@#$%^&*|\\\'\";:\/?]/gi;
    if (special_pattern.test(str) == true) {
        return true;
    } else {
        return false;
    }
}


//전화번호 검사 ('-' 포함)
function gfn_chkTel(textVal) {
    var mobile_pattern = /^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})-[0-9]{3,4}-[0-9]{4}$/;		// ex) 010-1234-5678
    var tel_pattern = /^(02|0[3-6]{1}[1-5]{1})-?([0-9]{3,4})-?[0-9]{4}$/;					// ex) 02-1234-5678
    var rep_pattern = /^(15|16|18)[0-9]{2}-?[0-9]{4}$/;										// ex) 1588-1588
    var rep2_pattern = /^(02|0[3-6]{1}[1-5]{1})-?(15|16|18)[0-9]{2}-?[0-9]{4}$/;			// ex) 02-1588-1588
    var rep3_pattern = /^(070|(050[2-8]{0,1})|080|013)-?([0-9]{3,4})-?[0-9]{4}$/;			// ex) 070-1234-5678


    if (mobile_pattern.test(textVal)) {			// 핸드폰 체크...
        return true;
    } else if (tel_pattern.test(textVal)) {		// 일반전화 체크...
        return true;
    } else if (rep_pattern.test(textVal)) {		// 대표전화1 (ex. 1588-1588) 체크...
        return true;
    } else if (rep2_pattern.test(textVal)) { 	// 대표전화2 (ex. 02-1588-1588) 체크...
        return true;
    } else if (rep3_pattern.test(textVal)) {	// 대표전화3 (ex. 070-1234-5678) 체크....
        return true;
    }

    return false;
}

//전화번호 검사 ('-' 제외)
function gfn_chkTel2(textVal) {
    var mobile_pattern = /^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})[0-9]{3,4}[0-9]{4}$/;	// ex) 01012345678
    var tel_pattern = /^(02|0[3-6]{1}[1-5]{1})?([0-9]{3,4})?[0-9]{4}$/;					// ex) 0212345678
    var rep_pattern = /^(15|16|18)[0-9]{2}?[0-9]{4}$/;									// ex) 15881588
    var rep2_pattern = /^(02|0[3-6]{1}[1-5]{1})?(15|16|18)[0-9]{2}?[0-9]{4}$/;			// ex) 0215881588
    var rep3_pattern = /^(070|(050[2-8]{0,1})|080|013)?([0-9]{3,4})?[0-9]{4}$/;			// ex) 07012345678

    if (mobile_pattern.test(textVal)) {			// 핸드폰 체크...
        return true;
    } else if (tel_pattern.test(textVal)) {		// 일반전화 체크...
        return true;
    } else if (rep_pattern.test(textVal)) {		// 대표전화1 (ex. 1588-1588) 체크...
        return true;
    } else if (rep2_pattern.test(textVal)) { 	// 대표전화2 (ex. 02-1588-1588) 체크...
        return true;
    } else if (rep3_pattern.test(textVal)) {	// 대표전화3 (ex. 070-1234-5678) 체크....
        return true;
    }

    return false;
}

//정수검사
function gfn_chkInt(str) {
    var special_pattern = /^[0-9]{1,}$/;
    if (special_pattern.test(str) == true) {
        return true;
    } else {
        return false;
    }
}

// 사업자 등록번호 검사
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

//null인지 검사 (name으로)
function gfn_chkTextNullByName(name, text) {
    //return gfn_chkTextNullByNameMsg(name, text + "은(/는) 필수입력값입니다.");
    if ($("[name='" + name + "']").val() == "") {
        alert(text + "은(/는) 필수입력값입니다.");
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
}

//null인지 검사 (name으로)
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
        alert(text + "은(/는) 필수입력값입니다.");
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
        alert(text + "을(/를) 선택해 주세요.");
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
}

//이메일 검사
function gfn_chkTextEmailByName(name, text) {
    var val = $("[name='" + name + "']").val();
    if(val==''){
        return true;
    }

    if (gfn_chkEmail(val) == false) {
        alert(text + " 형식이 올바르지 않습니다.");
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
}

//공백검사
function gfn_chkTextSpaceByName(name, text) {
    var val = $("[name='" + name + "']").val();
    if(val==''){
        return true;
    }

    if (gfn_chkSpace(val) == false) {
        alert(text + "은(/는) 공백을 사용할 수 없습니다.");
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
}

//전화번호 검사
function gfn_chkTextTelByName(name, text) {
    return gfn_chkTextTelByNameMsg(name, text + "은(/는) 전화번호 형식을 확인해 주세요.\n(ex. 000-0000-0000)");
    /*
    var val = $("[name='" + name + "']").val();
    if(val==''){
        return true;
    }

    if (gfn_chkTel(val) == false) {
        alert(text + "은(/는) 전화번호 형식이 안입니다\n(000-0000-0000 형식)");
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

//정수 검사
function gfn_chkTextIntByName(name, text) {
    var val = $("[name='" + name + "']").val();
    if(val==''){
        return true;
    }

    if (gfn_chkInt(val) == false) {
        alert(text + "은(/는) 숫자만 입력 하세요.");
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
}


function gfn_chkCheckBoxByName(name, text) {
    if ($("[name='" + name + "']").is(":checked") == false) {
        alert(text + "은(/는) 필수사항 입니다.");
        $("[name='" + name + "']").focus();
        return false;
    }
    return true;
}

function gfn_chkCheckBoxById(id, text) {
    return gfn_chkCheckBoxByIdMsg(id, text + "은(/는) 필수사항 입니다.");
}

function gfn_chkCheckBoxByIdMsg(id, msg) {
    if ($("#" + id ).is(":checked") == false) {
        alert(msg);
        $("#" + id ).focus();
        return false;
    }
    return true;
}

//전화번호 하이픈 추가 - 
function gfn_telHyphen(x) {
    return x != '' && x != null ? x.toString().replace(/(^02|^0505|^1[0-9]{3}|^0[0-9]{2})([0-9]+)?([0-9]{4})$/,"$1-$2-$3").replace("--", "-") : '';
}

//전화번호 하이픈 동적추가 - 
function gfn_telDynamicHyphen(th) {
    var telVal = $(th).val().replace(/[^0-9]/g, "").replace(/(^02|^0505|^1[0-9]{3}|^0[0-9]{2})([0-9]+)?([0-9]{4})$/,"$1-$2-$3").replace("--", "-");
    if (telVal.length > 7 && telVal.indexOf("-") == -1) {
        telVal = "";
    }
    $(th).val( telVal );
}

//숫자 외 제거 - 
function gfn_onlyNumber(str) {
    var regex = /[^0-9]/g;
    return str.replace(regex, "");
}

//사업자 등록번호 하이픈 추가
function gfn_bsnmRegNoHyphen(x) {
    return x != '' && x != null ? x.toString().replace(/([0-9]{3})([0-9]+)?([0-9]{5})$/,"$1-$2-$3").replace("--", "-") : '';
}

//사업자 등록번호 하이픈 추가
function gfn_bsnmRegNoDynamicHyphen(th) {
    $(th).val( $(th).val().replace(/[^0-9]/g, "").replace(/([0-9]{3})([0-9]+)?([0-9]{5})$/,"$1-$2-$3").replace("--", "-") );
}


//세자리 마다 콤마
function gfn_commas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}


//이미지 확장자인지 검사
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
            //없으면 통과
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

// 비밀번호 규칙 : 영문 소문자, 숫자, 특수문자 조합 6자리 이상
function gfn_checkPassword2(str) {
    var reg = /^(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+{}\[\]:;<>,.?~\\/-]).{6,}$/;

    if (gfn_chkSpace(str) == false) {
        alert("비밀번호에 공백을 사용할 수 없습니다.");
        return false;
    }

    if (reg.test(str) == false) {
        alert("비밀번호는 영문 소문자, 숫자, 특수문자 조합으로 6자리 이상이어야 합니다.");
        return false;
    }

    return true;
}


// 숫자 외 제거
function gfn_removeExceptNumber(obj) {
    var i = obj;
    var startPosition = i.value.length - i.selectionEnd;
    i.value = i.value.replace(/^\D+/g, '');
    var len = Math.max(i.value.length - startPosition, 0);
    return i.setSelectionRange(len, len);
}

// 숫자 외 제거 및 3자리수 동적 콤마 - 
function gfn_inputNumberFormat(obj) {
    var i = obj;

    if(i.value == "0") return i.value = 0;
    if(i.value == "00") return i.value = 0;

    var startPosition = i.value.length - i.selectionEnd;
    i.value = i.value.replace(/^0+|\D+/g, '').replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
    var len = Math.max(i.value.length - startPosition, 0);
    return i.setSelectionRange(len, len);

}


//날짜(8자리) 년-월-일로 변경
function gfn_dateAddDash(date) {
    if (date == undefined || date == null || date == "") {	// 값이 없으면...
        return "";
    } else {
        return date.substr(0, 4) + "-" + date.substr(4, 2) + "-" + date.substr(6, 2);
    }
}


/**
 * JsGrid 날짜 포맷 (20220601 -> 2022-06-01)
 * 사용법:
 *  1. jquery 초기화 함수에서 이 함수 호출
 *  2. JsGrid fields에서 [type:"date"] 으로 입력
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
 * JsGrid 전화번호 포맷 (01012341234 -> 010-1234-1234)
 * 사용법:
 *  1. jquery 초기화 함수에서 이 함수 호출
 *  2. JsGrid fields에서 [type:"phone"] 으로 입력
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
 * 전화번호 포맷
 * 기존 전화번호 값을 '-'이 붙은 형태로 변경
 * 모든 동일한 셀렉터 일괄 적용 하도록 처리
 * 사용전 libphonenumber-js.min.js 추가해 주세요
 */
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
 * 상품관리 > 추천상품 등록|해제
 * 상품 목록에서 추천상품 여부 checkbox 선택으로 등록|해제 설정
 */
/*
function gfn_changeRcmmGoodsStatus(chk, goodsUuid, goodsSe){

    var isChecked = $(chk).is(':checked');
    var goodsRcmmYn = (isChecked) ? "Y":"N";

    console.log(goodsSe);

    // 추천 상품 상태변경
    var url = CONTEXT_PATH + "/toy/goods/updateGoodsRcmmStatusAjax.doax";

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

            var statusMsg = (goodsRcmmYn == "Y") ? "으로 설정" : " 설정 해제";
            alert("추천상품"+statusMsg+" 되었습니다");
        },
        error: fn_AjaxError
    });
}
*/


/****************************
 * 자동 로그인
 */
function fn_autoLogin(userUid){
    console.log("--fn_autoLogin call");

    if(userUid!=''){
        //로그인 되있으면

        console.log("--fn_autoLogin 이미로그인");
        return false;
    }

    if(localStorage.getItem("autoLogin_yn") != "Y"){
        console.log("--fn_autoLogin 자동로그인 체크 안됨");

        return false;
    }



    var mberEmailAdres = localStorage.getItem("autoLogin_email");
    var scrNoEncpt = encodeURI( localStorage.getItem("autoLogin_pw") );

    var tknCertkey = "";
    var cntnStorageSe = "pc";


    var hw = deviceOSCheck();
    if (hw == "and") {
        //tknCertkey = android.getTokenId();
        cntnStorageSe = "and";
    }else if (hw == "ios") {
        //처리 할꺼!

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
            //로그인 성공이면
            if(data.result == "Y") {

                //localStorage.setItem("autoLogin_yn", "Y");
                //localStorage.setItem("autoLogin_email", data.mberEmlAdresEncpt);
                //localStorage.setItem("autoLogin_pw", data.pwdEncpt);

                location.href = $("#returnURL").val();



                //location.href = data.returnURL;
                //location.href = $("#returnURL").val();
                //console.log("자동 로그인 성공");
                window.location.reload();


            }
            //로그인 실패이면
            else {
                //alert(data.message);
                console.log("자동 로그인 실패");
            }
        },
        error: function(request, status, error, loginPath) {
            if (request.status == "510") {
                alert(msgMap.get('string.errorOccurredLoginSessionExpired'));	/*에러가 발생했습니다. 로그인세션이 만료되었습니다.*/
                location.replace(loginPath);
                return false;
            } else if (request.status == "403") {
                alert(msgMap.get('string.dontHaveAccess'));	/*접근 권한이 없습니다.*/
                return false;
            }

            alert(msgMap.get('string.systemErrorPleaseContactAdministrator'));	/*시스템 오류\n관리자에게 문의해주세요.*/
        }
    });
}


function fn_chkOS(){

    var hw = "";
    var varUA = navigator.userAgent.toLowerCase(); //userAgent 값 얻기

    /*
    if("${Constant.APP_MODE}" == "test"){
        //테스트 모드
        hw = "";

    }else{
	*/

    if ( varUA.indexOf('android') > -1) {
        //안드로이드
        hw = "and";
    } else if ( varUA.indexOf("iphone") > -1||varUA.indexOf("ipad") > -1||varUA.indexOf("ipod") > -1 ) {
        //IOS
        hw = "ios";
    } else {
        //아이폰, 안드로이드 외
        hw = "";
    }

    //}

    return hw;
}


