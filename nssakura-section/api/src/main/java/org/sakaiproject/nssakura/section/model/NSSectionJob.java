package org.sakaiproject.nssakura.section.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NSSectionJob {

	private Long id;
	private String userId;
	private String siteId;
	private Date startDate;
	private Date endDate;
	private int addUserNum;
	private int alterUserNum;
	private int addMemberNum;
	private int errorUserNum;
	private int processUserNum;

	private final String dateFormat = "yyyy/MM/dd aaa hh:mm:ss";
	
	public NSSectionJob(String siteId, String userId, int size){
		this.siteId = siteId;
		this.userId = userId;
		this.startDate = new Date();
		this.processUserNum = size;
	}
	
	public String getStartDateStr(){
		return getDateStr(startDate);
	}
	
	public String getEndDateStr(){
		return getDateStr(endDate);
	}
	
	private String getDateStr(Date date){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			return sdf.format(date);
		}catch(Exception e){
		}
		return "";
	}
}
