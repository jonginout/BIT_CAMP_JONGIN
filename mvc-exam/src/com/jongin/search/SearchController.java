package com.jongin.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.mvc.Controller;
import org.springframework.web.mvc.ModelAndView;
import org.springframework.web.mvc.RequestMapping;
import org.springframework.web.mvc.RequestParam;

import com.google.gson.Gson;
import com.jongin.common.PageResult;
import com.jongin.religion.ReligionDomain;
import com.jongin.religion.ReligionMapper;
import com.jongin.school.SchoolDomain;
import com.jongin.school.SchoolMapper;
import com.jongin.skill.SkillDomain;
import com.jongin.skill.SkillMapper;
import com.jongin.staff.StaffDomain;
import com.jongin.staff.StaffMapper;
import com.jongin.staffSkill.StaffSkillDomain;
import com.jongin.staffSkill.StaffSkillMapper;


@Controller
public class SearchController {

	//검색 메인
	@RequestMapping(value="/search/main.json")
	public Map<String, Object> main(
			@RequestParam(name="pageNo", defaultValue="1") int pageNo,
			HttpServletRequest request, StaffDomain staff)
					throws Exception {
		
		//스태프를 페이지에 상속
		
		System.out.println(staff);
		
		String [] skillCodes = request.getParameterValues("skillCode");
		System.out.println("skillCodesQ : "+request.getParameter("skillCode"));
		System.out.println("skillCodes : "+skillCodes);

		int listPerPage = 5; // 페이지 당 리스트
		int pagePerBlock = 3;	// 블록당 페이지 수
		staff.setPageNo(pageNo);
		staff.setListPerPage(listPerPage);

		SkillMapper skillDao = new SkillMapper();
		SchoolMapper schoolDao = new SchoolMapper();
		ReligionMapper religionDao = new ReligionMapper();
		StaffMapper staffDao = new StaffMapper();
		
		staff.setSkillCodes(skillCodes);
		int staffListCount = staffDao.listStaffCount(staff);
		
		
		PageResult pageResult = new PageResult(pageNo, staffListCount, pagePerBlock, listPerPage);
		
		
		Map<String, Object> obj = new HashMap<>();
		
        obj.put("skillCodes", staff.getSkillCodes());
		obj.put("skillList", skillDao.listSkill());
		obj.put("schoolList", schoolDao.listSchool());
		obj.put("religionList", religionDao.listReligion());
		obj.put("staffList", staffDao.listStaff(staff));
		obj.put("staffListCount", staffListCount);
		obj.put("pageResult", pageResult);
		
		return obj;

	}
	
	
	@RequestMapping(value="/search/insert.do")
	public String insert(
			StaffDomain staff , StaffSkillDomain staffSkill,
			HttpServletRequest request
			) throws Exception {


		String skillCodes[] = request.getParameterValues("skillCode");

		
		StaffMapper StaffDao = new StaffMapper();
		StaffSkillMapper staffSkillDao = new StaffSkillMapper();
		
		int nextStaffNo = StaffDao.staffGetNextStaffNo();
		staff.setStaffNo(nextStaffNo);
		StaffDao.insertStaff(staff);
		
		for (String skill : skillCodes) {
			System.out.println("선택한스킬"+skill);
			staffSkill.setStaffNo(nextStaffNo); //스태프
			staffSkill.setSkillCode(Integer.parseInt(skill));
			staffSkillDao.insertStaffSkill(staffSkill);			
		}

		return "redirect:/search/main.do";
	}
	
	@RequestMapping(value = "/search/insertfrom.do")
	public ModelAndView insertFrom() throws Exception {
		
		SkillMapper skillDao = new SkillMapper();
		SchoolMapper schoolDao = new SchoolMapper();
		ReligionMapper religionDao = new ReligionMapper();

		List<SkillDomain> skillList = skillDao.listSkill();
		List<SchoolDomain> schoolList = schoolDao.listSchool();
		List<ReligionDomain> religionList = religionDao.listReligion();

		ModelAndView mav = new ModelAndView("/search/insertfrom.jsp");
		mav.addAttribute("skillList", skillList);
		mav.addAttribute("schoolList", schoolList);
		mav.addAttribute("religionList", religionList);

		return mav;

	}

	@RequestMapping(value = "/search/modifyform.do")
	public ModelAndView modifyFrom(int staffNo) throws Exception {
		
		SkillMapper skillDao = new SkillMapper();
		SchoolMapper schoolDao = new SchoolMapper();
		ReligionMapper religionDao = new ReligionMapper();
		StaffMapper StaffDao = new StaffMapper();
		StaffSkillMapper staffSkillDao = new StaffSkillMapper();

		List<SkillDomain> skillList = skillDao.listSkill();
		List<SchoolDomain> schoolList = schoolDao.listSchool();
		List<ReligionDomain> religionList = religionDao.listReligion();
		

		List<StaffSkillDomain> staffSkill = staffSkillDao.detailStaffSkill(staffNo);
		StaffDomain staff = StaffDao.detailStaff(staffNo);
		
		
		ModelAndView mav = new ModelAndView("/search/modifyform.jsp");
		mav.addAttribute("skillList", skillList);
		mav.addAttribute("schoolList", schoolList);
		mav.addAttribute("religionList", religionList);
		
		Gson gson = new Gson();
        String result = gson.toJson(staffSkill);
		mav.addAttribute("staffSkill", result);
		mav.addAttribute("staff", staff);
		
		return mav;
		
	}
	
	@RequestMapping(value="/search/modify.do")
	public String modify(
			StaffDomain staff , StaffSkillDomain staffSkill,
			HttpServletRequest request
			) throws Exception {
		
		
		String skillCodes[] = request.getParameterValues("skillCode");
		
		StaffMapper StaffDao = new StaffMapper();
		StaffSkillMapper staffSkillDao = new StaffSkillMapper();
		
		staffSkillDao.deleteStaffSkill(staff.getStaffNo());
		StaffDao.modifyStaff(staff);
		
		for (String skill : skillCodes) {
			System.out.println("선택한스킬"+skill);
			staffSkill.setStaffNo(staff.getStaffNo()); //스태프
			staffSkill.setSkillCode(Integer.parseInt(skill));
			staffSkillDao.insertStaffSkill(staffSkill);			
		}
		
		return "redirect:/search/main.do";
	}

	
	@RequestMapping(value="/search/delete.do")
	public String delete(int staffNo) throws Exception {
		
		StaffMapper StaffDao = new StaffMapper();
		StaffSkillMapper staffSkillDao = new StaffSkillMapper();

		staffSkillDao.deleteStaffSkill(staffNo);
		StaffDao.deleteStaff(staffNo);

		return "redirect:/search/main.do";
		
	}
	
}
