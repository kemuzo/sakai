package org.sakaiproject.nssakura.learningStatus.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GradeModel {
	private String userId;
	private Double point;
	private Date date;
	
}
