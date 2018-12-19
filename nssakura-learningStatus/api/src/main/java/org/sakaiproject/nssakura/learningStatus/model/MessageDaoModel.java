package org.sakaiproject.nssakura.learningStatus.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import lombok.Data;

@Data
public class MessageDaoModel {
	private final String DELIMITER=",";
	private final String dateFormat = "yyyy/MM/dd aaa hh:mm:ss";
	
	private long id;
	private String siteId;
	private String userId;
	private Date sendDate=new Date();
	private String recipientTo;
	private String recipientRole;
	private String recipientSectionGroup;
	private String subject;
	private String content;
	
	public void setRecipientRoleFromArrays(String[] roles){
		if(roles == null || roles.length<1){
			return;
		}
		this.recipientRole=StringUtils.join(roles,DELIMITER);
	}
	
	public void setRecipientSectionGroupFromArrays(String[] sectionGroups){
		if(sectionGroups == null || sectionGroups.length<1){
			return;
		}
		this.recipientSectionGroup=StringUtils.join(sectionGroups,DELIMITER);
	}
	
	public String getSendDateStr(){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			return sdf.format(sendDate);
		}catch(Exception e){
		}
		return "";		
	}
}
