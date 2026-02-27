<%--
  Created by IntelliJ IDEA.
  User: bigbe
  Date: 2025-12-23
  Time: ?ㅽ썑 2:52
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

            filtering: true, // 寃???꾪꽣
            autoload: true,
            sorting: false, // ?뺣젹
            editing: true, // 洹몃━?쒕궡 ?섏젙
            inserting: true, // 洹몃━?쒕궡 ?묒꽦

            // ?섏씠吏?
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
                                noDataContent: '<div class="not-content"><span class="icon"></span><div class="text"><spring:message code="admin.common.grid.noResults" /></div></div>',
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
                    alert("<spring:message code='admin.system.code.list.alert.loadGroupFailed' javaScriptEscape='true' />");
                    d.resolve({ data: [], itemsCount: 0 });
                    });

                    return d.promise();
                },
                // Inserting CodeGroup
                insertItem: function(data) {
                    if(data.groupCd == "" || data.cdGroupNm ==""){
                        alert("<spring:message code='admin.system.code.list.alert.requiredGroupFields' javaScriptEscape='true' />");
                        return;
                    }
                    if(!confirm("<spring:message code='admin.system.code.list.confirm.insertGroup' javaScriptEscape='true' />")) return;
                    $.ajax({
                        type: 'POST',
                        url: "${CONTEXT_PATH }/toy/admin/sys/code/grp/insert.ac",
                        data: data,
                        beforeSend: function (xhr) {
                            // Your custom logic only (loading spinner etc.)
                        },
                        success: function(res) {
                            if (res && res.redirectUrl) { window.location.href = res.redirectUrl; return; }
                            if (res.result === "Y") { alert("<spring:message code='admin.system.code.list.alert.groupInserted' javaScriptEscape='true' />"); fn_setJsGrid(); return; }
                            if (res.result === "Duple") { alert(res.errorMessage || "<spring:message code='admin.system.code.list.alert.duplicateGroup' javaScriptEscape='true' />"); return; }
                            if (res.result === "Invalid") { alert(res.errorMessage || "<spring:message code='admin.common.invalidData' javaScriptEscape='true' />"); return; }
                            alert(res.errorMessage || "<spring:message code='admin.system.code.list.alert.insertFailed' javaScriptEscape='true' />");
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
                        alert("<spring:message code='admin.system.code.list.alert.requiredGroupFields' javaScriptEscape='true' />");
                        return;
                    }

                    if(!confirm("<spring:message code='admin.system.code.list.confirm.updateGroup' javaScriptEscape='true' />")) return;
                    let payload = {
                        groupCd: data.groupCd,
                        cdGroupNm: data.cdGroupNm,
                        useYn: data.useYn
                    };
                    $.ajax({
                        type: 'POST',
                        url: "${CONTEXT_PATH }/toy/admin/sys/code/grp/update.ac",
                        data: payload,
                        beforeSend: function (xhr) {
                            // Your custom logic only (loading spinner etc.)
                        },
                        success: function(res) {
                            if (res && res.redirectUrl) { window.location.href = res.redirectUrl; return; }
                            if (res.result === "Y") { alert("<spring:message code='admin.system.code.list.alert.groupUpdated' javaScriptEscape='true' />"); fn_setJsGrid(); return; }
                            if (res.result === "None") { alert(res.errorMessage || "<spring:message code='admin.system.code.list.alert.groupNotFound' javaScriptEscape='true' />"); fn_setJsGrid(); return; }
                            if (res.result === "Invalid") { alert(res.errorMessage || "<spring:message code='admin.common.invalidData' javaScriptEscape='true' />"); return; }
                            alert(res.errorMessage || "<spring:message code='admin.system.code.list.alert.updateFailed' javaScriptEscape='true' />");
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
                //{ name:"rn"            , title:"踰덊샇"        , type:"text"    , width:40 , align:"center", sorting: false, filtering: false, editing: false },
                { name:"groupCd" , title:"<spring:message code='admin.system.code.common.field.groupCd' javaScriptEscape='true' />", type:"text", width:90, align: "left", editing: false  },
                { name:"cdGroupNm" , title:"<spring:message code='admin.system.code.common.field.groupName' javaScriptEscape='true' />", type:"text", width:100, align: "left", sorting: false },
                { name:"useYn" , title:"<spring:message code='admin.system.code.common.field.useYn' javaScriptEscape='true' />", type:"checkbox", width:35, align: "center", inserting: false },
                //{ type:"control"    , deleteButton: false, width:40 }
                { type:"control"    , width : 100,
                    filterTemplate: function() {	// 寃??& 珥덇린??踰꾪듉
                        var filterBtn = $("<a>").addClass("btn sm").append("<i class=\"material-icons-outlined\">search</i>").append(" <spring:message code='admin.common.button.search' javaScriptEscape='true' />").click(function(e) {
                            $("#jsGrid").jsGrid('search');
                            e.stopPropagation();
                        });
                        var clearBtn = $("<a>").addClass("btn gray sm").append("<i class=\"material-icons-outlined\">clear</i>").append(" <spring:message code='admin.common.button.clear' javaScriptEscape='true' />").click(function(e) {
                            $("#jsGrid").jsGrid('clearFilter');
                            e.stopPropagation();
                        });
                        return $("<div>").append(filterBtn).append("&nbsp;").append(clearBtn);
                    },
                    insertTemplate: function(value, item) {	// 異붽? 踰꾪듉
                        var insertBtn = $("<a>").addClass("btn blue sm").append("<i class=\"material-icons-outlined\">add</i>").append(" <spring:message code='admin.common.button.add' javaScriptEscape='true' />").click(function(e) {
                            $("#jsGrid").jsGrid('insertItem', item);
                            e.stopPropagation();
                        });
                        var clearBtn = $("<a>").addClass("btn gray sm").append("<i class=\"material-icons-outlined\">clear</i>").append(" <spring:message code='admin.common.button.cancel' javaScriptEscape='true' />").click(function(e) {
                            $("#jsGrid").jsGrid('clearInsert');
                            e.stopPropagation();
                        });
                        return $("<div>").append(insertBtn).append("&nbsp;").append(clearBtn);
                    },
                    editTemplate: function(_, item) {	// Save, Cancel buttons
                        var insertBtn = $("<a>").addClass("btn blue sm")
                            .append("<i class=\"material-icons-outlined\">check</i>")
                            .append(" <spring:message code='admin.common.button.save' javaScriptEscape='true' />")
                            .click(function(e) {
                                // IMPORTANT: do NOT pass 'item' here.
                                // jsGrid will read the current editors' values and build the updated data.
                                $("#jsGrid").jsGrid('updateItem');
                                e.stopPropagation();
                                console.log("control editTemplate item(snapshot)=", item);
                        });
                        var clearBtn = $("<a>").addClass("btn gray sm")
                            .append("<i class=\"material-icons-outlined\">clear</i>")
                            .append(" <spring:message code='admin.common.button.cancel' javaScriptEscape='true' />")
                            .click(function(e) {
                                $("#jsGrid").jsGrid('cancelEdit');
                                e.stopPropagation();
                        });
                        
                        return $("<div>").append(insertBtn).append("&nbsp;").append(clearBtn);
                    },
                    itemTemplate: function(value, item) {	// ?섏젙 踰꾪듉
                        var editBtn = $("<a>").addClass("btn black sm").append("<i class=\"material-icons-outlined\">edit</i>").append(" <spring:message code='admin.common.button.edit' javaScriptEscape='true' />").click(function(e) {
                            $("#jsGrid").jsGrid('editItem', item);
                            e.stopPropagation();
                        });
                        return $("<div>").append(editBtn);
                    }

                }
            ]
        });
    }

    // 肄붾뱶紐⑸줉 議고쉶
    function fn_setCdJsGrid(groupCd){
        // NONE, LOGIN 洹몃９? default 洹몃９?쇰줈 ?좎?瑜?異붽??????녿떎.
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
            inserting: true, // 洹몃━?쒕궡 ?묒꽦
            editing: true, // 洹몃━?쒕궡 ?섏젙

            // ?섏씠吏?
            paging: false,
            pageLoading: true,

            controller: {
                loadData: function(data) {
                    var d = $.Deferred();
                    $.ajax({
                        type: 'POST',
                        url: "${CONTEXT_PATH }/toy/admin/sys/code/cd/list.doax?groupCd=" + groupCd,
                        data: data,
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
                            $("#cdJsGrid").jsGrid({
                                autoload: false,
                                noDataContent: '<div class="not-content"><span class="icon"></span><div class="text"><spring:message code="admin.common.grid.noResults" /></div></div>',
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
                        alert("<spring:message code='admin.system.code.list.alert.requiredCodeFields' javaScriptEscape='true' />");
                        return;
                    }
                    if(!confirm("<spring:message code='admin.system.code.list.confirm.insertCode' javaScriptEscape='true' />")) return;

                    $.ajax({
                        type: 'POST',
                        url: "${CONTEXT_PATH }/toy/admin/sys/code/cd/insert.ac?groupCd=" + groupCd,
                        data: data,
                        beforeSend: function (xhr) {
                            // Your custom logic only (loading spinner etc.)
                        },
                        success: function(res) {
                            if (res && res.redirectUrl) { window.location.href = res.redirectUrl; return; }
                            if (res.result === "Y") {
                                alert("<spring:message code='admin.system.code.list.alert.codeInserted' javaScriptEscape='true' />");
                                fn_setJsGrid();
                                fn_gridRefresh();
                                return;
                            }
                            if (res.result === "Duple") { alert(res.errorMessage || "<spring:message code='admin.system.code.list.alert.duplicateCode' javaScriptEscape='true' />"); return; }
                            if (res.result === "Invalid") { alert(res.errorMessage || "<spring:message code='admin.common.invalidData' javaScriptEscape='true' />"); return; }
                            alert(res.errorMessage || "<spring:message code='admin.system.code.list.alert.insertFailed' javaScriptEscape='true' />");
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
                { name:"cd" , title:"<spring:message code='admin.system.code.common.field.code' javaScriptEscape='true' />", type:"text", width:100, align: "left", editing: false  },
                { name:"cdNm" , title:"<spring:message code='admin.system.code.common.field.codeName' javaScriptEscape='true' />", type:"text", width:100, align: "left", sorting: false },
                { name:"useYn" , title:"<spring:message code='admin.system.code.common.field.useYn' javaScriptEscape='true' />", type:"checkbox", width:40, align: "center", sorting: false, inserting: false},
                { type:"control" , width:100 ,
                    insertTemplate: function(value, item) {	// 異붽? 踰꾪듉
                        var insertBtn = $("<a>").addClass("btn blue sm").append("<i class=\"material-icons-outlined\">add</i>").append(" <spring:message code='admin.common.button.add' javaScriptEscape='true' />").click(function(e) {
                            $("#cdJsGrid").jsGrid('insertItem', item);
                            e.stopPropagation();
                            $("#cdJsGrid").jsGrid('clearInsert');

                        });
                        var clearBtn = $("<a>").addClass("btn gray sm").append("<i class=\"material-icons-outlined\">clear</i>").append(" <spring:message code='admin.common.button.cancel' javaScriptEscape='true' />").click(function(e) {
                            $("#cdJsGrid").jsGrid('clearInsert');
                            $("#cdJsGrid").jsGrid("option", "inserting", false);
                            e.stopPropagation();
                        });
                        return $("<div>").append(insertBtn).append("&nbsp;").append(clearBtn);
                    },
                    itemTemplate: function(value, item) {

                        var editBtn = $("<a>").addClass("btn black sm").append("<i class=\"material-icons-outlined\">edit</i>").attr("href","javascript:fn_updateCdPop('" + item.groupCd + "','"+ item.cd +"')").append(" <spring:message code='admin.common.button.edit' javaScriptEscape='true' />");

                        var delBtn = $("<a>").addClass("btn orange sm").append("<i class=\"material-icons-outlined\">delete</i>").attr("href","javascript:deleteCd('" + item.groupCd + "','"+ item.cd+ "','"+ item.aditInfo+"')").append(" <spring:message code='admin.common.button.delete' javaScriptEscape='true' />");

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

    //肄붾뱶 ??젣
    function deleteCd(groupCd,cd,aditInfo1) {
        if(!confirm("<spring:message code='admin.system.code.list.confirm.deleteCode' javaScriptEscape='true' />")) {
            return;
        }

        $("#delete_form input[name='groupCd']").val(groupCd);
        $("#delete_form input[name='cd']").val(cd);
        $("#delete_form input[name='aditInfo1']").val(aditInfo1);
        $.ajax({
            type: 'POST',
            url: "<c:url value='${CONTEXT_PATH }/toy/admin/sys/code/cd/delete.ac' />",
            data: $("#delete_form").serialize(),
            beforeSend: function (xhr) {
                // Your custom logic only (loading spinner etc.)
            },

            success: function(data) {
                if (data && data.redirectUrl) {
                    window.location.href=data.redirectUrl;
                    return;
                }
                if(data.result == "N") {
                    //?뚮씪誘명꽣 泥댄겕?ㅽ뙣?덉쓣??
                    alert(data.errorMessage);
                    return;
                }

                if(data.result == "None") {
                    //??젣?좊뜲?댄꽣媛?놁쓣???뚮씪誘명꽣媛믪쓣 ?꾩쓽濡?吏묒뼱?ｌ뿀?꾨븣)
                    alert(data.errorMessage);
                    return;
                }

                if (data.result == "Invalid") {
                    //Constraint violation (e.g. FK)
                    alert(data.errorMessage || "<spring:message code='admin.system.code.list.alert.deleteConstraint' javaScriptEscape='true' />");
                    return;
                }

                else {
                    alert("<spring:message code='admin.system.code.list.alert.deleted' javaScriptEscape='true' />");

                    //肄붾뱶 紐⑸줉 ?ㅼ떆 媛?몄삤湲?
                    //fn_setCdJsGrid(data.groupCd);
                    fn_gridRefresh();
                }
            },
            error: fn_AjaxError
        });
    }

    //肄붾뱶紐⑸줉 refresh
    function fn_gridRefresh(){
        $("#cdJsGrid").jsGrid("loadData");
    }

</script>
</head>
<body>
<div id="wrapper">
    <%-- Common Header/Side Menu --%>
    <jsp:include page="/WEB-INF/jsp/toy/admin/include/adminHeader.jsp" flush="false" />
    <main id="main">
        <section class="contents-wrap">
            <div class="title-area">
                <h2 class="depth-title"><spring:message code="admin.system.code.list.title" /></h2> <!-- depth-title > h2고정 -->
            </div>

            <%-- Code Delete Form --%>
            <form name="delete_form" id="delete_form" method="post">
                <input type="hidden" name="groupCd" value="">
                <input type="hidden" name="cd" value="">
                <input type="hidden" name="aditInfo1" value="">
            </form>

            <!-- Change Contents (?볛넃?볛넃) -->
            <section class="content-box">

                <!--table Title-->
                <table id="content_table">
                    <tr style="vertical-align: top;">
                        <td>
                            <h3 class="title2">
                                <spring:message code="admin.common.searchResult" /><small>[<spring:message code="admin.common.total" /> <strong id="itemsCount" class="text-blue">0</strong><spring:message code="admin.common.countUnit" />]</small>
                            </h3>
                            <div id="jsGrid" ></div>

                            <div class="paging-group">
                                <div id="externalPager" style="text-align:center;"></div>
                            </div>
                        </td>

                        <td style="padding-left: 20px;">
                            <h3 class="title2 hide" id="cdCntId">
                                <spring:message code="admin.system.code.list.codeCount" /> <small>[ <strong id="codeCount" class="text-blue">0</strong><spring:message code="admin.common.countUnit" />]</small>
                            </h3>

                            <div id="cdJsGrid"></div>
                        </td>
                    </tr>
                </table>

            </section>


        </section>
    </main>
</div>
</body>
</html>

