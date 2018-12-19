package org.sakaiproject.nssakura.section.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.log4j.Logger;
import org.sakaiproject.section.api.coursemanagement.CourseSection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionModel {
    private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(SectionModel.class);

    private CourseSection section;
    private List<String> instructorNames;
    private int totalEnrollments;
    private boolean flaggedForRemoval;
    private String data_tt_parent_id="";
    private String data_tt_id="";
    private String delimiter="";
    
    public String getTitle(){
    	try{
    		String[] sections = section.getTitle().split(delimiter);
        	if(sections == null || sections.length<1){
        		return "";
        	}
        	if(data_tt_parent_id == null || data_tt_parent_id.length()<1){
        		return section.getTitle();
        	}
        	return sections[sections.length-1];
    	}catch(Exception e){
    	}
    	return "";
    }
}
