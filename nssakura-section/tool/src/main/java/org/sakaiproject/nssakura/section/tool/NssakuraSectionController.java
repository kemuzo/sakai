package org.sakaiproject.nssakura.section.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.nssakura.section.model.SectionModel;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class NssakuraSectionController extends NssakuraSectionBase implements Controller {

	/**
	 * Hello World Controller
	 * 
	 * @author Mike Jennings (mike_jennings@unc.edu)
	 * 
	 */

	public ModelAndView handleRequest(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		
		
		Map<String, Object> map = getInitMap();

		List<SectionModel> sections = projectLogic.getSectionModels();

		//for action
		String action = ServletRequestUtils.getStringParameter(req, "action");
		// del sections
		if (ACTION_UPDATE_SECTIONS.equals(action)){
			String sectionIds[] = ServletRequestUtils.getStringParameters(req, "sectionremove");
			if(sectionIds.length > 0){
				// 削除確認画面に遷移
				map.put("sections", extractSectionModels(sections, sectionIds));
				map.put("sectionremove", sectionIds);
				return new ModelAndView("deleteSections", map);
			}else{
				map.put("err", rb.getString("msgs_removing_section_not_selected"));
			}
		}
		map.put("sections", sections);
		String[] param = new String[1];
		param[0] = String.valueOf(getSectionMemberCount(sections));
		map.put("membersCountMsg", rb.getFormattedMessage("label_section_members_count", param));
		return new ModelAndView(DISPLAY_NAME_INDEX, map);
	}

	/**
	 * SectionModelリストからsectionIdをキーに抽出する
	 * @param sections
	 * @param sectionIds
	 * @return
	 */
	private List<SectionModel> extractSectionModels(List<SectionModel> sections, String[] sectionIds){
		TreeMap<String,SectionModel> hash = new TreeMap<String,SectionModel>();
		for ( SectionModel section : sections){
			hash.put(section.getSection().getUuid(), section);
		}
		List<SectionModel> extractSections = new ArrayList<SectionModel>();
		for ( String id : sectionIds){
			SectionModel model = hash.get(id);
			if(model != null){
				extractSections.add(model);
			}
		}
		return extractSections;
	}
	
	/**
	 * SectionModelリストの受講生の合計人数を帰す
	 * @param sections
	 * @return 受講生合計人数
	 * 
	 */
	private int getSectionMemberCount(List<SectionModel> sections){
		int num = 0;
		if(sections == null) return 0;
		for ( SectionModel section : sections ){
			num += section.getTotalEnrollments();
		}
		return num;
	}
}
