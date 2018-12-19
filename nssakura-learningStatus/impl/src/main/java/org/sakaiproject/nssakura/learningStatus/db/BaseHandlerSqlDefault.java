package org.sakaiproject.nssakura.learningStatus.db;

public class BaseHandlerSqlDefault implements BaseHandlerSql {

	@Override
	public String getPassedStudentsfromGradebookRecordSql() {
		return "SELECT STUDENT_ID from GB_GRADE_RECORD_T as t1 where t1.GRADABLE_OBJECT_ID=? AND POINTS_EARNED>?";
	}
	
	public String getGradableObjectSql(){
		return "SELECT t1.* from GB_GRADABLE_OBJECT_T as t1 LEFT JOIN GB_GRADEBOOK_T t2 on t1.GRADEBOOK_ID=t2.ID WHERE GRADEBOOK_UID=? ORDER BY t1.SORT_ORDER IS NULL, t1.SORT_ORDER, t1.NAME";
	}
	
	public String getGradebookRecordSql() {
		return "SELECT STUDENT_ID,DATE_RECORDED,POINTS_EARNED from GB_GRADE_RECORD_T as t1 where t1.GRADABLE_OBJECT_ID=?";
	}
}
