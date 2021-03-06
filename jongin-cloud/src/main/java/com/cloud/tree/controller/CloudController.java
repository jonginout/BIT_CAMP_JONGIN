package com.cloud.tree.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cloud.login.service.LoginService;
import com.cloud.repository.vo.Member;
import com.cloud.repository.vo.Tree;

@Controller
@RequestMapping("/cloud")
public class CloudController {

	@Autowired
	LoginService service;

	long folderSize(File f) {
		long length = 0;
		for (File file : f.listFiles()) {
			if (file.isFile()) {
				length += file.length();
			} else {
				length += folderSize(file);
			}
		}
		return length;
	}

	// ff 폴더 안 모든 파일 및 폴더 검색
	List<Tree> pullFile(String path) {

		File[] list = new File(path).listFiles();
		List<Tree> trees = new ArrayList<>();

		try {
			for (File ff : list) {

				// jci 파일 제외
				if (FilenameUtils.getExtension(ff.getName()).equals("jci")) {
					continue;
				}

				Tree tree = new Tree();

				Date updateDate = new Date(ff.lastModified());
				tree.setUpdateDate(updateDate);
				tree.setTitle(ff.getName());
				tree.setPath(ff.toString());

				if (ff.isFile()) {
					System.out.println("파일 : " + ff.getName());
					tree.setExt(FilenameUtils.getExtension(ff.getName()));
					tree.setSize(ff.length());

				} else if (ff.isDirectory()) {
					tree.setIsFolder(true);
					tree.setIsLazy(true);
					tree.setSize(folderSize(ff));
					System.out.println("폴더 : " + ff.getName());

					// pullFile(ff.toString());
				}
				trees.add(tree);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return trees;
	}

	void deleteFile(String path) {

		File file = new File(path);
		File[] fList = file.listFiles();

		if (!file.isDirectory()) {
			file.delete();
		} else {
			for (File f : fList) {
				deleteFile(f.toString());
			}
		}

		file.delete();

	}

	@RequestMapping("/cloud.do")
	public void cloud() throws Exception {
	}

	@RequestMapping("/sublist.json")
	@ResponseBody
	public List<Tree> subTree(String path) throws Exception {

		System.out.println(path);

		List<Tree> trees = pullFile(path);
		System.out.println(trees.toString());

		return trees;

	}

	@RequestMapping("/list.json")
	@ResponseBody
	public Tree rootTree(HttpSession session) throws Exception {

		Member m = (Member) session.getAttribute("user");
		String user = m.getId();

		String path = "cloud/" + user;

//		String folder = "/cloud/" + user;
//		String cmd = "chmod 777 " + folder; 
//		Runtime rt = Runtime.getRuntime();
//		Process prc = rt.exec(cmd);
//		prc.waitFor();
		
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}

		List<Tree> trees = pullFile(path);
		Tree tree = new Tree();

		Date updateDate = new Date(f.lastModified());

		System.out.println(trees.toString());
		tree.setMaxSize(m.getMaxVolume());
		tree.setSize(folderSize(f));
		tree.setTitle(user);
		tree.setIsFolder(true);
		tree.setPath(path);
		tree.setIsLazy(true);
		tree.setChildren(trees);
		tree.setUpdateDate(updateDate);

		return tree;

	}

	@RequestMapping("/newfolder.json")
	@ResponseBody
	public Map<String, Object> newfolder(String path, String name) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("dup", true);

		File f = new File(path + "/" + name);
		if (!f.exists()) {
			map.put("dup", false);
			f.mkdirs();
		}

		return map;
	}

	@RequestMapping("/newCode.json")
	@ResponseBody
	public Map<String, Object> newCode(String path, String name) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("dup", true);

		File f = new File(path + "/" + name);
		if (!f.exists()) {
			map.put("dup", false);
			f.createNewFile();
		}

		return map;
	}

	@RequestMapping("/filedelete.json")
	@ResponseBody
	public void fileDelete(String path) throws Exception {

		String jci = path + ".jci";

		deleteFile(path);
		deleteFile(jci);
	}

	@RequestMapping("/filerename.json")
	@ResponseBody
	public Map<String, Object> fileRename(String path, String rename, String nowHost) throws Exception {

		System.out.println(nowHost);

		String slash = "/";
		if (nowHost.equals("localhost")) {
			slash = "\\";
		}

		Map<String, Object> map = new HashMap<>();
		File f = new File(path);
		Path file = Paths.get(path);

		String ext = "";
		System.out.println("옮길거 : " + path);

		if (f.isDirectory()) {
			path = path.substring(0, path.lastIndexOf(slash) + 1) + rename;
		} else {
			if (f.getName().lastIndexOf(".") != -1) {
				ext = f.getName().substring(f.getName().lastIndexOf("."));
			}
			path = path.substring(0, path.lastIndexOf(slash) + 1) + rename + ext;
		}

		System.out.println("바뀐것 : " + path);

		try {
			Files.move(file, Paths.get(path));
			map.put("result", true);
		} catch (Exception e) {
			System.out.println(e);
			map.put("result", false);
		}
		return map;
	}

	@RequestMapping("/filemove.json")
	@ResponseBody
	public Map<String, Object> fileMove(String moveFilePath, String recFilePath) throws Exception {
		Map<String, Object> map = new HashMap<>();

		System.out.println("옮길 것 : " + moveFilePath);

		Path moveF = Paths.get(moveFilePath);
		File recF = new File(recFilePath);

		if (!recF.isDirectory()) {
			map.put("result", false);
			return map;
		}

		System.out.println(recFilePath);
		Path recPath = Paths.get(recFilePath + "/" + moveF.getFileName());
		System.out.println("옮겨질 곳 : " + recPath);

		try {
			Files.move(moveF, recPath);
			map.put("result", true);
			map.put("path", "" + recPath);
		} catch (Exception e) {
			System.out.println(e);
			map.put("result", false);
		}

		return map;

	}

	@RequestMapping("/codeview.json")
	@ResponseBody
	public Map<String, Object> codeView(String path) throws Exception {
		Map<String, Object> map = new HashMap<>();

//		FileReader fr = new FileReader(path);
//		BufferedReader br = new BufferedReader(fr);
		
		File file = new File(path);
//		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF8"));

		String line;
		String code = "";
		while ((line = br.readLine()) != null) {
			code += line + "\n";
		}

		map.put("code", code);
		return map;
	}

	@RequestMapping("/codechange.json")
	@ResponseBody
	public void codeChange(String path, String changeCode) throws Exception {

		deleteFile(path);

		File file = new File(path);
		
		FileOutputStream fileStream = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fileStream, "UTF-8");
		
		osw.write(changeCode);
		osw.close();

	}

	@RequestMapping("/fileupload.json")
	@ResponseBody
	public Map<String, Object> fileUpload(String uploadPath, MultipartFile[] files) throws Exception {

		System.out.println("================");

		Map<String, Object> map = new HashMap<>();

		System.out.println("업로드 할 경로 : " + uploadPath);

		File[] list = new File(uploadPath).listFiles();
		List<String> fileNameList = new ArrayList<>();

		// 업로드할 경로에 존재하는 파일을 읽는다 (중복확인용)
		for (File f : list) {
			fileNameList.add(f.getName());
		}

		for (MultipartFile f : files) {
			if (f.isEmpty()) {
				continue;
			}

			String oriFileName = f.getOriginalFilename();
			String saveFileName = oriFileName;

			if (fileNameList.indexOf(oriFileName) != -1) {
				String ori = oriFileName.substring(0, oriFileName.lastIndexOf("."));
				SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd-hh-mm-ss");
				String hash = "(" + sdf.format(new Date()) + ")";
				String ext = oriFileName.substring(oriFileName.lastIndexOf("."));

				saveFileName = ori + hash + ext;

				map.put("dup", true);
			}

			System.out.println("업로드한 파일 명 : " + oriFileName);

			if (oriFileName != null && !oriFileName.equals("")) {
				long fileSize = f.getSize();
				System.out.println("파일 사이즈 : " + fileSize);

				f.transferTo(new File(uploadPath + "/" + saveFileName));
			}

		}

		System.out.println("================");
		return map;

	}

	@RequestMapping("/filecomment.json")
	@ResponseBody
	public void fileComment(String comment, Tree tree) throws Exception {

		String commentPath = tree.getPath() + "/" + tree.getTitle() + ".jci";

		deleteFile(commentPath);

		File file = new File(commentPath);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file);
		fw.write(comment);
		fw.close();

	}

	@RequestMapping("/commentview.json")
	@ResponseBody
	public String commentView(Tree tree) throws Exception {

		String commentPath = tree.getPath() + "/" + tree.getTitle() + ".jci";

		File file = new File(commentPath);
		if (!file.exists()) {
			return null;
		}

		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		String line;
		String comment = "";
		while ((line = br.readLine()) != null) {
			comment += line + "\n";
		}

		return comment;
	}

	@RequestMapping("/detecttext.json")
	@ResponseBody
	public String detectText(String filePath) throws Exception, IOException {

		String base64Code = fileToString(new File(filePath));
		
		String TARGET_URL = "https://vision.googleapis.com/v1/images:annotate?";
		String API_KEY = "key=AIzaSyBEYeqtSfixGg0e4qlRnSOwNL3w4V38HfY";
		
		URL serverUrl = new URL(TARGET_URL + API_KEY);
		URLConnection urlConnection = serverUrl.openConnection();
		HttpURLConnection httpConnection = (HttpURLConnection)urlConnection;
		
		httpConnection.setRequestMethod("POST");
		httpConnection.setRequestProperty("Content-Type", "application/json");

		httpConnection.setDoOutput(true);
		
		BufferedWriter httpRequestBodyWriter = new BufferedWriter(
				new OutputStreamWriter(httpConnection.getOutputStream()));
		httpRequestBodyWriter.write("{" + 
				"  \"requests\": [" + 
				"    {" + 
				"      \"image\": {" + 
				"        \"content\": \""+base64Code+"\"" + 
				"      }," + 
				"      \"features\": [" + 
				"        {" + 
				"            \"type\" : \"TEXT_DETECTION\"" + 
				"        }" + 
				"      ]" + 
				"    }" + 
				"  ]" + 
				"}");
		httpRequestBodyWriter.close();
		
		String response = httpConnection.getResponseMessage();
		
		if (httpConnection.getInputStream() == null) {
			   System.out.println("No stream");
			   return "";
			}

			Scanner httpResponseScanner = new Scanner (httpConnection.getInputStream());
			String resp = "";
			while (httpResponseScanner.hasNext()) {
			   String line = httpResponseScanner.nextLine();
			   resp += line;
			   System.out.println(line);  //  alternatively, print the line of response
			}
			httpResponseScanner.close();

			return resp;
	}
	
	
	public String fileToString(File file) throws Exception {

		String fileString = new String();
		FileInputStream inputStream = null;
		ByteArrayOutputStream byteOutStream = null;


		inputStream = new FileInputStream(file);
		byteOutStream = new ByteArrayOutputStream();

		int len = 0;
		byte[] buf = new byte[1024];
		while ((len = inputStream.read(buf)) != -1) {
			byteOutStream.write(buf, 0, len);
		}

		byte[] fileArray = byteOutStream.toByteArray();
		fileString = new String(Base64.encodeBase64(fileArray));

		inputStream.close();
		byteOutStream.close();
		

		return fileString;
	}


}
