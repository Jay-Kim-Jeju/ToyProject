/* ====================================================================================================================
* Browser Start
* ====================================================================================================================*/
//HTML5 표준이 정해져 있다.
var winWidth = window.innerWidth,                                       //창가로 사이즈
    winHeight = window.innerHeight,                                     //창세로 사이즈
    bodyScoll = $(window).scrollTop();                                  //스크롤 위치



//브라우저 버전 체크
var IEVersionCheck = function() {

    var word,
        version = "N/A",
        agent = navigator.userAgent.toLowerCase(),
        name = navigator.appName;

    // IE old version ( IE 10 or Lower )
    if ( name == "Microsoft Internet Explorer" ) word = "msie ";

    else {
        // IE 11
        if ( agent.search("trident") > -1 ) word = "trident/.*rv:";

        // IE 12  ( Microsoft Edge )
        else if ( agent.search("edge/") > -1 ) word = "edge/";
    }

    var reg = new RegExp( word + "([0-9]{1,})(\\.{0,}[0-9]{0,1})" );
    if (  reg.exec( agent ) != null  )
        version = RegExp.$1 + RegExp.$2;

    return version;
};


//Side Navi
function SideMenu() {
	$('#side-wrapper.open .depth2 > li > a.drop').click(function(event) {
		var speed = 400;
		if ($(this).next('#side-wrapper.open .depth3').css('display')==='none') {
			$('#side-wrapper.open .depth3').slideUp(speed);
			$(this).next('#side-wrapper.open .depth3').slideDown(speed);
			$('#side-wrapper.open .depth2 > li > a.drop').removeClass('active');
			$(this).addClass('active');
		}
		else {
			$('#side-wrapper.open .depth3').slideUp(speed);
			$(this).removeClass('active');
		}
	});
}


//Side Navi 접기
function SideOpen() {
	$('#side-wrapper #sideGnb-btn').click(function(event) {

		var speed = 400; // 속도

		if ($('#side-wrapper').css('width')==='200px') {

			$('#side-wrapper').animate( {'width' : '80px'}, speed);
			$('#side-wrapper').removeClass('open');
			$('#side-wrapper').addClass('close');
		}
		else {
			$('#side-wrapper').animate( {'width' : '200px'}, speed);
			$('#side-wrapper').removeClass('close');
			$('#side-wrapper').addClass('open');
		}
	});
};

/*
** datepicker의 기간 날짜 출력 (시작일 ~ 종료일)
* startId : 시작일 ID
* opStr : 시작일 옵션명
* opVal : 시작일 옵션 값
* endId : 종료일 ID
* opEndStr : 종료일 옵션명
* opEndVal : 종료일 옵션값
*/
function fn_periodDatepicker(startId, opStr, opVal, endId, opEndStr, opEndVal) {
	// 시작일
	$("#" + startId).datepicker("option", opStr, opVal);
	$("#" + startId).datepicker("option", "onSelect", function (selectDate) {
		$("#" + endId).datepicker("option", "minDate", selectDate);
	});

	// 종료일
	if (opEndStr != undefined) {
		$("#" + endId).datepicker("option", opEndStr, opEndVal);
	}
}




/* ====================================================================================================================
* Document Ready
* ====================================================================================================================*/
$(function () {

    //IE하위 브라우저시 실행
    if(IEVersionCheck() == 10 || IEVersionCheck() == 9 || IEVersionCheck() == 8 || IEVersionCheck() == 7 || IEVersionCheck() == 5) {
        var error_browser = '';
        error_browser += '<div class="not-browser">';
        error_browser += '  <div class="warning"><span></span></div>';
        error_browser += '  <h1 class="error-title">현재 사용중인 브라우저는 지원되지 않습니다.<br><span class="sub">(In this broser isn&#39;t supported.)</span></h1>';
        error_browser += '  <p class="error-text">Microsoft의 지원 종료 된 브라우저를 사용하고 있습니다.</p>';
        error_browser += '  <p class="error-text">최신 버전의 Internet Explorer, Chroem, Safari, Firefox, Microsoft Edge<br>브라우저를 이용해 주세요.</p>';
        error_browser += '</div>';

        $('body').html(error_browser);
    }
    else {
    	//Common
            SideMenu();			//Side Navi
            SideOpen();			//Side Navi 접기

           $( ".datepicker" ).datepicker({
        		showOn: "both",
        		buttonImage: "/images/com/jquery/calendar.png",
        		buttonImageOnly: true
        	});
	}

});

