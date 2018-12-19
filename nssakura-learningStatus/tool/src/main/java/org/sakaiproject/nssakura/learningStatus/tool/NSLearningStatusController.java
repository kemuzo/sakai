package org.sakaiproject.nssakura.learningStatus.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.nssakura.learningStatus.logic.LearningStatusSakaiProxy;
import org.sakaiproject.nssakura.learningStatus.model.GradeCourse;
import org.sakaiproject.nssakura.learningStatus.model.GradeSectionModel;
import org.sakaiproject.nssakura.learningStatus.model.GradeUserModel;
import org.sakaiproject.nssakura.section.model.SectionModel;
import org.sakaiproject.user.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes(value={"gradeCourse"})
public class NSLearningStatusController extends NSLearningStatusCsvController{

	/**
	 * Hello World Controller
	 * 
	 * @author Mike Jennings (mike_jennings@unc.edu)
	 * 
	 */
	@Autowired
	private LearningStatusSakaiProxy lsSakaiProxy = null;

	@RequestMapping(value="/getJson", method=RequestMethod.GET)
	@ResponseBody
	public String getJSON(HttpServletRequest request,
			HttpServletResponse response){
		GradeCourse gradeCourse =  getGradeCourse(request);
		String result = "{\"process\":\"success\",\"msgs\":\"done\"}";
		return result;
	}
	@RequestMapping(value="/top", method=RequestMethod.GET)
	public ModelAndView top(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> map = getMap();
		List<SectionModel> models = lsSakaiProxy.getSectionModels();
		int n = 0;
		if(models != null ){
			n = models.size();
		}
		map.put("sectionNum", n);
		GradeCourse gradeCourse = (GradeCourse)request.getSession().getAttribute("gradeCourse");
		if(checkGradeCourse(gradeCourse)){
			map.put("gradeCourse", gradeCourse);
			map.put("currentSiteId", lsSakaiProxy.getCurrentSiteId());
			map.put("userDisplayName", lsSakaiProxy.getCurrentUserDisplayName());
			return new ModelAndView("index", map);
		}
		return new ModelAndView("top", map);
	}
	
	@RequestMapping(value="/index")
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> map = getMap();
		GradeCourse gradeCourse = (GradeCourse)request.getSession().getAttribute("gradeCourse");
		if(!checkGradeCourse(gradeCourse)){
			gradeCourse = getGradeCourse(request);
		}
		if(gradeCourse != null ){
			map.put("gradeCourse", gradeCourse);
			List<String> titleList = gradeCourse.getTitleList();
			if(titleList==null || titleList.isEmpty()){
				map.put("err", rb.getString("msgs_no_gradeitem"));
			}
		}
		map.put("currentSiteId", lsSakaiProxy.getCurrentSiteId());
		map.put("userDisplayName", lsSakaiProxy.getCurrentUserDisplayName());
		
		return new ModelAndView("index", map);
	}

	@RequestMapping(value="/csvout")
	public void csvout(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		GradeCourse gradeCourse = (GradeCourse)request.getSession().getAttribute("gradeCourse");
		if(! checkGradeCourse(gradeCourse)){
			gradeCourse = getGradeCourse(request);
		}
		outputCsv(response, gradeCourse.getSections());
	}

	@RequestMapping(value="/sectionGrade")
	public ModelAndView sectionGrade(@RequestParam("sectionId") String sectionId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> map = getMap();
		List<GradeUserModel> gumodels = lsSakaiProxy.getGradebookDataBySection(sectionId);
		if(gumodels != null && gumodels.size()>0){
			map.put("gradeData", gumodels);
			map.put("sectionId", sectionId);
			map.put("sectionTitle", lsSakaiProxy.getSectionTitle(sectionId));
			map.put("assignments", gumodels.get(0).getAssignmentItems());
			List<String> titleList = gumodels.get(0).getAssignmentTitleList();
			if(titleList==null || titleList.isEmpty()){
				map.put("err", rb.getString("msgs_no_gradeitem"));
			}
			List<User> tas = lsSakaiProxy.getTa(sectionId);
			if(tas != null && tas.size()>0){
				map.put("tas", tas);
			}
		}
		return new ModelAndView("sectionGrade",map);
	}

	@RequestMapping(value="/csvoutStudent")
	public void csvoutStudent(@RequestParam("sectionId") String sectionId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<GradeUserModel> gumodels = lsSakaiProxy.getGradebookDataBySection(sectionId);
		if(gumodels != null && gumodels.size()>0){
			outputCsvStudent(response,gumodels);
		}
	}

	@RequestMapping(value="/csvoutNonexec")
	public void csvoutNoexec(@RequestParam("sectionId") String sectionId, @RequestParam("assignmentId") String assignmentId,
			HttpServletRequest request,HttpServletResponse response) throws Exception {
		// sectionId, assginmentIdから未受講者（未完了者）のユーザリストを取得する
		List<User> users = lsSakaiProxy.getNonexecUsers(sectionId, assignmentId);
		outputCsvUserEids(response,users);
	}

	//-------------------------------------------------------//
	private boolean checkGradeCourse(GradeCourse gradeCourse){
		try{
			String siteId = gradeCourse.getSiteId();
			if(siteId.equals(lsSakaiProxy.getCurrentSiteId())){
				return true;
			}
		}catch(Exception e){
		}
		return false;
	}
	
	private GradeCourse getGradeCourse(HttpServletRequest request){
		List<GradeSectionModel> gradeList = lsSakaiProxy.getGradebookData();
		GradeCourse gradeCourse = new GradeCourse(lsSakaiProxy.getCurrentSiteId(), gradeList);
		request.getSession().setAttribute("gradeCourse", gradeCourse);
		return gradeCourse;
		
	}
	
	private Map<String, Object> getMap(){
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("superuser", lsSakaiProxy.isSuperUser());
		map.put("instructor", lsSakaiProxy.isInstructor());
		return map;
	}
}
