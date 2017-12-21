<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!doctype html>
<html lang="ko">
<head>
<script type="text/javascript" src="http://code.jquery.com/jquery-1.11.3.min.js"></script>
</head>
<body>

<h1>네이버 로그인 처리중...</h1>

<script type="text/javascript" src="https://static.nid.naver.com/js/naveridlogin_js_sdk_2.0.0.js" charset="utf-8"></script>
<script type="text/javascript">
	const nowHost = location.hostname.toLowerCase();	
	var localCallbackUrl = "http://localhost:8080/jongin-cloud/login/naverLogin.do";
	var Cafe24CallbackUrl = "http://chopo01.cafe24.com/login/naverLogin.do";
	const callbackUrl = nowHost=='localhost' ? localCallbackUrl : Cafe24CallbackUrl;

	var naverLogin = new naver.LoginWithNaverId(
		{
			clientId: "lbeEftb6cSDwLJdxPMJW",
			callbackUrl: callbackUrl,
			isPopup: false,
			callbackHandle: true
			/* callback 페이지가 분리되었을 경우에 callback 페이지에서는 callback처리를 해줄수 있도록 설정합니다. */
		}
	);

	/* (3) 네아로 로그인 정보를 초기화하기 위하여 init을 호출 */
	naverLogin.init();

	/* (4) Callback의 처리. 정상적으로 Callback 처리가 완료될 경우 main page로 redirect(또는 Popup close) */
	window.addEventListener('load', function () {
		naverLogin.getLoginStatus(function (status) {
			if (status) {
				/* (5) 필수적으로 받아야하는 프로필 정보가 있다면 callback처리 시점에 체크 */
				var email = naverLogin.user.getEmail();
				if( email == undefined || email == null) {
					alert("이메일은 필수정보입니다. 정보제공을 동의해주세요.");
					/* (5-1) 사용자 정보 재동의를 위하여 다시 네아로 동의페이지로 이동함 */
					naverLogin.reprompt();
					return;
				}
				console.log(naverLogin.user)
				signupChk(naverLogin.user)
// 				window.location.replace("http://" + window.location.hostname + ( (location.port==""||location.port==undefined)?"":":" + location.port) + "/sample/main.html");
			} else {
				console.log("callback 처리에 실패하였습니다.");
			}
		});
	});
	
	
	function signupChk(user) {
		$.ajax({
			type : "post",
			url : "${pageContext.request.contextPath}/login/login.do",
			dataType : "json",
			data : {
				id : user.nickname,
				name : user.name,
				email : user.email,
				profileImg : user.profile_image
			},
			dataType : "json",
			success : function (data) {
				if(data.result){
					location.href = "${pageContext.request.contextPath}/cloud/cloud.do"
				}
			}
		})
	}
	
</script>
</body>
</html>
	