package org.sakaiproject.nssakura.learningStatus.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.sakaiproject.nssakura.learningStatus.logic.LearningStatusSakaiProxy;
import org.sakaiproject.nssakura.learningStatus.model.MailForm;
import org.sakaiproject.nssakura.learningStatus.model.MessageModel;
import org.sakaiproject.nssakura.section.model.SectionModel;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class NSMailController {
	@Autowired
	private LearningStatusSakaiProxy lsSakaiProxy = null;

	protected static ResourceLoader rb = new ResourceLoader("org.sakaiproject.nssakura.learningStatus.bundle.messages");

	@ModelAttribute("mailForm")
	public MailForm initForm(){
		MailForm mailForm = new MailForm();
		return mailForm;
	}

	@RequestMapping(value="/messageIndex")
	public ModelAndView messageIndex(){
		Map<String, Object> map = getMap();
		return goIndex(map);
	}
	
	@RequestMapping(value="/messageCreate")
	public ModelAndView messageCreate(MailForm mailForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> map = getMap();
		return goCreate(map);
	}

	@RequestMapping(value="/messageResend")
	public ModelAndView messageResend(@RequestParam("messageId") String messageIdStr) throws Exception {
		Map<String, Object> map = getMap();
		try{
			Long messageId = Long.valueOf(messageIdStr);
			MailForm mailForm = lsSakaiProxy.getMailFormFromDB(messageId);
			if( mailForm != null){
				//model.addAttribute("mailForm", mailForm);
				map.put("mailForm", mailForm);
				map.put("msg", rb.getString("msgs_mail_resend"));
			}
		}catch(Exception e){}
		return goCreate(map);
	}

	@RequestMapping(value="/sendMessage", method=RequestMethod.POST)
	public ModelAndView sendMessage(@Valid @ModelAttribute MailForm mailForm){
		Map<String, Object> map = getMap();
		if(! validateMessage(mailForm, map)){
			return goCreate(map);
		}
		int n = lsSakaiProxy.notify(mailForm);
		if(n < 0){
			map.put("err", rb.get("err_no_sender"));
			return goCreate(map);
		}
		if(n == 0){
			map.put("err",rb.get("err_no_validate_addresses"));
			return goCreate(map);
		}
		map.put("msg", rb.get("msgs_send_done"));
		return goIndex(map);
	}

	@RequestMapping(value="/messageRemove")
	public ModelAndView messageRemove(@RequestParam("messageId") String messageIdStr) throws Exception {
		Map<String, Object> map = getMap();
		boolean flg = false;
		try{
			Long messageId = Long.valueOf(messageIdStr);
			flg = lsSakaiProxy.removeMessage(messageId);
		}catch(Exception e){}
		if(flg){
			map.put("msg", rb.getString("msgs_remove_success"));
		}else{
			map.put("err", rb.getString("msgs_remove_failuer"));
		}
		return goIndex(map);
	}


	private ModelAndView goIndex(Map<String,Object> map){
		List<MessageModel> messages = lsSakaiProxy.getMessageModels();
		map.put("messages", messages);
		Boolean superUser = lsSakaiProxy.isSuperUser();
		return new ModelAndView("message/index", map);
	}

	private ModelAndView goCreate(Map<String,Object> map){
		String senderEmail = lsSakaiProxy.getSenderEmail();
		map.put("sender", senderEmail);
		List<String> roles = lsSakaiProxy.getRoles();
		map.put("roles", roles);
		List<SectionModel> sections = lsSakaiProxy.getSectionModels();
		map.put("sections", sections);
		return new ModelAndView("message/create",map);
	}

	private boolean validateMessage(MailForm mailForm, Map map){
		if(! mailForm.isAddressValidate()){
			map.put("err", rb.get("err_no_addresses"));
			return false;
		}
		if(! mailForm.isSubjectValidate()){
			map.put("err", rb.get("err_no_subject"));
			return false;
		}
		if(! mailForm.isContentValidate()){
			map.put("err", rb.get("err_no_content"));
			return false;
		}
		return true;
	}
	
	private Map<String, Object> getMap(){
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("superuser", lsSakaiProxy.isSuperUser());
		map.put("instructor", lsSakaiProxy.isInstructor());
		return map;
	}
}
