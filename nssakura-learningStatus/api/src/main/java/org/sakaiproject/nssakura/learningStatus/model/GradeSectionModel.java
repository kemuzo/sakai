package org.sakaiproject.nssakura.learningStatus.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.sakaiproject.nssakura.section.model.SectionModel;
import org.sakaiproject.service.gradebook.shared.Assignment;

@Data
public class GradeSectionModel {

	private SectionModel sectionModel;
	private List<GradeAssignmentTotalItem> assignmentItems;
	private String originalTitle="";
	private int allNum;
	
	public GradeSectionModel(SectionModel sectionModel){
		this.allNum = sectionModel.getTotalEnrollments();
		this.sectionModel = sectionModel;
	}
	public GradeSectionModel(List<Assignment> assignments, int allNum){
		this.allNum = allNum;
		this.assignmentItems = new ArrayList<GradeAssignmentTotalItem>();
		for(Assignment assignment : assignments){
			GradeAssignmentTotalItem assignmentItem = new GradeAssignmentTotalItem(assignment);
			assignmentItem.setAllNum(allNum);
			this.assignmentItems.add(assignmentItem);
		}
	}
	public GradeSectionModel(SectionModel sectionModel, List<Assignment> assignments){
		this.allNum = sectionModel.getTotalEnrollments();
		this.sectionModel = sectionModel;
		this.assignmentItems = new ArrayList<GradeAssignmentTotalItem>();
		for(Assignment assignment : assignments){
			GradeAssignmentTotalItem assignmentItem = new GradeAssignmentTotalItem(assignment);
			assignmentItem.setAllNum(sectionModel.getTotalEnrollments());
			this.assignmentItems.add(assignmentItem);
		}
	}
	
	/**
	 * 実施数をプラスする
	 * @param assignment
	 * @param executed
	 */
	public void addExecuteNum(Assignment assignment){
		if(assignment == null ) return ;
		String title = assignment.getName();
		for(GradeAssignmentTotalItem ga : assignmentItems){
			if(ga.getItemName().equals(title)){
				ga.addExecuteNum();
				return;
			}
		}
		GradeAssignmentTotalItem ga= new GradeAssignmentTotalItem(assignment);
		ga.addExecuteNum();
		this.assignmentItems.add(ga);
	}
	
	/**
	 * Assignmentタイトルリストを返す
	 * @return
	 */
	public List<String> getAssignmentTitleList(){
		List<String> list = new ArrayList<String>();
		for(GradeAssignmentTotalItem item: assignmentItems){
			list.add(item.getItemName());
		}
		return list;
	}
	
	public String getTableId(){
		String id = "";
		try{
			id = sectionModel.getData_tt_id();
		}catch(Exception e){}
		return id;
	}
	
	public String getParentTableId(){
		String id = "";
		try{
			id = sectionModel.getData_tt_parent_id();
		}catch(Exception e){}
		return id;
	}
	
	public String getTitle(){
		if(sectionModel==null || (originalTitle!=null&& originalTitle.length()>0)){
			return originalTitle;
		}
		return sectionModel.getTitle();
	}
	
	public String getSectionId(){
		try{
			return sectionModel.getSection().getUuid();
		}catch(Exception e){}
		return "";
	}
}
