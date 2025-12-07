// 공통변수
var deviceWidth = $(window).outerWidth();
var deviceHeight = $(window).outerHeight();


function modalHideForCommentEdit() {
	$('body').css('overflow', '');

	$('#commentEdit .modal-bg').fadeOut();
	$('#commentEdit').hide();

	// Hide the editSection element
	$("#editSection").hide();
}


function modalShowWithReviewSn(modalId, reviewSn) {

	//console.log("modalShowWithReviewSn : Review Sn received:", reviewSn);

	var modalWidth = $(modalId + ' .modal-content').data('width');
	var modalScroll = $(modalId).data('scroll');

	if ($(modalId).is(":hidden")) {
		// 스크롤 Lock
		$('body').css('overflow', 'hidden');

		// Modal Show
		$(modalId + ' .modal-bg').fadeIn();
		$(modalId).show();

		// Data Width 있는경우 (모달 가로 크기)
		if (modalWidth > 0) {
			$(modalId + ' .modal-content').css('width', modalWidth);
		}

		// Data Scroll 있는경우 (모달 스크롤)
		if (modalScroll == true) {
			$(modalId).addClass('scroll');
		}

		// Set the value of the input field
		$(modalId + ' #editReviewSn').val(reviewSn);
		$(modalId + ' #deleteReviewSn').val(reviewSn);
		$(modalId + ' #reportReviewSn').val(reviewSn);



		// Animation
		setTimeout(function () {
			$(modalId + ' .modal-content').addClass('transition');
		}, 100);
	} else {
		modalHide(modalId);
	}

	// 닫기버튼 클릭 시
	$(modalId + ' .close').on('click', function () {
		modalHide(modalId);
	});

	// 닫기버튼 클릭 시
	$(modalId + ' .remove').on('click', function () {
		modalJsRemove(modalId);
	});
}



/* ====================================================================================================================
* Html Style (layer popup 등)
* ====================================================================================================================*/
// Layer popup (타켓지정)
function modalShow(obj) {
    var modalWidth = $(obj + ' .modal-content').data('width');
    var modalScroll = $(obj).data('scroll');

	if($(obj).is(":hidden")) {
        // 스크롤 Lock
        $('body').css('overflow', 'hidden');
        
        // Modal Show
        $(obj + ' .modal-bg').fadeIn();
        $(obj).show();

        // Data Width 있는경우 (모달 가로 크기)
        if(modalWidth > 0) {
            $(obj + ' .modal-content').css('max-width', modalWidth);
        }

        // Data Scroll 있는경우 (모달 스크롤)
        if(modalScroll == true) {
            $(obj).addClass('scroll')
        }

        // Animation
        setTimeout(function() {
            $(obj + ' .modal-content').addClass('transition');
        }, 100);
	}
	else {
		modalHide(obj)
	}

	// 닫기버튼 클릭 시
	$(obj + ' .close').on('click', function() {
		modalHide(obj)
    });
}

function modalHide(obj) {
    // 스크롤 Reset
    $('body').css('overflow', 'visible');
        
    // Modal Hide
    $(obj + ' .modal-bg').hide();
    $(obj).hide();
    
    // Animation Reset
    $('.modal-content').removeClass('transition');


	console.log("start hide")

}

function modalRemove(obj, remove) {
    // 스크롤 Reset
    $('body').css('overflow', 'visible');
        
    // Modal Hide
    $(obj + ' .modal-bg').hide();
    $(obj).hide();

	//Content Remove
	$(remove).remove();
    
    // Animation Reset
    $('.modal-content').removeClass('transition');
}





// Default Accordion
function accordion(obj) {
	var speed = 350;															//animation speed

	$(obj + ' dt .open').on('click', function() {
		if ($(this).parents('dt').next('dd').is(":hidden")) {
			$(this).parents('dt').next('dd').slideDown(speed);					//해당 dd 보이기
			$(this).addClass('active');											//클래스 추가
		}
		else {
			$(this).parents('dt').next('dd').slideUp(speed);					//해당 dd 숨기기
			$(this).removeClass('active');										//클래스 제거
		}

		return false;
	});
}



// Group Accordion
function groupAccordion() {
	var speed = 350;

	$('.ac-head').on('click', function() {
		var target = $(this).next('.ac-body');

		if (target.is(":hidden")) {
			target.slideDown(speed);

			if ($(this).hasClass("off")) {
				$(this).removeClass('off');
			}
			$(this).removeClass('active');
		}
		else {
			target.slideUp(speed);
			$(this).addClass('active');
		}
	});
}



// Toggle Event
function toggleEvent(id, btn) {
	const target = $(id);
	const speed = 300;

	if(target.is(":hidden")) {
		target.slideDown(speed);
		$(btn).addClass('active');
	} else {
		target.slideUp(speed);
		$(btn).removeClass('active');
	}
}



// Product Toggle(상품선택)
function productToggle(i) {
	var speed = 350;
	
	if(i == 'off') {
		$('.tog-body').slideUp();
		$('.tog-head').removeClass('active');
	} else {
		$('.tog-head').on('click', function() {
			var target = $(this).next('.tog-body');
	
			if (target.is(":hidden")) {
				target.slideDown(speed);
				$(this).addClass('active');
			}
			else {
				target.slideUp(speed);
				$(this).removeClass('active');
			}
		});
	}
}



// Tab panel
function tabPanel(params) {
	var defaults = {
		container: "#tabs", 	//item wrap id
		firstItem: "#tab1", 	//first show item
		active: 0 				//ul li > menu on
	};
	for (var def in defaults) { //array object 확인
		if (typeof params[def] === 'undefined') {
			params[def] = defaults[def];
		}
		else if (typeof params[def] === 'object') {
			for (var deepDef in defaults[def]) {
				if (typeof params[def][deepDef] === 'undefined') {
					params[def][deepDef] = defaults[def][deepDef];
				}
			}
		}
	};

	//변수선언
	var item = params.container+' ';
	var firstItem = params.firstItem;
	var active = params.active;


    $(item+'.menu a').removeClass('active');
	$(item + '.tab-panel').hide();								    //전체 콘텐츠 hide
	$(firstItem).show();										    //해당 콘텐츠 show
    $(item + '.menu li').eq(active).find('a').addClass('active');	//해당 메뉴 active

    $(item+'.menu a').click(function() {
		var show = $(this).attr('href');
		
        $(item+'.menu a').removeClass('active');
		$(this).addClass('active');

		$(item + ' .tab-panel').hide();
		$(show).show();

		return false;
	});
}



// item show
function itemShow(obj) {
	$(obj).show();
}

// item hide
function itemHide(obj) {
	$(obj).hide();
}

// Item show and hide
function ShowAndHide(show, hide) {
	$(hide).hide();
	$(show).show();
}

// item hide
function slideHide(obj) {
	$(obj).slideUp();
}



// Nav 가로 스크롤
function widAutoScroll(obj) {
	var winWidth = $(window).width(),
		menuWidth = 0;

	$(obj+' li').map(function() {
		menuWidth = menuWidth + $(this).outerWidth(true);			//전체 메뉴 길이 체크
	});

	if(winWidth < menuWidth) {										//윈도우 창 크기보다 큰경우 실행
		$(obj).css('width', (menuWidth+60));						//메뉴 가로 사이즈 입력
	}
    else {
        $(obj).css('width', 'auto');                                //창크기가 클경우 가로 오토
    }
}



// 클립보드 URL 복사
function urlClipBoard() {
    $('#urlClipBoard').val();
    $('#urlClipBoard').select();
    try {
        var successful = document.execCommand('copy');
        alert("클립보드에 주소가 복사되었습니다.\nCtrl + V 로 붙여넣기 하세요.");
		$('.tool-tip').hide();
    } catch (err) {
        alert('이 브라우저는 지원하지 않습니다.');
    }
}


// 텍스트 복사
function txtCopy(name) {
	const copyText = document.getElementById(name).innerText;

	// 임시의 textarea 생성
	const $textarea = document.createElement("textarea");

	// body 요소에 존재해야 복사가 진행됨
	document.body.appendChild($textarea);
	
	// 복사할 특정 텍스트를 임시의 textarea에 넣어주고 모두 셀렉션 상태
	$textarea.value = copyText;
	$textarea.select();

	try {
        // 복사 후 textarea 지우기
		document.execCommand('copy');
		document.body.removeChild($textarea);
		alert("클립보드에 복사되었습니다.");
    } catch (err) {
        alert('이 브라우저는 지원하지 않습니다.');
    }
}



// Target Scroll Animation
function targetScrollAni(id, speed) {
    var scrollPosition = $(id).offset().top;

    $("html, body").animate({
        scrollTop: scrollPosition
    }, speed);

    return false;
}



// Q&A 댓글 Toggle
function replyToggle(i) {
	const target = $(i).closest('li').find('.reply-wrap');
	const speed = 300;

	if(target.is(":hidden")) {
		target.slideDown(speed);
	} else {
		target.slideUp(speed);
	}
}



// MyPage 리뷰 Toggle
function reviewToggle(i) {
	const target = $(i).closest('li').find('.comment-wrap');
	const speed = 300;

	if(target.is(":hidden")) {
		target.slideDown(speed);
	} else {
		target.slideUp(speed);
	}
}



// 새창열기
function windowBlankOpen(url) {
	var openWindow = window.open("about:blank");
	openWindow.location.href = url;
}



// 공지사항 더보기(모달)
function noticeDetailModal(obj) {
	// 값 가져오기
	const data = $(obj).html();

	// 모달
	let innerHtml = "";
		innerHtml += "<div id='noticeDetailModal' class='modal'>";
		innerHtml += "  <div class='modal-bg'></div>";
		innerHtml += "  <div class='modal-content'>";
		innerHtml += "      <div class='content'>";
		innerHtml += "          <div class='main'>";
		innerHtml += "              <button type='button' class='close2' onclick='noticeDetailRemoveModal()'><strong>닫기</strong></button>";
		innerHtml += "              <div class='wrapper'>";
		innerHtml += "                  <div class='notice-modal'>"+data+"</div>";
		innerHtml += "              </div>";
		innerHtml += "          </div>";
		innerHtml += "      </div>";
		innerHtml += "  </div>";
		innerHtml += "</div>";

	$('body').append(innerHtml);

	modalShow('#noticeDetailModal');
}

// 공지사항 더보기(모달) 삭제
function noticeDetailRemoveModal(obj) {
	modalHide('#noticeDetailModal');
	$('#noticeDetailModal').remove();
}



// Main popup
function getCookie(cname) {
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for(var i=0; i<ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1);
		if (c.indexOf(name) != -1) return c.substring(name.length,c.length);
	}

	return "";
}

function setCookie(cname, cvalue, exdays) {
	var d = new Date();
	d.setTime(d.getTime() + (exdays*24*60*60*1000));

	var expires = "expires="+d.toUTCString();
	document.cookie = cname + "=" + cvalue + "; " + expires;
}
