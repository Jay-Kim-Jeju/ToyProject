/* ====================================================================================================================
* Browser Start
* ====================================================================================================================*/
// HTML5 표준이 정해져 있다.
var winWidth = window.innerWidth,                                       //창가로 사이즈
    winHeight = window.innerHeight,                                     //창세로 사이즈
    bodyScoll = $(window).scrollTop();                                  //스크롤 위치



// 브라우저 버전 체크
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





/* ====================================================================================================================
* Var, Let, Const
* ====================================================================================================================*/
// Device Width Size
var deviceWidth = $(window).outerWidth();
var deviceHeight = $(window).outerHeight();





/* ====================================================================================================================
* Function
* ====================================================================================================================*/
/* 모바일 / PC 접속체크 */
function deviceFormCheck() {
    var mobileKeyWords = new Array('Android', 'iPhone', 'iPod', 'BlackBerry', 'Windows CE', 'SAMSUNG', 'LG', 'MOT', 'SonyEricsson');
    for (var info in mobileKeyWords) {
        if (navigator.userAgent.match(mobileKeyWords[info]) != null) {
            return "mobile";
        }
    }
    return "pc";
}


// 모바일 기기 체크
function deviceOSCheck() {
    var hw = "";
    var varUA = navigator.userAgent.toLowerCase(); //userAgent 값 얻기

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

    return hw;
}



// Top Button
function goTopBtn() {
    $('html, body').animate( { scrollTop : 0 }, 500);
}



// Side Menu
function sideMenuShow() {
    $('.side-btn').on('click', function() {
        $('.side-menu').addClass('open');
    });
}

function sideMenuHide() {
    $('.side-menu').removeClass('open');
}



// Gnb Menu
function GnbMenuStyle() {
    $('.gnb-menu nav > ul > li').each(function () {
        const count = $(this).find('a').length;
        if(count > 1) {
            $(this).addClass('drop');
        }
    });
}



// Fixed Layout
function FixedLayout() {
    $('#header').addClass('fixed');
    $('#footer').addClass('fixed');
}







/* ====================================================================================================================
* Scroll Event
* ====================================================================================================================*/
var didScroll,
    start = 200;														//스크롤 시작 이벤트 위치

$(window).scroll(function(event) {
    didScroll = true;													//스크롤 이벤트 부하에 따른 setInterval 사용
});



// Top Button fadeIn/Out
function scrollTopBtn() {
    var scrollHeight2 = $(window).scrollTop();
    var obj = '.go-top';
    var showArray = ['#main'];

    showArray.forEach((name)=> {
        if($(name).length == 1) {
            if(scrollHeight2 > 350) {
                $(obj).fadeIn('500');
            }
            else {
                $(obj).fadeOut('100');
            }
        }
    })
}





/* ====================================================================================================================
* SetInterval
* ====================================================================================================================*/
setInterval(function() {
    if (didScroll) {

        // 상단으로 가기
        scrollTopBtn();

        didScroll = false;
    }
}, 10);




/* ====================================================================================================================
* Document Ready
* ====================================================================================================================*/
$(document).ready(function(){

    //IE하위 브라우저시 실행
    if(IEVersionCheck() <= 10) {
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
        // Common
        $("a[href^='#']").click(function (event) {
            event.preventDefault();
            var target = $(this.hash);
            $('html, body').animate({ scrollTop: target.offset().top - 80 }, 500);
        });

        // Side Menu
        sideMenuShow();
        GnbMenuStyle();

        // Main
        if($('.main-wrapper').length == 1) {
            FixedLayout();

            // 메인팝업
            if($('.main-popup').length >= 1) {                                                      //main layer popup
                cookiedata = document.cookie;                                                       //쿠키 조회
                var cookieArray = cookiedata.split(';');                                            //쿠키 배열 만들기
                var cookieCount = cookieArray.length;                                               //쿠키 개수

                $(".main-popup").show();                                                            //팝업 show

                for (var i = 0; i < cookieCount; i++) {
                    //console.log('cookie[' + i + '] = ' + cookieArray[i]);

                    if(i >= 1) {
                        cookieCut = cookieArray[i].substring(1, cookieArray[i].length-2);           //뒤에 문자열 자르기 (=y) + 앞에 여백포함
                    } else {
                        cookieCut = cookieArray[i].substring(0, cookieArray[i].length-2);           //뒤에 문자열 자르기 (=y)
                    }

                    if(cookieArray[i]) {
                        $("input[name='"+cookieCut+"']").parents('.main-popup').hide();             //쿠키 있을 시 팝업 숨김
                    }
                }

                $(".main-popup .close").click(function(){
                    var cookieName = $(this).parents('.today-close').find('input').attr('name');    //해당 input name 가져오기

                    if($("input[name='"+cookieName+"']").is(":checked") == true) {                  //오늘하루그만보기 체크시
                        setCookie(cookieName, "Y", 1);                                              //쿠키생성
                    }

                    $(this).parents('.main-popup').hide();                                          //해당 팝업 숨김
                });

            }

        } else {
            // 서브 Footer 배경
            const bgData = [
                { name: '.title-card.color3', addName: 'bg3'},  //오늘제주
                { name: '.item-list.news', addName: 'bg4'}, //제주소식
                { name: '.title-card.color4', addName: 'bg4'},
                { name: '.title-card.color5', addName: 'bg5'}, //추천장소
                { name: '.login', addName: 'bg6'}, //로그인
                { name: '.member', addName: 'bg6'}, //회원
                { name: '.mypage', addName: 'bg6'}, //마이페이지
            ];

            bgData.forEach((obj)=> {
                if($(obj.name).length == 1) {
                    $('#footer').addClass(obj.addName);
                }
            });
        }
    }
    
});





/* ====================================================================================================================
* Window Road
* ====================================================================================================================*/
/*
$(window).on('load', function() {
    
});
*/





/* ====================================================================================================================
* Window Resize
* ====================================================================================================================*/
/*
$(window).resize(function() {
	if(this.resizeTO) {
		clearTimeout(this.resizeTO);
	}
	this.resizeTO = setTimeout(function() {
		$(this).trigger('resizeEnd');
	}, 250);
})
$(window).on('resizeEnd', function() {

    // Common

});
*/
