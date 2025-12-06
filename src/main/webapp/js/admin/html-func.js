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
            $(obj + ' .modal-content').css('width', modalWidth);
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

	// 닫기버튼 클릭 시
	$(obj + ' .remove').on('click', function() {
		modalJsRemove(obj)
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
}

function modalJsRemove(obj) {
	// 스크롤 Reset
	$('body').css('overflow', 'visible');

	$(obj).remove();
}

function orderPreviewShow() {
  modalShow('#order-preview');
  // Swiper
  var swiper = new Swiper(".preview-slider", {
	pagination: {
	  el: ".swiper-pagination",
	  type: "fraction",
	},
	navigation: {
	  nextEl: ".swiper-button-next",
	  prevEl: ".swiper-button-prev",
	},
  });
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








