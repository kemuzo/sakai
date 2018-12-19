package org.sakaiproject.nssakura.section.tool;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.Setter;

import org.sakaiproject.nssakura.section.Constant;
import org.sakaiproject.nssakura.section.model.NSSectionJob;
import org.sakaiproject.nssakura.section.model.UserCsvResultModel;
import org.sakaiproject.nssakura.section.tool.model.MemberImportForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("userCsvResultModel")
public class NssakuraImportMembersController extends NssakuraSectionBase {
	
	@ModelAttribute
	public MemberImportForm setMemberImportForm(){
		boolean flag = projectLogic.getSakaiProxy().getServerBooleanValue(Constant.SAKAI_PROPERTY_USER_OVERRIDE, false);
		return new MemberImportForm(flag);
	}
	
	@ModelAttribute
	public UserCsvResultModel setUserCsvResultModel(){
		return new UserCsvResultModel();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView importMembers(@ModelAttribute UserCsvResultModel userCsvResultModel, HttpServletRequest req, WebRequest request, SessionStatus status) throws ServletRequestBindingException{
		Map<String, Object> map = getInitMap();
		String err = ServletRequestUtils.getStringParameter(req, "err");
		if("filesizeexceeded".equals(err)){
			String maxsize = ServletRequestUtils.getStringParameter(req, "maxfilesize");
			map.put("err", rb.getFormattedMessage("msgs_error_filesizeexceeded", maxsize));
		}
		request.removeAttribute("userCsvResultModel", WebRequest.SCOPE_SESSION);
		userCsvResultModel = setUserCsvResultModel();
		map.put("userCsvResultModel", userCsvResultModel);
		String menu = ServletRequestUtils.getStringParameter(req, "menu");
		if( "jobStatus".equals(menu)){
			List<NSSectionJob> list = addMemberService.getJobs();
			map.put("jobs", list);
			return new ModelAndView(DISPLAY_NAME_JOBS, map);
		}
		return importMembersDo(map);
	}

	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView fileImport(
			@ModelAttribute("memberImportForm") MemberImportForm memberImportForm,
			@ModelAttribute("userCsvResultModel") UserCsvResultModel userCsvResultModel,
			HttpServletRequest req, WebRequest request, SessionStatus status) throws ServletRequestBindingException{
		Map<String, Object> map = getInitMap();

		String returnSubmit = ServletRequestUtils.getStringParameter(req, "cancelButton");	
		// cancel
		if(returnSubmit != null && returnSubmit.length() > 0){
			status.setComplete();
			request.removeAttribute("userCsvResultModel", WebRequest.SCOPE_SESSION);
			userCsvResultModel = setUserCsvResultModel();
			map.put("userCsvResultModel", userCsvResultModel);
			return new ModelAndView(goIndex(map), map);
		}

		//for action
		String action = ServletRequestUtils.getStringParameter(req, "action");

		if( "exec".equals(action)){
			//check checkCode
			String checkCode = ServletRequestUtils.getStringParameter(req, "checkCode");
			if(! userCsvResultModel.isValidate(checkCode)){
				map.put("err", rb.get("msgs_error_invalid_display_change"));
				status.setComplete();
				request.removeAttribute("userCsvResultModel", WebRequest.SCOPE_SESSION);
				userCsvResultModel = setUserCsvResultModel();
				map.put("userCsvResultModel", userCsvResultModel);
				return importMembersDo(map);
			}
			boolean override = ServletRequestUtils.getBooleanParameter(req, "override", false);
			int len = userCsvResultModel.getUserCsvList().size();
			if( len > LIMIT_NUM ){
				//非同期実行
				addMemberService.registerImportUsersJob(userCsvResultModel, override, projectLogic.getSakaiProxy().getCurrentSiteId(), projectLogic.getSakaiProxy().getCurrentUserId());
				map.put("msg", rb.getString("msgs_exec_async"));
				return new ModelAndView(goIndex(map), map);
			}
			addMemberService.registerImportUsers(userCsvResultModel, override, projectLogic.getSakaiProxy().getCurrentSiteId(), projectLogic.getSakaiProxy().getCurrentUserId());
			map.put("msg", rb.getFormattedMessage("msgs_result_import_file", new Object[]{userCsvResultModel.getAddUser().size(),userCsvResultModel.getAlterUser().size(),userCsvResultModel.getAddMember().size()}));
			String errorUser = userCsvResultModel.getErrorUsersStr();
			if( errorUser != null){
				map.put("err", rb.getFormattedMessage("error_result_import_file", new Object[]{errorUser}));
			}
			return new ModelAndView(goAddSection(map), map);
		}else if ("selectCSVFile".equals(action)){
			MultipartFile file = memberImportForm.getFile();
			String charCode = memberImportForm.getCharCode();
			if ( file != null && file.getSize() > 0 ){
				try {
					byte[] fileData = file.getBytes();
					userCsvResultModel = projectLogic.getImportUsers(fileData, charCode);
					if( userCsvResultModel == null){
						map.put("err", rb.get("msgs_error_fileempty"));
					}else if (userCsvResultModel.isError()){
						map.put("err", rb.get(userCsvResultModel.getErrorMsgKey()));
					}else{
						int len = userCsvResultModel.getUserCsvList().size();
						if( len > LIMIT_NUM ){
							map.put("err", rb.get("msgs_warning_limitover"));
						}
						map.put("userCsvResultModel", userCsvResultModel);
						map.put("checkCode", userCsvResultModel.getCheckCode());
						map.put("override", memberImportForm.isOverride());
					}
				} catch (IOException e) {
					map.put("err", rb.get("msgs_error_fileempty"));
				}
			}else{
				map.put("err", rb.get("msgs_error_fileempty"));
			}
		}
		return importMembersDo(map);
	}
	

	/**
	 * 受講生登録初期画面表示
	 * @param map
	 * @return
	 */
	private ModelAndView importMembersDo(Map<String, Object> map){
		setAdmin(map);
		String processingDate = addMemberService.getProcessing();
		if( processingDate != null){
			map.put("processing", true);
			map.put("err", rb.getFormattedMessage("msgs_warning_processing_date", processingDate));
			return new ModelAndView( DISPLAY_NAME_IMPORTMEMBERS_PROCESSING , map);
		}
		int jobsNum = addMemberService.getJobsNum();
		map.put("jobsNum", jobsNum);
		return new ModelAndView( DISPLAY_NAME_IMPORTMEMBERS, map);
	}
	
	@Setter
	private AddMemberService addMemberService;
}
