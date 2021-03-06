// import { request } from "https";

/*
	// 사용할 모듈 정리
	1. http (웹 서버 기능) 
	2. fs (파일 내용 읽기)
	3. mysql (데이터베이스 접근)
	4. querystring (파라미터 처리)
	
	게시판 기능중에서 등록폼, 등록, 목록 
	
	/writeForm.do 일 경우 writeForm.html 파일의 내용을 읽어서 
	사용자에게 전송
	
	/write.do 일 경우 넘어온 파라미터 값을 얻어온 다음 데이터베이스에
	입력
	
	등록된 다음 자동으로 /list.do 경로를 호출
	: response.writeHead(302, {"Location": "list.do"}); 
	
	/list.do 일 경우 데이터베이스의 게시물 내용을 조회한 다음 
	사용자 화면으로 결과를 넘겨준다.
 */
var http = require("http");
var fs = require("fs");
var url = require("url");
var mysql = require("mysql");
var qs = require("querystring");
var pug = require("pug")

// 커넥션...
var con = mysql.createConnection({
	user: "hanbit",
	password: "hanbit",
	database: "hanbit"
});

http.createServer(function(request, response) {
	var pathname = url.parse(request.url).pathname;
	console.log(pathname);
	switch (pathname) {
	case "/":
		index(response);	
		break;
	case "/writeForm.do":
		writeForm(response);	
		break;
	case "/write.do":
		write(request, response);	
		break;
	case "/updateform.do":
		updateForm(request, response);	
		break;
	case "/update.do":
		update(request, response);	
		break;
	case "/detail.do":
		detail(request, response);	
		break;
	case "/delete.do":
		del(request, response);	
		break;
	case "/list.do":
		list(response);	
		break;
	default:
		error404(response);
	}
})
.listen(10001, function() {
	console.log("10001 서버 구동중");
});

function detail(req, res) {
	var param = url.parse(req.url, true).query;

	con.query(
		"select * from tb_board where no = ? ", 
		[param.no],
		function (err, rows) {
			if (err) {
				throw err;
			}
			var board = rows[0];
			pug.renderFile(
				"view/board/detail.pug",
				{board : board},
				function (err, data) {
					if(err){
						console.log(err);
						return
					}
					res.writeHead(200, {"Content-Type":"text/html; charset=utf-8"});
					res.end(data)
				}
			);

		}
	);
}

function del(request, response) {
	var param = url.parse(request.url, true).query;
	con.query(
		"delete from tb_board where no = ? ", 
		[param.no],
		function (err, result) {
			if (err) {
				throw err;
			}
			// console.log(result);
			// console.log(result.affectedRows);
			// console.log("삭제가 성공했습니다.");

			response.writeHead(302, {"Location": "list.do"});
			response.end();
		}
	);
}

function error404(response) {
	fs.readFile("view/error/error404.html", "utf-8", function(err, data) {
		// 오류에 대한 처리 코드 추가 가능
		response.writeHead(200, {"Content-Type": "text/html; charset=utf-8"});
		response.end(data);	
	});	
}

function index(response) {
	fs.readFile("view/index.html", "utf-8", function(err, data) {
		// 오류에 대한 처리 코드 추가 가능
		response.writeHead(200, {"Content-Type": "text/html; charset=utf-8"});
		response.end(data);	
	});	
}

function list(res) {
	var sql = "select no, title, writer " +
	"  from tb_board         " +
	" order by no desc        ";
	con.query(sql, function (err, rows, fields) {
		// 에러가 존재할 경우
		if (err) {
			throw err;
		}
		pug.renderFile(
			"view/board/list.pug",
			{boards : rows},
			function (err, data) {
				if(err){
					console.log(err);
					return
				}
				res.writeHead(200, {"Content-Type":"text/html; charset=utf-8"});
				res.end(data)
			}
		);	


	});
}
function write(request, response) {
	var pData = "";
	request.on("data", function (data) {
		pData += data;
	});
	
	request.on("end", function () {
		var param = qs.parse(pData);
		var sql = "insert into tb_board(writer, title, content) values(?, ?, ?) ";
		con.query(
				sql, 
				[param.writer, param.title, param.content], 
				function (err, result) {
					if (err) {
						throw err;
					}
					response.writeHead(302, {"Location": "list.do"});
					response.end();
				}
		);
	});
}
function update(req, res) {
	var pData = "";
	req.on("data", function (data) {
		pData += data;
	});
	
	req.on("end", function () {
		var param = qs.parse(pData);
		var sql = "update tb_board set title = ?, content = ? where no = ? ";
		con.query(
				sql, 
				[param.title, param.content, param.no], 
				function (err, result) {
					if (err) {
						throw err;
					}
					res.writeHead(302, {"Location": "list.do"});
					res.end();
				}
		);
	});
}

function writeForm(res) {
	pug.renderFile(
		"view/board/writeForm.pug",
		function (err, data) {
			if(err){
				console.log(err);
				return
			}
			res.writeHead(200, {"Content-Type":"text/html; charset=utf-8"});
			res.end(data)
		}
	);	
}

function updateForm(req, res) {
	var param = url.parse(req.url, true).query;
	
	con.query(
		"select * from tb_board where no = ? ", 
		[param.no],
		function (err, rows) {
			if (err) {
				throw err;
			}
			var board = rows[0];

			pug.renderFile(
				"view/board/updateForm.pug",
				{board : board},
				function (err, data) {
					if(err){
						console.log(err);
						return
					}
					res.writeHead(200, {"Content-Type":"text/html; charset=utf-8"});
					res.end(data)
				}
			);
		}
	);	
}