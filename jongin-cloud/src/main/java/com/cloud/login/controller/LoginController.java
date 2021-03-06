package com.cloud.login.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import com.cloud.common.Encryption;
import com.cloud.login.service.LoginService;
import com.cloud.repository.vo.AuthSms;
import com.cloud.repository.vo.AutoLogin;
import com.cloud.repository.vo.Member;

@Controller
@RequestMapping("/login")
public class LoginController {

	@Autowired
	LoginService service;

	@RequestMapping("/loginform.do")
	public void loginForm() throws Exception {
	}

	@RequestMapping("/naverLogin.do")
	public void naverLogin() throws Exception {
	}

	@RequestMapping("/signupform.do")
	public void signupForm() throws Exception {
	}
	

	@RequestMapping("/init.json")
	public String init(
			Member member, RedirectAttributes attr
			) throws Exception {

		System.out.println(member.toString());
		
		attr.addFlashAttribute("member", member);
		return "redirect:/login/loginform.do";
	}
	
	
	@RequestMapping("/login.json")
	@ResponseBody
	public Map<String, Object> login(
			Member member, String save, HttpSession session, HttpServletResponse response
			) throws Exception {
		
		System.out.println(member.toString());
		
		Map<String, Object> map = new HashMap<>();
		map.put("result", false);
		
		String loginType = member.getType();
		
		if(loginType!=null) {
			
			Member vo = new Member();
			vo = member;
			
			vo.setPass(Encryption.getSHA512(loginType));
			member.setPass(loginType);
			
			if(service.checkId(vo.getId())<1) {
				
	//			member.setAddress("");
				service.signup(vo);
				
				System.out.println("외부 로그인 최초 회원가입!");
			}
		
		}else {
			member.setPass(Encryption.getSHA512(member.getPass()));
		}
		
//		member.setPass(Encryption.getSHA512(member.getPass()));
		Member user = service.login(member);
		if(user!=null) {
			user.setPass(null);
			session.setAttribute("user", user);
			map.put("result", true);
						
			////////////////////////////////////////자동로그인////////
			if(save.equals("true")) {
				AutoLogin autoLogin = new AutoLogin();
				
				Cookie cookie = new Cookie("userCookie", session.getId());
                // 쿠키를 찾을 경로를 컨텍스트 경로로 변경해 주고...
                cookie.setPath("/");
                int amount = 60 * 60 * 24 * 7; // 일
                cookie.setMaxAge(amount); 
                response.addCookie(cookie);
                
                // currentTimeMills()가 1/1000초 단위임으로 1000곱해서 더해야함
                Date sessionLimit = new Date(System.currentTimeMillis() + (1000*amount));
                
                
                autoLogin.setMemberNo(user.getMemberNo());
                autoLogin.setSessionId(session.getId());
                autoLogin.setLimitTime(sessionLimit);
                service.autoLogin(autoLogin);
                // 현재 세션 id와 유효시간을 사용자 테이블에 저장한다.
			}
			////////////////////////////////////////자동로그인////////

		}else {
			map.put("result", false);
		}
		
		System.out.println(user);
		return map;
	}
	
	@RequestMapping("/signup.json")
	@ResponseBody
	public void signup(Member member, HttpServletRequest request) throws Exception {
		
		String postCode = request.getParameter("postCode");
		String address1 = request.getParameter("address1");
		String address2 = request.getParameter("address2");
		String email1 = request.getParameter("email1");
		String email2 = request.getParameter("email2");

		String adress = "["+postCode+"]"+address1+" "+address2;
		String email = email1+"@"+email2;
		member.setAddress(adress);
		member.setEmail(email);
		member.setType("NORMAL");
        member.setPass(Encryption.getSHA512(member.getPass()));

		
		// root는 공용폴더
		// path는 실제 경로

		String uploadDir = "data/profile";
		System.out.println(uploadDir);

		File f = new File(uploadDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		// 폴더 만들기

		for (MultipartFile file : member.getImages()) {
			if (file.isEmpty())
				continue;

			String oriFileName = file.getOriginalFilename();
			// 원본이름으로 null을 구분

			System.out.println("원본 파일명 : " + oriFileName);

			if (oriFileName != null && !oriFileName.equals("")) {
				// 확장자 처리
				String ext = "";
				int index = oriFileName.lastIndexOf(".");
				if (index != -1) {
					// 파일명에서 확장자명(.포함)을 추출
					ext = oriFileName.substring(index);
				}
				
				// 고유한 파일명 만들기
				String saveFileName = "mlec-" + UUID.randomUUID().toString() + ext;
				System.out.println("저장할 파일명 : " + saveFileName);

				// 임시저장된 파일을 원하는 경로에 저장
				file.transferTo(new File(uploadDir + "/" + saveFileName));
				// transferTo 를 해야 내가 원하는 곳으로 저장된다.
				
				member.setProfileImg(uploadDir + "/" + saveFileName);
			}
		}

		System.out.println(member);
		service.signup(member);
	}

	@RequestMapping("/logout.do")
	public String logout(
			HttpSession session, 
			HttpServletRequest request, HttpServletResponse response ) throws Exception {
		Member user = (Member) session.getAttribute("user");
		if (user != null) {
			session.invalidate();
			
			Cookie userCookie = WebUtils.getCookie(request, "userCookie");
			if(userCookie != null) {
				// null이 아니면 존재하면!
				userCookie.setPath("/");
                // 쿠키는 없앨 때 유효시간을 0으로 설정하는 것 !!! invalidate같은거 없음.
				userCookie.setMaxAge(0);
                // 쿠키 설정을 적용한다.
                response.addCookie(userCookie);
                 
                // 사용자 테이블에서도 유효기간을 현재시간으로 다시 세팅해줘야함.
                service.autoLoginEdit(user.getMemberNo());
			}
		}
		return "redirect:/login/loginform.do";
	}

	// 아이디 체크
	@RequestMapping("/idchk.do")
	@ResponseBody
	public Map<String, Boolean> idChk(String id) throws Exception {
		Map<String, Boolean> map = new HashMap<>();
		boolean result = false;
		if (service.checkId(id) > 0) {
			map.put("result", true);
		} else {
			map.put("result", false);
		}
		return map;
	}
	
	// 비번 아이디 체크
	@RequestMapping("/lock.json")
	@ResponseBody
	public Map<String, Boolean> lock(
			Member member, HttpSession session
			) throws Exception {
		Map<String, Boolean> map = new HashMap<>();
		
		System.out.println(member.toString());

		Member user = service.checkPw(member);
		System.out.println(user);
		if (user != null) {
			user.setLockMode(member.getLockMode());
			service.lockEdit(user);
			
			user.setPass(null);
			session.setAttribute("user", user);
			
			map.put("result", true);
			
		} else {
			map.put("result", false);
		}
		return map;
	}
	
	@RequestMapping("/keysave.json")
	@ResponseBody
	public Map<String, Boolean> keySave(AuthSms authSms) throws Exception {
		Map<String, Boolean> map = new HashMap<>();
		
		String key = authSms.getAuthKey();
		System.out.println(key);
		
		authSms.setAuthKey(null);
		if(service.checkKey(authSms)<1) {
			// 중복되는게 없으면
			
			authSms.setAuthKey(key);
			service.saveKey(authSms);
			map.put("dup", false);
		}else {
			System.out.println(service.checkKey(authSms));
			map.put("dup", true);			
		}
		
		return map;
	}
	
	@RequestMapping("/keydelete.json")
	@ResponseBody
	public void keyDelete(AuthSms authSms) throws Exception {
		service.removeKey(authSms);
	}
	
	@RequestMapping("/keycheck.json")
	@ResponseBody
	public Map<String, Boolean> keyCheck(AuthSms authSms) throws Exception {
		Map<String, Boolean> map = new HashMap<>();
		
		if(service.checkKey(authSms)<1) {
			map.put("result", false);
		}else {
			// 맞는게 있다면
			map.put("result", true);
			service.removeKey(authSms);
		}
		
		return map;
	}
	
	@RequestMapping("/javaexe.json")
	@ResponseBody
	public void javaExe() throws Exception {
		
		Runtime run = Runtime.getRuntime();
		
		  // 컴파일                                                                        //   /usr/local/jdk8/bin/java
												// 	  /usr/local/jdk8/bin/javac
	      Process proc = run.exec(new String[] {"C:\\Program Files\\Java\\jdk1.8.0_141\\bin\\javac", 
	                        "-d", 
	                        "C:\\program-workspaces\\lecture-project\\Test\\src",
	                        "-encoding", 
	                        "utf-8",
	                        "C:\\program-workspaces\\lecture-project\\Test\\src\\Run.java"});
	      // 컴파일 될 때 까지 기달
	      proc.waitFor();
	      
	      // 실행
	      proc = run.exec(
	            new String[] {
	                  "C:\\program-bin\\Java\\jdk-9.0.1\\bin\\java", 
	                  "-Dfile.encoding=UTF8", 
	                  "-cp", 
	                  "C:\\program-workspaces\\lecture-project\\Test\\src", "Run"});
	      InputStream in = proc.getInputStream();
	      BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
	      String result = "";
	      while (true) {
	         String line = br.readLine();
	         if (line == null) {
	            break;
	         }
	         result += line;
	      }
	      System.out.println(result);   

	}
	
	@RequestMapping("/kakao.json")
	@ResponseBody
	public String kakao(String accessToken) throws Exception {
		String result = "";

	       String token = accessToken;
	        String header = "Bearer " + token; // Bearer 다음에 공백 추가
	        try {
	            String apiURL = "https://kapi.kakao.com/v1/user/me";
	            URL url = new URL(apiURL);
	            HttpURLConnection con = (HttpURLConnection)url.openConnection();
	            con.setRequestMethod("GET");
	            con.setRequestProperty("Authorization", header);
	            int responseCode = con.getResponseCode();
	            BufferedReader br;
	            if(responseCode==200) { // 정상 호출
	                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
	            } else {  // 에러 발생
	                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
	            }
	            String inputLine;
	            StringBuffer response = new StringBuffer();
	            while ((inputLine = br.readLine()) != null) {
	                response.append(inputLine);
	            }
	            br.close();
	            result = response.toString();
	        } catch (Exception e) {
	            System.out.println(e);
	        }
	
	        return result;
	}
		

	// 아이디 체크
	@RequestMapping("/sms.json")
	@ResponseBody
	public Map<String, Boolean> sendSms(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String charsetType = "UTF-8"; // EUC-KR 또는 UTF-8
		Map<String, Boolean> map = new HashMap<>();

		request.setCharacterEncoding(charsetType);
		response.setCharacterEncoding(charsetType);
		String action = nullcheck(request.getParameter("action"), "");
		if (action.equals("go")) {

			String sms_url = "";
			sms_url = "https://sslsms.cafe24.com/sms_sender.php"; // SMS 전송요청 URL
			String user_id = base64Encode("cv613"); // SMS아이디
			String secure = base64Encode("da71acb9982e99fc6bb47827703f32f6");// 인증키
			String msg = base64Encode(nullcheck(request.getParameter("msg"), ""));
			String rphone = base64Encode(nullcheck(request.getParameter("rphone"), ""));
			String sphone1 = base64Encode(nullcheck(request.getParameter("sphone1"), ""));
			String sphone2 = base64Encode(nullcheck(request.getParameter("sphone2"), ""));
			String sphone3 = base64Encode(nullcheck(request.getParameter("sphone3"), ""));
			String rdate = base64Encode(nullcheck(request.getParameter("rdate"), ""));
			String rtime = base64Encode(nullcheck(request.getParameter("rtime"), ""));
			String mode = base64Encode("1");
			String subject = "";
			if (nullcheck(request.getParameter("smsType"), "").equals("L")) {
				subject = base64Encode(nullcheck(request.getParameter("subject"), ""));
			}
			String testflag = base64Encode(nullcheck(request.getParameter("testflag"), ""));
			String destination = base64Encode(nullcheck(request.getParameter("destination"), ""));
			String repeatFlag = base64Encode(nullcheck(request.getParameter("repeatFlag"), ""));
			String repeatNum = base64Encode(nullcheck(request.getParameter("repeatNum"), ""));
			String repeatTime = base64Encode(nullcheck(request.getParameter("repeatTime"), ""));
			String returnurl = nullcheck(request.getParameter("returnurl"), "");
			String nointeractive = nullcheck(request.getParameter("nointeractive"), "");
			String smsType = base64Encode(nullcheck(request.getParameter("smsType"), ""));

			String[] host_info = sms_url.split("/");
			String host = host_info[2];
			String path = "/" + host_info[3];
			int port = 80;

			// 데이터 맵핑 변수 정의
			String arrKey[] = new String[] { "user_id", "secure", "msg", "rphone", "sphone1", "sphone2", "sphone3",
					"rdate", "rtime", "mode", "testflag", "destination", "repeatFlag", "repeatNum", "repeatTime",
					"smsType", "subject" };
			String valKey[] = new String[arrKey.length];
			valKey[0] = user_id;
			valKey[1] = secure;
			valKey[2] = msg;
			valKey[3] = rphone;
			valKey[4] = sphone1;
			valKey[5] = sphone2;
			valKey[6] = sphone3;
			valKey[7] = rdate;
			valKey[8] = rtime;
			valKey[9] = mode;
			valKey[10] = testflag;
			valKey[11] = destination;
			valKey[12] = repeatFlag;
			valKey[13] = repeatNum;
			valKey[14] = repeatTime;
			valKey[15] = smsType;
			valKey[16] = subject;

			String boundary = "";
			Random rnd = new Random();
			String rndKey = Integer.toString(rnd.nextInt(32000));
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytData = rndKey.getBytes();
			md.update(bytData);
			byte[] digest = md.digest();
			for (int i = 0; i < digest.length; i++) {
				boundary = boundary + Integer.toHexString(digest[i] & 0xFF);
			}
			boundary = "---------------------" + boundary.substring(0, 11);

			// 본문 생성
			String data = "";
			String index = "";
			String value = "";
			for (int i = 0; i < arrKey.length; i++) {
				index = arrKey[i];
				value = valKey[i];
				data += "--" + boundary + "\r\n";
				data += "Content-Disposition: form-data; name=\"" + index + "\"\r\n";
				data += "\r\n" + value + "\r\n";
				data += "--" + boundary + "\r\n";
			}

			// out.println(data);

			InetAddress addr = InetAddress.getByName(host);
			Socket socket = new Socket(host, port);
			// 헤더 전송
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), charsetType));
			wr.write("POST " + path + " HTTP/1.0\r\n");
			wr.write("Content-Length: " + data.length() + "\r\n");
			wr.write("Content-type: multipart/form-data, boundary=" + boundary + "\r\n");
			wr.write("\r\n");

			// 데이터 전송
			wr.write(data);
			wr.flush();

			// 결과값 얻기
			BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream(), charsetType));
			String line;
			String alert = "";
			ArrayList tmpArr = new ArrayList();
			while ((line = rd.readLine()) != null) {
				tmpArr.add(line);
			}
			wr.close();
			rd.close();

			String tmpMsg = (String) tmpArr.get(tmpArr.size() - 1);
			String[] rMsg = tmpMsg.split(",");
			String Result = rMsg[0]; // 발송결과
			String Count = ""; // 잔여건수
			if (rMsg.length > 1) {
				Count = rMsg[1];
			}

			// 발송결과 알림
			if (Result.equals("success")) {
				alert = "성공적으로 발송하였습니다.";
				alert += " 잔여건수는 " + Count + "건 입니다.";
			} else if (Result.equals("reserved")) {
				alert = "성공적으로 예약되었습니다";
				alert += " 잔여건수는 " + Count + "건 입니다.";
			} else if (Result.equals("3205")) {
				alert = "잘못된 번호형식입니다.";
			} else {
				alert = "[Error]" + Result;
			}

			System.out.println(nointeractive);

			if (nointeractive.equals("1") && !(Result.equals("Test Success!")) && !(Result.equals("success"))
					&& !(Result.equals("reserved"))) {
//				실패
				System.out.println("실괘"+alert);
				map.put("result", false);
			} else if (!(nointeractive.equals("1"))) {
//				성공
				map.put("result", true);
				System.out.println("싱공!"+alert);
			}
			
		}
		return map;
	}

	public static String nullcheck(String str, String Defaultvalue) throws Exception {
		String ReturnDefault = "";
		if (str == null) {
			ReturnDefault = Defaultvalue;
		} else if (str == "") {
			ReturnDefault = Defaultvalue;
		} else {
			ReturnDefault = str;
		}
		return ReturnDefault;
	}

	/**
	 * BASE64 Encoder
	 * 
	 * @param str
	 * @return
	 */
	public static String base64Encode(String str) throws java.io.IOException {
		sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
		byte[] strByte = str.getBytes();
		String result = encoder.encode(strByte);
		return result;
	}

	/**
	 * BASE64 Decoder
	 * 
	 * @param str
	 * @return
	 */
	public static String base64Decode(String str) throws java.io.IOException {
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		byte[] strByte = decoder.decodeBuffer(str);
		String result = new String(strByte);
		return result;
	}
	
	
	

}
