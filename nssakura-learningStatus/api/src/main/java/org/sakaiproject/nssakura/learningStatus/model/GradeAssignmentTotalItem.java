package org.sakaiproject.nssakura.learningStatus.model;

import java.math.BigDecimal;

import lombok.Data;

import org.sakaiproject.service.gradebook.shared.Assignment;

@Data
public class GradeAssignmentTotalItem {
	private String itemName;
	private int allNum;
	private int executeNum;

	public GradeAssignmentTotalItem() {
	}

	public GradeAssignmentTotalItem(Assignment assignment) {
		itemName = assignment.getName();
	}
	
	public void addExecuteNum(){
		executeNum++;
	}
	
	public String getExecuteNumDecimalStr(){
		if(allNum == 0){
			return "0";
		}
		double percent = (double)executeNum*100/(double)allNum;
		BigDecimal bd = new BigDecimal(percent);
		bd = bd.setScale(1,BigDecimal.ROUND_HALF_UP);
		return executeNum + "(" + bd.doubleValue() + "%)";
	}
	
	public String getExecuteNumStr(){
		if(allNum == 0){
			return "0";
		}
		int percent = executeNum*100/allNum;
		return executeNum + "(" + percent + "%)";
	}
}
