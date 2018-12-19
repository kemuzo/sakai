package org.sakaiproject.nssakura.config.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.nssakura.config.logic.ProjectLogic;
import org.sakaiproject.nssakura.config.logic.SakaiProxy;
import org.sakaiproject.nssakura.config.model.NSConfig;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class NSConfigController {
	
	@Autowired
	private ProjectLogic projectLogic = null;
	
	@Autowired
	private SakaiProxy sakaiProxy = null;
	
	@RequestMapping(value="/index")
	public ModelAndView indexPage() throws Exception {
		Map<String, Object> map = new HashMap<String,Object>();
		
		// 管理者権限のないユーザはこのツールを使用できない．
		if (!sakaiProxy.isSuperUser()) {
			map.put("errorMessage", rb.getString("error_no_permission"));
			return new ModelAndView("error", map);
		}
		
		List<NSConfig> configurations = projectLogic.getAllConfigurations();
		map.put("configurations", configurations);
		return new ModelAndView("index", map);
	}
	
	@RequestMapping(value="/new")
	public ModelAndView createNew() throws Exception {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("config", new NSConfig());
		return new ModelAndView("new", map);
	}
	
	@RequestMapping(value="/edit")
	public ModelAndView viewEditPage(@RequestParam("name") String name) throws Exception {
		NSConfig config = projectLogic.getConfigByName(name);
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("config", config);
		return new ModelAndView("edit", map);
	}
	
	@RequestMapping(value="/registration")
	public ModelAndView registrationPage(
			@ModelAttribute("config") NSConfig config,
			@RequestParam("command") String command,
			Model model) throws Exception {
		config.setName(StringUtils.trimToNull(config.getName()));
		config.setValue(StringUtils.trimToNull(config.getValue()));
		config.setDescription(StringUtils.trimToNull(config.getDescription()));
		
		if ("entry".equals(command)) {
			if (!validate(config, model)) {
				return new ModelAndView("new");
			}
			try {
				projectLogic.saveConfig(config);
			} catch (DataIntegrityViolationException e) {
				model.addAttribute("errorMessage", rb.getString("error_duplicate_primary_key"));
				return new ModelAndView("new");
			}
		} else if ("modify".equals(command)) {
			if (!validate(config, model)) {
				return new ModelAndView("edit");
			}
			projectLogic.updateConfig(config);
		} else if ("delete".equals(command)) {
			projectLogic.deleteConfig(config);
		}
		return new ModelAndView("redirect:/index");
	}
	
	private static ResourceLoader rb = new ResourceLoader("org.sakaiproject.nssakura.config.messages");
	private static final int MAX_NAME_LENGTH = 99;
	private static final int MAX_VALUE_LENGTH = 255;
	private static final int MAX_DESCRIPTION_LENGTH = 255;
	
	private boolean validate(NSConfig config, Model model) {
		
		int nameLength = StringUtils.length(config.getName());
		if (nameLength == 0) {
			String message = rb.getString("valid_required_name");
			model.addAttribute("errorMessage", message);
			return false;
		}
		if (MAX_NAME_LENGTH < nameLength) {
			String message = rb.getFormattedMessage("valid_too_long_name",
					new Object[] { String.valueOf(MAX_NAME_LENGTH), String.valueOf(nameLength) });
			model.addAttribute("errorMessage", message);
			return false;
		}
		
		int valueLength = StringUtils.length(config.getValue());
		if (MAX_VALUE_LENGTH < valueLength) {
			String message = rb.getFormattedMessage("valid_too_long_value",
					new Object[] { String.valueOf(MAX_VALUE_LENGTH), String.valueOf(valueLength) });
			model.addAttribute("errorMessage", message);
			return false;
		}
		
		int descriptionLength = StringUtils.length(config.getDescription());
		if (MAX_DESCRIPTION_LENGTH < descriptionLength) {
			String message = rb.getFormattedMessage("valid_too_long_description",
					new Object[] { String.valueOf(MAX_DESCRIPTION_LENGTH), String.valueOf(descriptionLength) });
			model.addAttribute("errorMessage", message);
			return false;
		}
		
		return true;
	}

}
