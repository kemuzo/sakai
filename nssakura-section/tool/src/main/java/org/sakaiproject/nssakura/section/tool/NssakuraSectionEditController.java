package org.sakaiproject.nssakura.section.tool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import org.sakaiproject.nssakura.section.model.SectionModel;

public class NssakuraSectionEditController extends NssakuraSectionBase implements Controller {

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

		if(returnSubmit == null || returnSubmit.length() < 1){
			//削除処理
			String sectionIds[] = ServletRequestUtils.getStringParameters(req, "sectionremove");
			projectLogic.getSakaiProxy().disbandSections(new HashSet(Arrays.asList(sectionIds)));
			map.put("msg", rb.getString("msgs_section_removed"));
		}
		
		return new ModelAndView(goIndex(map), map);
	}

}
