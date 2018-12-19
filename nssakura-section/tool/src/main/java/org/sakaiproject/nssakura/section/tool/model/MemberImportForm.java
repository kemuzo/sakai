package org.sakaiproject.nssakura.section.tool.model;

import java.io.Serializable;

import lombok.Data;

import org.springframework.web.multipart.MultipartFile;

@Data
public class MemberImportForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MultipartFile file;
	private String charCode="utf8";
	private boolean override=false;
	
	public MemberImportForm(boolean override){
		this.override = override;
	}
	
	public MemberImportForm(){
	}
}
