<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2025-12-23
  Time: 오후 2:52
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
    $(function () {
        fn_setJsGrid();
    });

    // Listing GroupCodes
    function fn_setJsGrid(){
        $("#jsGrid").jsGrid({
            width: "750px",
            height: "auto",

            filtering: true, // 검색 필터
            autoload: true,
            sorting: false, // 정렬
            editing: true, // 그리드내 수정
            inserting: true, // 그리드내 작성

            // 페이징
            paging: true,
            pageLoading: true,
            pageSize: 10,
            pageButtonCount: 10,
            pagerContainer: "#externalPager",

            controller: {
                // Call ajax for bringing grid Data
                loadData: function(filter) {
                    var d = $.Deferred();
                    $("#pageIndex").val(filter['pageIndex']);
                    $.ajax({
                        type: "POST",
                        url: "${CONTEXT_PATH }/toy/admin/sys/code/grp/list.doax",
                        data: filter,
                        beforeSend: function (xhr) {
                            // Your custom logic only (loading spinner etc.)
                        },
                        dataType: "json"
                    }).done(function(response) {
                        // If auth failed, server returns redirectUrl.
                        if (response && response.redirectUrl) {
                            window.location.href = response.redirectUrl;
                            return;
                        }
                        if(response.data.length == 0) {
                            $("#jsGrid").jsGrid({
                                autoload: false,
                                noDataContent: '<div class="not-content"><span class="icon"></span><div class="text">검색 결과가 없습니다.</div></div>',
                            });

                            $('.jsgrid-filter-row').find('td').eq(0).find('input').val(filter.groupCd);
                            $('.jsgrid-filter-row').find('td').eq(1).find('input').val(filter.cdGroupNm);
                            if(typeof filter.useYn != "undefined") {
                                $('.jsgrid-filter-row').find('td').eq(2).find('input').prop({
                                    checked: filter.useYn,
                                    readOnly: false,
                                    indeterminate: false
                                });
                            }
                        }

                        d.resolve(response);
                        $("#itemsCount").text(response.itemsCount);


                    }).fail(function(xhr) {
                    console.error("grp/list.doax failed", xhr.status, xhr.responseText);
                    alert("Failed to load group codes. Please check server logs.");
                    d.resolve({ data: [], itemsCount: 0 });
                    });

                    return d.promise();
                },
                // Inserting CodeGroup
                insertItem: function(data) {
                    if(data.groupCd == "" || data.cdGroupNm ==""){
                        alert("코드그룹아이디, 그룹명은 필수입력값입니다.");
                        return;
                    }
                    if(!confirm("코드그룹을 등록하시겠습니까?")) return;
                    $.ajax({
                        type: 'POST',
                        url: "${CONTEXT_PATH }/toy/admin/sys/code/grp/insert.doax",
                        data: data,
                        beforeSend: function (xhr) {
                            // Your custom logic only (loading spinner etc.)
                        },
                        success: function(res) {
                            if (res && res.redirectUrl) { window.location.href = res.redirectUrl; return; }
                            if (res.result === "Y") { alert("정상적으로 등록되었습니다."); fn_setJsGrid(); return; }
                            if (res.result === "Duple") { alert(res.errorMessage || "Duplicate group code."); return; }
                            if (res.result === "Invalid") { alert(res.errorMessage || "Invalid data."); return; }
                            alert(res.errorMessage || "Insert failed.");
                        },
                        error: function(request, status, error) {
                            fn_AjaxError(request, status, error, "${CONTEXT_PATH }/toy/admin/login.do");
                        }
                    });
                },
                // Updating GroupCode
                updateItem: function(data) {
                    //console.log("updateItem data from jsGrid =", data);
                    if(data.groupCd == "" || data.cdGroupNm ==""){
                        alert("코드그룹아이디, 그룹명은 필수입력값입니다.");
                        return;
                    }

                    if(!confirm("정말로 수정하시겠습니까?")) return;
                    let payload = {
                        groupCd: data.groupCd,
                        cdGroupNm: data.cdGroupNm,
                        useYn: data.useYn
                    };
                    $.ajax({
                        type: 'POST',
                        url: "${CONTEXT_PATH }/toy/admin/sys/code/grp/update.doax",
                        data: payload,
                        beforeSend: function (xhr) {
                            // Your custom logic only (loading spinner etc.)
                        },
                        success: function(res) {
                            if (res && res.redirectUrl) { window.location.href = res.redirectUrl; return; }
                            if (res.result === "Y") { alert("정상적으로 수정되었습니다."); fn_setJsGrid(); return; }
                            if (res.result === "None") { alert(res.errorMessage || "Group code does not exist."); fn_setJsGrid(); return; }
                            if (res.result === "Invalid") { alert(res.errorMessage || "Invalid data."); return; }
                            alert(res.errorMessage || "Update failed.");
                        },
                        error: function(request, status, error) {
                            fn_AjaxError(request, status, error, "${CONTEXT_PATH }/toy/admin/login.do");
                        }
                    });
                }
            },
            rowClick: function(args) {
                // when rowclicks, expand the list of codes belonging on groupCode
                fn_setCdJsGrid(args.item.groupCd);
                $(".jsgrid-pick-row").removeClass("jsgrid-pick-row");
                $(".jsgrid-selected-row").addClass("jsgrid-pick-row");
            },
            fields: [
                //{ name:"rn"            , title:"번호"        , type:"text"    , width:40 , align:"center", sorting: false, filtering: false, editing: false },
                { name:"groupCd" , title:"코드그룹아이디"  , type:"text"   , width:90, align: "left", editing: false  },
                { name:"cdGroupNm" , title:"코드그룹명"      , type:"text"   , width:100, align: "left"  , sorting: false },
                { name:"useYn"      , title:"사용여부"      , type:"checkbox", width:35 , align: "center", inserting: false },
                //{ type:"control"    , deleteButton: false, width:40 }
                { type:"control"    , width : 100,
                    filterTemplate: function() {	// 검색 & 초기화 버튼
                        var filterBtn = $("<a>").addClass("btn sm").append("<i class=\"material-icons-outlined\">search</i>").append(" 검 색").click(function(e) {
                            $("#jsGrid").jsGrid('search');
                            e.stopPropagation();
                        });
                        var clearBtn = $("<a>").addClass("btn gray sm").append("<i class=\"material-icons-outlined\">clear</i>").append(" 초기화").click(function(e) {
                            $("#jsGrid").jsGrid('clearFilter');
                            e.stopPropagation();
                        });
                        return $("<div>").append(filterBtn).append("&nbsp;").append(clearBtn);
                    },
                    insertTemplate: function(value, item) {	// 추가 버튼
                        var insertBtn = $("<a>").addClass("btn blue sm").append("<i class=\"material-icons-outlined\">add</i>").append(" 등 록").click(function(e) {
                            $("#jsGrid").jsGrid('insertItem', item);
                            e.stopPropagation();
                        });
                        var clearBtn = $("<a>").addClass("btn gray sm").append("<i class=\"material-icons-outlined\">clear</i>").append(" 취 소").click(function(e) {
                            $("#jsGrid").jsGrid('clearInsert');
                            e.stopPropagation();
                        });
                        return $("<div>").append(insertBtn).append("&nbsp;").append(clearBtn);
                    },
                    editTemplate: function(_, item) {	// Save, Cancel buttons
                        var insertBtn = $("<a>").addClass("btn blue sm")
                            .append("<i class=\"material-icons-outlined\">check</i>")
                            .append(" 저 장")
                            .click(function(e) {
                                // IMPORTANT: do NOT pass 'item' here.
                                // jsGrid will read the current editors' values and build the updated data.
                                $("#jsGrid").jsGrid('updateItem');
                                e.stopPropagation();
                                console.log("control editTemplate item(snapshot)=", item);
                        });
                        var clearBtn = $("<a>").addClass("btn gray sm")
                            .append("<i class=\"material-icons-outlined\">clear</i>")
                            .append(" 취 소")
                            .click(function(e) {
                                $("#jsGrid").jsGrid('cancelEdit');
                                e.stopPropagation();
                        });
                        
                        return $("<div>").append(insertBtn).append("&nbsp;").append(clearBtn);
                    },
                    itemTemplate: function(value, item) {	// 수정 버튼
                        var editBtn = $("<a>").addClass("btn black sm").append("<i class=\"material-icons-outlined\">edit</i>").append(" 수 정").click(function(e) {
                            $("#jsGrid").jsGrid('editItem', item);
                            e.stopPropagation();
                        });
                        return $("<div>").append(editBtn);
                    }

                }
            ]
        });
    }

    // 코드목록 조회
    function fn_setCdJsGrid(groupCd){
        // NONE, LOGIN 그룹은 default 그룹으로 유저를 추가할 수 없다.
        /*if (groupCd == "NONE" || groupCd == "LOGIN" || groupCd == "ADMIN_PART_MANAGER" || groupCd == "ADMIN_OUT_MANAGER"){
            $("#insertButton").css("display", "none");
            $("#cdJsGrid").empty();
            return;
        }*/

        $("#cdJsGrid").jsGrid({
            width: "100%",
            height: "630px",

            autoload: true,
            selecting: false,
            inserting: true, // 그리드내 작성
            editing: true, // 그리드내 수정

            // 페이징
            paging: false,
            pageLoading: true,

            controller: {
                loadData: function(data) {
                    var d = $.Deferred();
                    $.ajax({
                        type: 'POST',
                        url: "${CONTEXT_PATH }/toy/admin/sys/code/cd/list.doax?groupCd=" + groupCd,
                        data: data,
                        beforeSend : function(xhr){
                            var token = $("meta[name='_csrf']").attr("content");
                            var header = $("meta[name='_csrf_header']").attr("content");
                            xhr.setRequestHeader(header, token);

                            $("#cdCntId").removeClass('hide');
                        },
                        dataType: "json"
                    }).done(function(response) {
                        // If auth failed, server returns redirectUrl.
                        if (response && response.redirectUrl) {
                            window.location.href = response.redirectUrl;
                            return;
                        }
                        if(response.data.length == 0) {
                            $("#cdJsGrid").jsGrid({
                                autoload: false,
                                noDataContent: '<div class="not-content"><span class="icon"></span><div class="text">검색 결과가 없습니다.</div></div>',
                            })
                        }

                        d.resolve(response);
                        $("#codeCount").text(response.itemsCount);
                    });
                    return d.promise();
                },
                // Inserting for code
                insertItem: function(data) {

                    if(data.cd == "" || data.cdNm ==""){
                        alert("코드아이디, 코드명 필수입력값입니다.");
                        return;
                    }
                    if(!confirm("코드를 등록하시겠습니까?")) return;

                    $.ajax({
                        type: 'POST',
                        url: "${CONTEXT_PATH }/toy/admin/sys/code/cd/insert.doax?groupCd=" + groupCd,
                        data: data,
                        beforeSend: function (xhr) {
                            // Your custom logic only (loading spinner etc.)
                        },
                        success: function(res) {
                            if (res && res.redirectUrl) { window.location.href = res.redirectUrl; return; }
                            if (res.result === "Y") {
                                alert("정상적으로 작성되었습니다.");
                                fn_setJsGrid();
                                fn_gridRefresh();
                                return;
                            }
                            if (res.result === "Duple") { alert(res.errorMessage || "Duplicate code."); return; }
                            if (res.result === "Invalid") { alert(res.errorMessage || "Invalid data."); return; }
                            alert(res.errorMessage || "Insert failed.");
                        },
                        error: function(request, status, error) {
                            fn_AjaxError(request, status, error, "${CONTEXT_PATH }/toy/admin/login.do");
                        }
                    });
                }
            },
            rowClick: function(args) {
                // TODO :???
            },

            fields: [
                { name:"cd" , title:"코드 아이디"  , type:"text"   , width:100, align: "left", editing: false  },
                { name:"cdNm" , title:"코드 명"      , type:"text"   , width:100, align: "left"  , sorting: false },
                { name:"useYn"     , title:"사용여부"      	, type:"checkbox"	, width:40 , align: "center", sorting: false, inserting: false},
                { type:"control" , width:100 ,
                    insertTemplate: function(value, item) {	// 추가 버튼
                        var insertBtn = $("<a>").addClass("btn blue sm").append("<i class=\"material-icons-outlined\">add</i>").append(" 등 록").click(function(e) {
                            $("#cdJsGrid").jsGrid('insertItem', item);
                            e.stopPropagation();
                            $("#cdJsGrid").jsGrid('clearInsert');

                        });
                        var clearBtn = $("<a>").addClass("btn gray sm").append("<i class=\"material-icons-outlined\">clear</i>").append(" 취 소").click(function(e) {
                            $("#cdJsGrid").jsGrid('clearInsert');
                            $("#cdJsGrid").jsGrid("option", "inserting", false);
                            e.stopPropagation();
                        });
                        return $("<div>").append(insertBtn).append("&nbsp;").append(clearBtn);
                    },
                    itemTemplate: function(value, item) {

                        var editBtn = $("<a>").addClass("btn black sm").append("<i class=\"material-icons-outlined\">edit</i>").attr("href","javascript:fn_updateCdPop('" + item.groupCd + "','"+ item.cd +"')").append(" 수 정");

                        var delBtn = $("<a>").addClass("btn orange sm").append("<i class=\"material-icons-outlined\">delete</i>").attr("href","javascript:deleteCd('" + item.groupCd + "','"+ item.cd+ "','"+ item.aditInfo+"')").append(" 삭 제");

                        return $("<div>").append(editBtn).append("&nbsp;").append(delBtn);
                    }
                }
            ]
        });
    }

    //Popup for code detail for updating
    function fn_updateCdPop(groupCd, cd){
        var url = "${CONTEXT_PATH }/toy/admin/sys/code/cd/detail.do"
            + "?groupCd=" + encodeURIComponent(groupCd)
            + "&cd=" + encodeURIComponent(cd);
        window.open(url, "dtlcd", "resizable=yes,scrollbars=yes,width=800,height=320");
    }

    //코드 삭제
    function deleteCd(groupCd,cd,aditInfo1) {
        if(!confirm("해당 코드를 삭제하시겠습니까?")) {
            return;
        }

        $("#delete_form input[name='groupCd']").val(groupCd);
        $("#delete_form input[name='cd']").val(cd);
        $("#delete_form input[name='aditInfo1']").val(aditInfo1);
        $.ajax({
            type: 'POST',
            url: "<c:url value='${CONTEXT_PATH }/toy/admin/sys/code/cd/delete.doax' />",
            data: $("#delete_form").serialize(),
            beforeSend : function(xhr){
                var token = $("meta[name='_csrf']").attr("content");
                var header = $("meta[name='_csrf_header']").attr("content");
                xhr.setRequestHeader(header, token);
            },

            success: function(data) {
                if (data && data.redirectUrl) {
                    window.location.href=data.redirectUrl;
                    return;
                }
                if(data.result == "N") {
                    //파라미터 체크실패했을때
                    alert(data.errorMessage);
                    return;
                }

                if(data.result == "None") {
                    //삭제할데이터가없을때(파라미터값을 임의로 집어넣었을때)
                    alert(data.errorMessage);
                    return;
                }

                if (data.result == "Invalid") {
                    //Constraint violation (e.g. FK)
                    alert(data.errorMessage || "Cannot delete due to constraint violation.");
                    return;
                }

                else {
                    alert("삭제처리 되었습니다.");

                    //코드 목록 다시 가져오기
                    //fn_setCdJsGrid(data.groupCd);
                    fn_gridRefresh();
                }
            },
            error: fn_AjaxError
        });
    }

    //코드목록 refresh
    function fn_gridRefresh(){
        $("#cdJsGrid").jsGrid("loadData");
    }

</script>
<div id="wrapper">
    <%-- Common Header/Side Menu --%>
    <jsp:include page="/WEB-INF/jsp/toy/admin/include/adminHeader.jsp" flush="false">
        <jsp:param value="home_menu" name="admin_menu"/>
    </jsp:include>
    <main id="main">
        <section class="contents-wrap">
            <div class="title-area">
                <h2 class="depth-title">코드 관리</h2> <!-- depth-title > h2고정 -->
            </div>

            <%-- Code Delete Form --%>
            <form name="delete_form" id="delete_form" method="post">
                <input type="hidden" name="groupCd" value="">
                <input type="hidden" name="cd" value="">
                <input type="hidden" name="aditInfo1" value="">
            </form>

            <!-- Change Contents (↓↓↓↓) -->
            <section class="content-box">

                <!--table Title-->
                <table id="content_table">
                    <tr style="vertical-align: top;">
                        <td>
                            <h3 class="title2">
                                검색결과 <small>[총 <strong id="itemsCount" class="text-blue">0</strong>건]</small>
                            </h3>
                            <div id="jsGrid" ></div>

                            <div class="paging-group">
                                <div id="externalPager" style="text-align:center;"></div>
                            </div>
                        </td>

                        <td style="padding-left: 20px;">
                            <h3 class="title2 hide" id="cdCntId">
                                코드 건수 <small>[ <strong id="codeCount" class="text-blue">0</strong>건]</small>
                            </h3>

                            <div id="cdJsGrid"></div>
                        </td>
                    </tr>
                </table>

            </section>


        </section>
    </main>
</div>

</head>

<body>

</body>


