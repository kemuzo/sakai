package org.sakaiproject.nssakura.section.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.nssakura.section.model.SectionModel;
import org.sakaiproject.nssakura.section.model.SelectItem;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class NssakuraAssignTaController extends NssakuraSectionBase implements Controller {

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

		//Cancel
		if(returnSubmit != null && returnSubmit.length() > 0){
			return new ModelAndView(goIndex(map), map);
		}

		//for action
		String action = ServletRequestUtils.getStringParameter(req, "action");
		String sectionId = ServletRequestUtils.getStringParameter(req, "sectionId");
		CourseSection section = projectLogic.getSakaiProxy().getSection(sectionId);
		// update tas
		if (ACTION_UPDATE_TA .equals(action)){
			String availableUsers[] = ServletRequestUtils.getStringParameters(req, "availableUsers");
			String selectedUsers[] = ServletRequestUtils.getStringParameters(req, "selectedUsers");
			List<String>[]result  = projectLogic.setSectionTas(selectedUsers, sectionId);
			String[] param = new String[1];
			param[0] = section.getTitle();
			String errmess = "";
			if((!result[0].isEmpty()) || (!result[1].isEmpty())){
				//success
				map.put("msg",rb.getFormattedMessage("msgs_success_update_ta",param));
			}else{
				//failuer
				errmess = rb.getFormattedMessage("msgs_unsuccess_update_ta", param);
			}
			if(! result[2].isEmpty()){
					errmess += "<br/>" + rb.getFormattedMessage("msgs_unsucess_update_ta_detail", concat(result[2]));
			}
			if( errmess!=null && errmess.length()>0){
				map.put("err",errmess);
			}
			return new ModelAndView(goIndex(map), map);
		}
		Map<String,List<SelectItem>> taSelectItemMap = projectLogic.getSectionTasForRegister(sectionId);
		List<SelectItem>selectedUsers = taSelectItemMap.get("selectedUsers");
		List<SelectItem>availableUsers = taSelectItemMap.get("availableUsers");
		map.put("selectedUsers", selectedUsers);
		map.put("availableUsers",availableUsers);
		map.put("section", section);
		
		return new ModelAndView(DISPLAY_NAME_ASSIGNTA, map);
	}

	private String concat(List<String> datas){
		if( datas == null || datas.isEmpty()){
			return "";
		}
		String result = datas.get(0);
		for(int i=1; i < datas.size(); i++){
			result += "," + datas.get(i);
		}
		return result;
	}
}
