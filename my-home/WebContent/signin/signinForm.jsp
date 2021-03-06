<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<style type="text/css">
	#id-chk-text{
		color: green;
		font-size: 13px;
		font-weight: bold;
	}
</style>
</head>
<body>
	<div class="container">
		<div class="header">
			<c:import url="/include/topMenu.jsp" />
		</div>

		<div class="content">

			<h1>회원가입</h1>

			<c:if test="${error!=null}">
				<div class="alert alert-danger col-md-6" role="alert">${ error }</div>
			</c:if>

			<form action="${pageContext.request.contextPath}/signin/signinprocess"
				method="post" name="form">
				<div class="form-row col-md-12">
					<div class="form-group col-md-3">
						<label for="exampleInputEmail1">
							ID
							<c:if test="${idChk}">
     						 	<span id="id-chk-text">(중복체크 완료)</span>
     						</c:if>
						</label> 
					    <div class="input-group">
					    	<c:if test="${idChk}">
     						 <input type="text" name="id" class="form-control" id="exampleInputEmail1" value="${id}" placeholder="Enter Id" readonly="readonly">
     						</c:if>
     						<c:if test="${!idChk}">
     						 <input type="text" name="id" class="form-control" id="exampleInputEmail1" value="${id}" placeholder="Enter Id">
     						</c:if>
    						  <span class="input-group-btn">
       						 	<button class="btn btn-secondary" type="button" onclick="idChk();" >중복체크</button>
     						 </span>

   						 </div>
					</div>
					<div class="form-group col-md-3">
						<label for="exampleInputPassword1">이름</label> 
						<input type="text" name="name" class="form-control" placeholder="Name">
					</div>
				</div>
				<div class="form-group col-md-6">
					<label for="exampleInputPassword1">Password</label> 
					<input type="password" name="pass" class="form-control"
						id="exampleInputPassword1" placeholder="Password">
				</div>
				<div class="form-group col-md-6">
					<label for="exampleInputPassword1">Password 확인</label> 
					<input type="password" name="passChk" class="form-control"
						id="exampleInputPassword1" placeholder="Password Check">
				</div>
				<div class="form-check col-md-6">
						<button type="button" onclick="clearForm();" class="btn btn-danger">초기화</button>
					<c:if test="${idChk}">
						<button type="button" onclick="signin();" class="btn btn-primary">SignIn</button>
					</c:if>
				</div>
			</form>

		</div>


		<div class="footer">
			<c:import url="/include/footer.jsp" />
		</div>
	</div>

</body>
</html>

<script type="text/javascript">
	function clearForm() {
		console.log("ss");
		location.href="${pageContext.request.contextPath}/signin/signinform";
	}
	function idChk() {
		var id = form.id.value;
		var idPattern = /^([0-9a-zA-Z]){3,8}$/;
		if (!idPattern.test(id)) {
			alert("아이디는 영대소문자,숫자로 3~8자 입니다.");
			return;
		}
		location.href="${pageContext.request.contextPath}/signin/idchk?id="+id;
	}
	function signin() {
		var id = form.id.value;
		var pass = form.pass.value;
		var passChk = form.passChk.value;
		var name = form.name.value;
		
		var idPattern = /^([0-9a-zA-Z]){3,8}$/;
		var namePattern = /^([가-힣a-zA-Z]){2,10}$/;
		var passPattern = /^([0-9a-zA-Z]){3,15}$/;
		
		if (!idPattern.test(id)) {
			alert("아이디는 영대소문자,숫자로 3~8자 입니다.");
			return;
		}
		if (!namePattern.test(name)) {
			alert("이름은 한글,영대소문자,숫자로 2~10자 입니다.");
			return;
		}
		if (!passPattern.test(pass)) {
			alert("패스워드는 영대소문자,숫자로 3~15자 입니다.");
			return;
		}
		if (!passPattern.test(passChk)) {
			alert("패스워드는 영대소문자,숫자로 3~15자 입니다.");
			return;
		}
		if (pass!=passChk) {
			alert("패스워드와 확인이 일치하지 않습니다.");
			return;
		}
		
		form.submit();
	}
	$(".flex-sm-fill.text-sm-center.nav-link.active").removeClass("active");
	$(".flex-sm-fill.text-sm-center.nav-link:eq(5)").addClass("active");
// 	var pattern1 = /(^[a-zA-Z])/;
// 	pattern1.test("가나다") false
</script>