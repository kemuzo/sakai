package org.sakaiproject.nssakura.learningStatus.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class GradeCourse {
	private String siteId;
	private List<GradeSectionModel> sections;
	private Date date=new Date();

	private final String dateFormat = "yyyy/MM/dd aaa hh:mm:ss";
	
	public GradeCourse (String siteId, List<GradeSectionModel> list)
	{
		this.siteId = siteId;
		this.sections = list;
	}
	
	public List<String> getTitleList(){
		try{
			return getSections().get(0).getAssignmentTitleList();
		}catch(Exception e){}
		return null;
	}
	
	public String getDateStr(){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			return sdf.format(date);
		}catch(Exception e){
		}
		return "";
	}
}
