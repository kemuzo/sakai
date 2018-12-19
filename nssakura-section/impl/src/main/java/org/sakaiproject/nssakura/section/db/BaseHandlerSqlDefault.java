package org.sakaiproject.nssakura.section.db;

public class BaseHandlerSqlDefault implements BaseHandlerSql {

	@Override
	public String getSectionNamesSql() {
		return "SELECT DISTINCT(t1.VALUE) from (SELECT USER_ID,VALUE FROM SAKAI_USER_PROPERTY WHERE NAME=?) as t1"
		+ "left join SAKAI_SITE_USER t2 on t1.USER_ID=t2.USER_ID where t2.PERMISSION=?";
	}

	public String getSectionNamesOfMembersSql() {
		return "SELECT DISTINCT(t2.VALUE) from (SELECT USER_ID FROM SAKAI_SITE_USER where SITE_ID=? and PERMISSION=?) as t1 left join SAKAI_USER_PROPERTY t2 on t1.USER_ID=t2.USER_ID where t2.NAME=? group by t2.VALUE";
	}

	public String getUserIdsBySectionNameSql(){
		return "SELECT USER_ID FROM SAKAI_USER_PROPERTY WHERE NAME=? AND VALUE=?";
	}
	public String getUserIdsBySectionNameAllSql(){
		return "SELECT * FROM (SELECT USER_ID FROM SAKAI_USER_PROPERTY WHERE NAME=? AND ( VALUE=? OR VALUE like ?)) as t1 "
		+ "left join SAKAI_SITE_USER t2 on t1.USER_ID=t2.USER_ID where t2.PERMISSION= ?";
	}
	
	public String getUserIdsBySectionNameAndSiteIdSql(){
		return "SELECT * FROM (SELECT USER_ID FROM SAKAI_USER_PROPERTY WHERE NAME=? AND ( VALUE=? OR VALUE like ?)) as t1 "
		+ "left join SAKAI_SITE_USER t2 on t1.USER_ID=t2.USER_ID where t2.PERMISSION= ? and t2.site_id=?";
	}
	
	public String getUserIdsByType(){
		return "SELECT USER_ID FROM SAKAI_USER WHERE TYPE=?";
	}
	
	public String getEnrollmentCounts(){
		return "SELECT REALM_ID,COUNT(SRRG.USER_ID) FROM SAKAI_REALM SR " 
			+ "INNER JOIN SAKAI_REALM_RL_GR SRRG ON SR.REALM_KEY = SRRG.REALM_KEY "
			+ "INNER JOIN SAKAI_REALM_RL_FN SRRF ON SRRF.ROLE_KEY = SRRG.ROLE_KEY AND SRRF.REALM_KEY = SR.REALM_KEY "
			+ "INNER JOIN SAKAI_REALM_FUNCTION SRF ON SRRF.FUNCTION_KEY = SRF.FUNCTION_KEY "
			+ "INNER JOIN SAKAI_USER_ID_MAP SUIM ON SRRG.USER_ID=SUIM.USER_ID ";
	}
	public String getEnrollmentCountsWhere(){
			return  "WHERE FUNCTION_NAME = ? and SRRG.ACTIVE = ? and  REALM_ID IN ( ";
	}
	public String getEnrollmentCountsGroupby(){
			return "GROUP BY REALM_ID";
	}
}
