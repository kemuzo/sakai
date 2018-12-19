package org.sakaiproject.nssakura.section.tool;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class ExceptionResolverImpl  implements HandlerExceptionResolver{

	@Override
	public ModelAndView resolveException(HttpServletRequest httpservletrequest,
			HttpServletResponse httpservletresponse, Object obj,
			Exception exception) {
		Map<String, Object> map = new HashMap<String,Object>();
		if(exception instanceof MaxUploadSizeExceededException){
			long maxsize = ((MaxUploadSizeExceededException)exception).getMaxUploadSize();
			return new ModelAndView("redirect:importMembers.htm?menu=init&err=filesizeexceeded&maxfilesize=" + maxsize);
		}
		System.out.println("ERROR:org.sakaiproject.nssakura.section.tool\n" + exception.getMessage());
		map.put("errmsg", exception.getMessage());
		return new ModelAndView("error", map);
	}

}
