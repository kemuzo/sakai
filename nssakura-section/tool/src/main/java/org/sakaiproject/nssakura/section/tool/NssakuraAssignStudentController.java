package org.sakaiproject.nssakura.section.tool;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.sakaiproject.nssakura.section.exception.DuplicateGroupMemberException;
import org.sakaiproject.nssakura.section.exception.NoRegisteredUserException;
import org.sakaiproject.nssakura.section.exception.SiteSaveException;
import org.sakaiproject.nssakura.section.exception.UnmachedRoleException;
import org.sakaiproject.nssakura.section.model.UserModel;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.exception.RoleConfigurationException;
import org.sakaiproject.section.api.facade.Role;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class NssakuraAssignStudentController extends NssakuraSectionBase implements Controller {

	/**
	 * Hello World Controller
	 * 
	 * @author Mike Jennings (mike_jennings@unc.edu)
	 * 
	 */
	
	public ModelAndView handleRequest(HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		final Logger log = Logger.getLogger(NssakuraAssignStudentController.class);

		Map<String, Object> map = getInitMap();

		String addSubmit = ServletRequestUtils.getStringParameter(req, "addButton");	
		String addAutoSubmit = ServletRequestUtils.getStringParameter(req, "addAutoButton");	
		String removeButton = ServletRequestUtils.getStringParameter(req, "removeButton");	
		String returnButton = ServletRequestUtils.getStringParameter(req, "returnButton");
		String submitButton = ServletRequestUtils.getStringParameter(req, "submitButton");
		String cancelButton = ServletRequestUtils.getStringParameter(req, "cancelButton");

		//for action
		String action = ServletRequestUtils.getStringParameter(req, "action");
		String sectionId = ServletRequestUtils.getStringParameter(req, "sectionId");
		CourseSection section = projectLogic.getSakaiProxy().getSection(sectionId);

		map.put("section", section);

		if(checkButton(returnButton)){
			return new ModelAndView(goIndex(map), map);
		}
		if (ACTION_UPDATE_STUDENT .equals(action)){
			// add one student
			if(checkButton(addSubmit)){
				String userEid = ServletRequestUtils.getStringParameter(req,"userEid");
				if(userEid == null || userEid.length() < 1){
					map.put("err",rb.getString("msgs_no_user_id_enrollment"));
				}else{
					String[] param = new String[1];
					param[0] = userEid;
					try{
						projectLogic.addSectionEnrollmentUser(userEid, sectionId);
						//success
						map.put("msg",rb.getFormattedMessage("msgs_success_update_enrollment",param));
					}catch(UnmachedRoleException e){
						//Student roleでないユーザを登録しようとしたエラー
						map.put("err",rb.getFormattedMessage("msgs_different_role_error_enrollment", param));
					}catch(SiteSaveException e){
						e.printStackTrace();
						map.put("err",rb.getFormattedMessage("msgs_unsuccess_update_enrollment", param));
					}catch(RoleConfigurationException e){
						e.printStackTrace();
						map.put("err",rb.getString("msgs_cannot_register_enrollment"));
					}catch(DuplicateGroupMemberException e){
						map.put("err",rb.getFormattedMessage("msgs_already_member_enrollment", param));
					}catch(NoRegisteredUserException e){
						map.put("err",rb.getFormattedMessage("msgs_no_user_in_sakai", param));
					}
				}
			}else if(checkButton(addAutoSubmit)){
				//自動割当
				List<String> userIds = projectLogic.getSakaiProxy().getUserIdsBySectionName(section.getTitle());
				if(userIds == null || userIds.size()<1){
					map.put("err",rb.getString("msgs_no_beloged_users"));
				}else{
					List<String> registerdUserIds = projectLogic.getSakaiProxy().addSectionMembersAndSiteMembers(userIds, Role.STUDENT, sectionId);
					String[] param = new String[1];
					param[0] = Integer.toString(registerdUserIds.size());
					map.put("msg",rb.getFormattedMessage("msgs_add_members", param));
				}
			}else if(checkButton(removeButton)){
				//割当削除
				String userIds[] = ServletRequestUtils.getStringParameters(req, "enrollmentremove");
				if(userIds == null || userIds.length < 1){
					map.put("err", rb.getString("msgs_removing_user_not_selected"));
				}else{
					List<UserModel> userList = projectLogic.getUserModelListByUserId(userIds);
					map.put("users", userList);
					return new ModelAndView("deleteMembers", map);
				}
			}
		}else if(ACTION_REMOVE_MEMBERS.equals(action)){
			if(checkButton(submitButton)){
				//削除
				String userIds[] = ServletRequestUtils.getStringParameters(req, "memberremove");
				log.debug("Remove members:" + userIds);
				projectLogic.removeSecitonEnrollmentUser(userIds, sectionId);
				map.put("msg", rb.getString("msgs_member_removed"));
			}
		}
		//init
		
		List<UserModel> enrollments = projectLogic.getSectionEnrollmentUsers(sectionId);
		int n = 0;
		if( enrollments != null && enrollments.size()>0 ){
			n = enrollments.size();
		}
		String[] param = new String[1];
		param[0] = String.valueOf(n);
		map.put("enrollments", enrollments);
		map.put("enrollments_num", rb.getFormattedMessage("msgs_enrollment_num", param));
		return new ModelAndView(DISPLAY_NAME_ASSIGNSTUDENT, map);
	}

}
