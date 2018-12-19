package org.sakaiproject.nssakura.section.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.nssakura.section.logic.NssakuraSection;
import org.sakaiproject.nssakura.section.logic.ProjectLogic;
import org.sakaiproject.nssakura.section.model.SectionModel;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.web.servlet.ModelAndView;

@Data
public class NssakuraSectionBase {
	
	protected static final String ACTION_UPDATE_SECTIONS = "updateSections";
	protected static final String ACTION_ADD_SECTIONS = "addSections";
	protected static final String ACTION_ADD_AUTO_SECTIONS = "addAutoSections";
	protected static final String ACTION_UPDATE_TA = "updateTa";
	protected static final String ACTION_UPDATE_STUDENT = "updateStudents";
	protected static final String ACTION_REMOVE_SECTIONS = "removeSections";
	protected static final String ACTION_REMOVE_MEMBERS = "removeMembers";
	
	protected static final String REALM_SECTION_ADD = "sectionAddEnabled";
	protected static final String REALM_SECTION_REMOVE = "sectionRemoveEnabled";
	protected static final String REALM_TA_ASSIGNED = "taAssignedEnabled";
	protected static final String REALM_IMPORT_MEMBER = "memberImportEnabled";
	protected static final String REALM_ADMIN = "isAdmin";

	protected final String DISPLAY_NAME_IMPORTMEMBERS="importMembers";
	protected final String DISPLAY_NAME_IMPORTMEMBERS_PROCESSING="importMembersProcessing";
	protected final String DISPLAY_NAME_JOBS = "jobs";
	protected final String DISPLAY_NAME_ADDSECTIONS="addSections";
	protected final String DISPLAY_NAME_ASSIGNSTUDENT="assignStudent";
	protected final String DISPLAY_NAME_ASSIGNTA="assignTa";
	protected final String DISPLAY_NAME_INDEX="index";
	
	protected int LIMIT_NUM;

	public void init()
	{
		if (rb == null)
			rb = new ResourceLoader("org.sakaiproject.nssakura.section.bundle.messages");
		try{
			LIMIT_NUM = ServerConfigurationService.getInt("nssakura.section.import.max", 500);
		}catch(Exception e){
		}
	}

	protected Map<String, Object> getInitMap(){
		Map<String, Object> map = new HashMap<String,Object>();
		setEnabled(map);
		return map;
	}
	
	protected void setEnabled(Map map){
		if(projectLogic.getSakaiProxy().isSiteMemberInRoleInstructor() || projectLogic.getSakaiProxy().isSuperUser()){
			map.put(REALM_SECTION_ADD ,true);
			map.put(REALM_SECTION_REMOVE,true);
			map.put(REALM_TA_ASSIGNED, true);
			map.put(REALM_IMPORT_MEMBER, true);
		}
		if(projectLogic.getSakaiProxy().isSuperUser()){
			map.put(REALM_ADMIN, true);
		}
	}

	protected void setAdmin(Map map){
		if(projectLogic.getSakaiProxy().isSuperUser()){
			map.put(REALM_ADMIN, true);
		}
	}

	protected String goIndex(Map map){
		List<SectionModel> sections = projectLogic.getSectionModels();
		map.put("sections", sections);
		return DISPLAY_NAME_INDEX;
	}

	protected String goAddSection(Map map){
		List<String> sectionNames = projectLogic.getUnregisteredSectionNames();
		map.put("unregisteredSectionNames", sectionNames);
		
		return DISPLAY_NAME_ADDSECTIONS;
	}

	protected boolean checkButton(String submit){
		return (submit != null) && (submit.length() > 0);
	}
	
	protected ProjectLogic projectLogic = null;
	protected static ResourceLoader rb = null;
}
