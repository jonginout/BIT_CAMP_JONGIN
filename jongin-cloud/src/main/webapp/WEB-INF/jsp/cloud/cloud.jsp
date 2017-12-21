<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>    
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="robots" content="noindex">
	
	<%@ include file="/WEB-INF/jsp/include/basicinclude.jsp" %>

	<!-- dynatree -->
	<link href="${pageContext.request.contextPath}/lib/dynatree-master/src/skin/ui.dynatree.css" rel="stylesheet" type="text/css" id="skinSheet">
	<script src="${pageContext.request.contextPath}/lib/dynatree-master/src/jquery.dynatree.js"></script>

	<!-- 코드미러 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/codeMirror/codemirror.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/codeMirror/ambiance.css">
	<script src="${pageContext.request.contextPath}/lib/codeMirror/codemirror.js"></script>
	<script src="${pageContext.request.contextPath}/lib/codeMirror/javascript.js"></script>
	<script src="${pageContext.request.contextPath}/lib/codeMirror/active-line.js"></script>
	<script src="${pageContext.request.contextPath}/lib/codeMirror/matchbrackets.js"></script>
	
	<!-- pdf view -->
	<script src="${pageContext.request.contextPath}/lib/pdfobject/pdfobject.min.js"></script>
	
	<!-- 클라우드 CSS -->
	<link href="${pageContext.request.contextPath}/css/cloud/cloud.css" rel="stylesheet" type="text/css" id="skinSheet">
	

</head>
<body>

  	<%@ include file="/WEB-INF/jsp/include/header.jsp" %>

	<!-- 트리 로드 -->
	<script src="${pageContext.request.contextPath}/js/cloud/cloud.js"></script>
	
              <!------------------------------------------------------------>
              <!------------------------------------------------------------>
    
                	<div class="container" id="cloud-content">
						<div class="button-box">
							<button class="btn btn-default btn-md" id="reload">
								<i class="fa fa-refresh" aria-hidden="true"></i> 
								클라우드 새로고침
							</button>
							<button class="btn btn-default btn-md" id="newFolder">
								<i class="fa fa-folder-o" aria-hidden="true"></i>
								현 위치에 새폴더
							</button>
							<button class="btn btn-default btn-md" id="newCode">
								<i class="fa fa-file-o" aria-hidden="true"></i>
								현 위치에 새파일
							</button>			
							<button class="btn btn-default btn-md" data-toggle="modal" data-target="#addVolume">
								<i class="fa fa-hdd-o" aria-hidden="true"></i> 
								용량추가
							</button>	
						</div>
						<form id="uploadForm" method="post" enctype="multipart/form-data">
							<input type="hidden" name="uploadPath" value=""/>
							<input type="file" name="files" multiple="multiple">
							<button type="button">업로드</button>
						</form>
						
						<div id="tree"></div>
				
						<div class="row detail-box">
							<div class="hidden-xs col-sm-4 file-list">
								<div id="nowList"></div>
							</div>
							<div class="col-xs-12 col-sm-8 file-detail">
								<div id="detailFile" data-key=""></div>
							</div>
						</div>
				
					</div>
    
              <!------------------------------------------------------------>
              <!------------------------------------------------------------>

      	<%@ include file="/WEB-INF/jsp/include/basic-js.jsp" %>

  		
  		
<!-- 클라우드 제어 -->
<script src="${pageContext.request.contextPath}/js/cloud/cloud-set.js"></script>

<!-- 모달 -->
<%@ include file="/WEB-INF/jsp/cloud/modal/cloud-modal.jsp" %>

<!-- 결제 -->
<script type="text/javascript" src="https://service.iamport.kr/js/iamport.payment-1.1.5.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cloud/cloud-pay.js"></script>

</body>
</html>