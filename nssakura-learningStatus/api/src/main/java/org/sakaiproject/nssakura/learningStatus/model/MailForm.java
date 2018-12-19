package org.sakaiproject.nssakura.learningStatus.model;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class MailForm {
	private String to;
	@NotNull
	private String subject;
	@NotNull
	private String content;
	private String[] roles;
	private String[] sections;
	
	public boolean isAddressValidate(){
		int rolesLength = roles==null ? 0 : roles.length;
		int sectionsLength = sections==null ? 0 : sections.length;
		if(rolesLength<1 && sectionsLength<1 && to.isEmpty()){
			return false;
		}
		return true;
	}
	
	public boolean isSubjectValidate(){
		if(subject.isEmpty()){
			return false;
		}
		return true;
	}
	
	public boolean isContentValidate(){
		if(content.isEmpty()){
			return false;
		}
		return true;
	}
}
