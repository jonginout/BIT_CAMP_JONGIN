<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

	<script type="text/javascript">
	function mod()
	{
		if(confirm('수정하시겠습니까?')){
			return true;
		}else {
			return false;
		}
	}
	</script>
	<style type="text/css">
		#submit{
			width: 577px;
			margin-bottom: 20px;
		}
	</style>
	<body>
		<div align="center">
		
		<h1>글 작성</h1>
		
		<form method="post" 
		      action="${pageContext.request.contextPath}/board/modify?no=${detail.no}">
	
			<div>
				제목 : <input type="text" name="title" value="${detail.title}">
				작성자 : <input type="text" name="writer" value="${detail.writer}">
			</div>
			<br />
			<div>
				<textarea rows="6" cols="80" name="content">${detail.content}</textarea>
			</div>
			<div>
				<button onclick="return mod();" id="submit">수정</button>
			</div>
		</form>
		
			<a href="detail?no=${detail.no}">수정취소</a>
			/ 
			<a href="list">글 목록</a>
		
		</div>
	</body>
</html>








