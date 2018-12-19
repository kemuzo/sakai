package org.sakaiproject.nssakura.section.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

import org.sakaiproject.nssakura.section.logic.NssakuraSection;
import org.sakaiproject.nssakura.section.model.SectionModel;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class NssakuraSectionAddController extends NssakuraSectionBase implements Controller {

	/**
	 * Hello World Controller
	 * 
	 * @author Mike Jennings (mike_jennings@unc.edu)
	 * 
	 */
	
	public ModelAndView handleRequest(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		String returnSubmit = ServletRequestUtils.getStringParameter(req, "cancelButton");	

		Map<String, Object> map = getInitMap();

		// cancel
		if(returnSubmit != null && returnSubmit.length() > 0){
			return new ModelAndView(goIndex(map), map);
		}

		//for action
		String action = ServletRequestUtils.getStringParameter(req, "action");
		// add sections
		if (ACTION_ADD_SECTIONS.equals(action)){
			String sectionNames[] = ServletRequestUtils.getStringParameters(req, "sectionadd");
			if(sectionNames.length > 0){
				// 追加
				Collection<CourseSection> addedSections = nssakuraSection.addSections(sectionNames);
				if( addedSections == null || addedSections.isEmpty()){
					map.put("error", rb.getString("msgs_error_add_sections"));
				}else{
					String resultMess = rb.getString("msgs_add_sections");
					map.put("msg", resultMess + getSectionNamesMess(addedSections));
				}
				List<SectionModel> sections = nssakuraSection.getSectionModels();
				map.put("sections", sections);
				return new ModelAndView(DISPLAY_NAME_INDEX, map);
			}else{
				// 未選択
				map.put("err", rb.getString("msgs_adding_section_not_selected"));
			}
		}else if (ACTION_ADD_AUTO_SECTIONS.equals(action)){
			nssakuraSection.rebuildSections();
			map.put("msg", rb.getString("msgs_rebuild_sections"));
			return new ModelAndView(goIndex(map),map);
		}
		
		return new ModelAndView(goAddSection(map), map);
	}

	private String getSectionNamesMess(Collection<CourseSection> sections){
		String result = "<ul>";
		List<String> resultList = new ArrayList<String>();
		for(Iterator it=sections.iterator(); it.hasNext();){
			CourseSection section = (CourseSection)it.next();
			result = result + "<li>" + section.getTitle() + "</li>";
		}
		result = result + "</ul>";
		return result;
	}
	
	@Setter @Getter
	private NssakuraSection nssakuraSection = null;

}
