<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@ taglib prefix="c" 			uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" 			uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="fn" 			uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" 		uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" 		uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" 		uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" 		uri="http://www.springframework.org/security/tags"%>

<%--<%@ taglib prefix="un" 			uri="http://jakarta.apache.org/taglibs/unstandard-1.0" %>--%>

<un:useConstants var="Constant" className="toy.com.util.CmConstants" />

<c:set var="curURL" value="${requestScope['javax.servlet.forward.request_uri']}" />

<c:set var="jsCssVer" value="20260225" />

<%-- 이미지 사이즈 --%>
<c:set var="sizeImageProfl" value="400px * 400px" />        <%-- 프로필 사진 --%>
<%--<c:set var="sizeImageList" value="300px * 400px" />--%>    <%-- 리스트이미지 --%>
<%--<c:set var="sizeImagePtcl" value="500px * 600px" />--%>     <%-- 상세이미지 --%>

