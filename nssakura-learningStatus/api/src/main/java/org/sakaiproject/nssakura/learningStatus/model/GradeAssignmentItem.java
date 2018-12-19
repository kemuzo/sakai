package org.sakaiproject.nssakura.learningStatus.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GradeAssignmentItem {
	private Long itemId;
	private String itemName;
	private Double points;
	private Date date;

	public GradeAssignmentItem() {
	}
}
