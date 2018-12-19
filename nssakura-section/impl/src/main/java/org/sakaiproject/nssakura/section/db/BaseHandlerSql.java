package org.sakaiproject.nssakura.section.db;

public interface BaseHandlerSql {

	String getSectionNamesSql();
	String getSectionNamesOfMembersSql();
	String getUserIdsBySectionNameSql();
	String getUserIdsBySectionNameAllSql();
	String getUserIdsBySectionNameAndSiteIdSql();
	String getUserIdsByType();
	String getEnrollmentCounts();
	String getEnrollmentCountsWhere();
	String getEnrollmentCountsGroupby();
}
