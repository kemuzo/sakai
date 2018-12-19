package org.sakaiproject.nssakura.learningStatus.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.sakaiproject.nssakura.section.model.SectionModel;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.user.api.User;

@Data
public class GradeUserModel {

	private User user;
	private List<GradeAssignmentItem> assignmentItems;
	
	public GradeUserModel(User user){
		this.user = user;
	}
	public GradeUserModel(User user, List<Assignment> assignments){
		this.user = user;
		this.assignmentItems = new ArrayList<GradeAssignmentItem>();
		for(Assignment assignment : assignments){
			GradeAssignmentItem assignmentItem = new GradeAssignmentItem();
			assignmentItem.setItemId(assignment.getId());
			assignmentItem.setItemName(assignment.getName());
			assignmentItems.add(assignmentItem);
		}
	}
	
	/**
	 * Assignmentタイトルリストを返す
	 * @return
	 */
	public List<String> getAssignmentTitleList(){
		List<String> list = new ArrayList<String>();
		for(GradeAssignmentItem item: assignmentItems){
			list.add(item.getItemName());
		}
		return list;
	}
	
	public String getUserName(){
		return user.getDisplayName();
	}
	
	public String getUserId(){
		return user.getDisplayId();
	}
}
