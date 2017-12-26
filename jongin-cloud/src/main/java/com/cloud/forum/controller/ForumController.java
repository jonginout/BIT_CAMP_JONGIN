package com.cloud.forum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cloud.forum.service.ForumService;
import com.cloud.repository.vo.Forum;
import com.google.gson.Gson;

@Controller
@RequestMapping("/forum")
public class ForumController {
	
	@Autowired
	ForumService service;
	
	@RequestMapping("/forum.do")
	public ModelAndView forum() throws Exception {
		ModelAndView mav = new ModelAndView();
		
		Forum forum = new Forum();
		forum.setStart(0);
		forum.setCount(20);
		
		List<Forum> forums = service.forumList(forum);
		for (Forum f : forums) {
			if(f.getCategory().equals("FREE")) {
				f.setCategory("자유");
			}
		}
		mav.addObject("forums", forums);
		mav.addObject("forumsJSON", new Gson().toJson(forums));
		return mav;
	}

	@RequestMapping("/forum.json")
	@ResponseBody
	public String forumJson(Forum forum) throws Exception {
		
		if(forum.getStart()==null) {
			forum.setStart(0);
			forum.setCount(20);
		}
		
		List<Forum> forums = service.forumList(forum);
		for (Forum f : forums) {
			if(f.getCategory().equals("FREE")) {
				f.setCategory("자유");
			}
		}
		
		return new Gson().toJson(forums);
	}
	
}
