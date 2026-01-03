<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2025-12-16
  Time: 오전 12:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@ include file="/WEB-INF/jsp/toy/admin/include/top.jsp" %>

<!-- jsgrid -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/com/jsgrid/jsgrid-theme.css" />
<script src="${pageContext.request.contextPath}/js/com/jsgrid/jsgrid.js"></script>

<script src="${pageContext.request.contextPath}/js/common.js?jsCssVer=${jsCssVer}" ></script>
<script type="text/javascript">
    $(function (){
        fn_setAcssLogJsGrid();

        // 기존 datepicker 무력화
        $(".datepicker").datepicker("destroy");

        // 검색 시작일
        $( "#sStartDt" ).datepicker({
            maxDate: 0,
            showOn: "both",
            buttonImage: "/images/com/jquery/calendar.png",
            buttonImageOnly: true,
            onSelect: function(selectDate) {
                $("#sEndDt").datepicker("option", "minDate", selectDate);
            }
        });

        // 검색 종료일
        $( "#sEndDt" ).datepicker({
            showOn: "both",
            maxDate: 0,
            buttonImage: "/images/com/jquery/calendar.png",
            buttonImageOnly: true
        });
    });


    //목록 페이지 조회
    function fn_Search() {

        $("#AccessLogJsGrid").jsGrid({
            pageIndex: 1
        });

        fn_setAcssLogJsGrid();
    }

    //접속 허용 IP 목록 조회
    function fn_setAcssLogJsGrid(){

        // alert('test');

        $("#AccessLogJsGrid").jsGrid({
            width: "100%",
            height: "auto",

            filtering: false, // 검색 필터
            autoload: true,
            sorting: false, // 정렬
            editing: false, // 그리드내 수정
            inserting: false, // 그리드내 작성

            // 페이징
            paging: true,
            pageLoading: true,
            pageSize: 10,
            pageButtonCount: 10,
            pagerContainer: "#externalPager_acssLog",

            controller: {
                // 그리드데이터 ajax 호출
                loadData: function(filter) {
                    var d = $.Deferred();
                    $("#pageIndex").val(filter['pageIndex']);
                    $.ajax({
                        url: "/toy/admin/sys/accesslog/selectAdmAcssLogList.doax",
                        data: $("#searchForm").serialize(),
                        dataType: "json"
                    }).done(function(response) {
                        if(response.data.length == 0) {
                            $("#" + jsGridId).jsGrid({
                                autoload: false,
                                noDataContent: '<div class="not-content"><span class="icon"></span><div class="text">검색 결과가 없습니다.</div></div>',
                            });
                        }

                        d.resolve(response);
                        $("#itemsCount").text(gfn_commas(response.itemsCount));
                    });
                    return d.promise();
                },
            },

            rowClick: function(args) {

            },


            fields: [
                { name:"mngrUid"		, title:"관리자ID"	, type:"text"	, width:80	, align: "center" },
                { name:"accessIp"  	, title:"접속IP"	, type:"text"	, width:100	, align: "center"},
                { name:"reqUri", title:"Request URI", type:"text"   , width:250	, align: "left"  , sorting: false },
                { name:"actionDesc"  , title:"액션 로그 설명"  , type:"text"	, width:250 , align: "left", inserting: false, editing: false, filtering: false },
                { name:"regDt" , title:"접속 시간" , type:"text"	, width:140 , align: "center", inserting: false, editing: false, filtering: false},
                { name:"memo" , title:"메모" , type:"text"	, width:80 , align: "left", inserting: false, editing: false, filtering: false },
            ]
        });

    }
</script>
</head>

<body>
<div id="wrapper">

    <%-- Common Header/Side Menue --%>
    <jsp:include page="/WEB-INF/jsp/toy/admin/include/adminHeader.jsp" flush="false">
        <jsp:param value="system_menu" name="admin_menu"/>
    </jsp:include>

    <main id="main">
        <section class="contents-wrap">
            <div class="title-area">
                <h2 class="depth-title">접속이력조회</h2>
            </div>

            <!-- Change Contents (↓↓↓↓) -->
            <section class="search-form">
                <div class="form-inline2">
                    <form name="searchForm" id="searchForm" method="get" action="${curURL}" onsubmit="fn_Search(); return false;">
                        <input type="hidden" id="pageIndex" name="pageIndex" value="${searchVO.pageIndex}"/>
                        <div class="form-group">
                            <label class="tit">관리자 아이디</label>
                            <input type="text" placeholder="관리자 아이디" class="w130" id="sMngrUid" name="sMngrUid" value="${searchVO.sMngrUid}">
                        </div>
                        <div class="form-group">
                            <label class="tit">검색 기간</label>
                            <span class="date-wrap w142"><input class="datepicker" type="text" id="sStartDt" name="sStartDt" value="${searchVO.sStartDt}"></span>
                            <span>~</span>
                            <span class="date-wrap w142"><input class="datepicker" type="text" id="sEndDt" name="sEndDt" value="${searchVO.sEndDt}"></span>
                        </div>
                        <div class="form-group">
                            <label class="tit">접속 IP</label>
                            <input type="text" placeholder="접속 IP" class="w150" id="sAccessIp" name="sAccessIp" value="${searchVO.sAccessIp}">
                        </div>
                        <div class="form-group">
                            <label class="tit">액션 URI</label>
                            <input type="text" placeholder="액션 URI" class="w150" id="sReqUri" name="sReqUri" value="${searchVO.sReqUri}">
                        </div>
                        <div class="form-group">
                            <label class="tit">액션 내용</label>
                            <input type="text" placeholder="액션 설명내용" class="w200" id="sActionDesc" name="sActionDesc" value="${searchVO.sActionDesc}">
                        </div>
                        <button type="submit" class="btn block search"><i class="material-icons-outlined">search</i> 검 색</button>
                    </form>
                </div>
            </section>

            <section class="content-box">
                <h3 class="title2">검색결과 <small>[총 <strong id="itemsCount" class="text-blue">0</strong>건]</small></h3>

                <!-- 내용 -->
                <div class="table-content-wrapper">
                    <div id="AccessLogJsGrid" ></div>
                    <div id="externalPager_acssLog" style="text-align:center;"></div>
                </div>
                <!-- //내용 -->

            </section> <!-- //content-box -->

            <!-- //Change Contents (↑↑콘텐츠 변경↑↑) -->

        </section> <!-- //contents-wrap -->
    </main>
</div>
</body>
</html>
