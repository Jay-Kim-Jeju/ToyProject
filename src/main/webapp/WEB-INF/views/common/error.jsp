<%@ page isErrorPage="true" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>오류 발생</title>
</head>
<body>
<h2>🚨 시스템 오류가 발생했습니다(error.jsp)</h2>
<p><strong>요청 경로:</strong> ${url}</p>
<p><strong>예외 메시지:</strong> ${exception.message}</p>

<hr />
<h4>개발자 참고용 로그 확인:</h4>
<p>콘솔 또는 로그 파일을 확인하세요.</p>
</body>
</html>
