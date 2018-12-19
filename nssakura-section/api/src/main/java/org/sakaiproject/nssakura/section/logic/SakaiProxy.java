package org.sakaiproject.nssakura.section.logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.authz.api.Member;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.nssakura.section.exception.DuplicateGroupMemberException;
import org.sakaiproject.nssakura.section.exception.SiteSaveException;
import org.sakaiproject.nssakura.section.exception.UnmachedRoleException;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.coursemanagement.ParticipationRecord;
import org.sakaiproject.section.api.exception.RoleConfigurationException;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserEdit;

/**
 * An interface to abstract all Sakai related API calls in a central method that can be injected into our app.
 * 
 * @author Mike Jennings (mike_jennings@unc.edu)
 *
 */
public interface SakaiProxy {

	/**
	 * Get current siteid
	 * @return
	 */
	public String getCurrentSiteId();
	
	/**
	 * Get current user id
	 * @return
	 */
	public String getCurrentUserId();
	
	/**
	 * Get current user display name
	 * @return
	 */
	public String getCurrentUserDisplayName();
	
	/**
	 * Is the current user a superUser? (anyone in admin realm)
	 * @return
	 */
	public boolean isSuperUser();
	
	/**
	 * Post an event to Sakai
	 * 
	 * @param event			name of event
	 * @param reference		reference
	 * @param modify		true if something changed, false if just access
	 * 
	 */
	public void postEvent(String event,String reference,boolean modify);
	
	/**
	 * Wrapper for ServerConfigurationService.getString("skin.repo")
	 * @return
	 */
	public String getSkinRepoProperty();
	
	/**
	 * Gets the tool skin CSS first by checking the tool, otherwise by using the default property.
	 * @param	the location of the skin repo
	 * @return
	 */
	public String getToolSkinCSS(String skinRepo);
	
	/**
	 * Get section groups in the current site. 
	 * @return section list
	 */
	public List<CourseSection> getSections();

	/**
	 * Get section groups map in the current site
	 * @return section map
	 */
	public Map<String, CourseSection> getSectionsMap();
	
	/**
	 * Get Section From SectionId
	 * @param sectionId
	 * @return
	 */
	public CourseSection getSection(String sectionId);
	
	/**
	 * Get section Tas map
	 * @param sections
	 * @return
	 */
	public Map<String,List<ParticipationRecord>> getSectionTas(List<CourseSection> sections);

	/**
	 * Get Tas from SectionId
	 * @param sectionId
	 * @return
	 */
	public List<ParticipationRecord> getSectionTas(String sectionId);

	/**
	 * Get Tas from SiteId
	 * @param siteId
	 * @return
	 */
	public List<ParticipationRecord> getSiteTeachingAssistants(String siteId);
	
	/**
	 * set(overwrite) Member to Section
	 * @param selectedUsers
	 * @param role
	 * @param sectionId
	 * @return result flg
	 */
	public boolean setSectionMembersAndSiteMembers(String[] selectedUsers, Role role, String sectionId);

	/**
	 * set(overwrite) Member to Section
	 * @param selectedUsers
	 * @param role
	 * @param sectionId
	 * @return result flg
	 */
	public boolean setSectionMembers(String[] selectedUsers, Role role, String sectionId);
	/**
	 * add Members to Section
	 * @param userIdList
	 * @param role
	 * @param sectionId
	 * @return
	 */
	public List<String> addSectionMembersAndSiteMembers(List<String> userIdList, Role role, String sectionId);
	
	/**
	 * remove members from section
	 * @param userIds
	 * @param sectionId
	 */
	public void removeSectionMembers(String[] userIds, String sectionId);
	
	/**
	 * set Member to Section
	 * @param userId
	 * @param role
	 * @param sectionId
	 * @throws UnmachedRoleException
	 * @throws SiteSaveException
	 * @throws RoleConfigurationException
	 * @throws DuplicateGroupMemberException
	 */
	public void addSectionMember(String userId, Role role, String sectionId)
	throws UnmachedRoleException,SiteSaveException, RoleConfigurationException, DuplicateGroupMemberException;

	/**
	 * set Member to Sections
	 * @param selectedUsers
	 * @param role
	 * @param sectionIds list
	 * @throws UnmachedRoleException
	 * @throws SiteSaveException
	 * @throws RoleConfigurationException
	 * @throws DuplicateGroupMemberException 
	 */
	public void addSectionMember(String userId, Role role, List<CourseSection> sections) throws IdUnusedException, PermissionException, UnmachedRoleException, RoleConfigurationException;	
	/**
	 * Get number of enrollments
	 * @param sections
	 * @return
	 */
	public Map getEnrollmentCounts(List<CourseSection> sections);

	/**
	 * Get enrollments of section.
	 * @param sectionId
	 * @return
	 */
	public List<EnrollmentRecord> getSectionEnrollment(String sectionId);
	
	/**
	 * role in Site TA or not ?
	 * @return
	 */
	public boolean isSiteMemberInRoleTA();
	
	/**
	 * role in Site Instructor or not ?
	 * @return
	 */
	public boolean isSiteMemberInRoleInstructor();

	/**
	 * disband Sections
	 * @param sectionIds
	 */
	public void disbandSections(Set<String> sectionIds) ;
	
	/**
	 * Get section names from user's property. 
	 * @return section list
	 */
	public List<String> getSectionNamesFromUsers();

	/**
	 * Get section names from site students's property
	 * @return
	 */
	public List<String> getSectionNamesFromStudentMembers();
	public List<String> getSectionNamesFromStudentMembers(HashSet<String> tas);	
	/**
	 * Get userids from user's property.
	 * @return
	 */
	public List<String> getUserIdsBySectionName(String sectionName);

	/**
	 * Get userids from user's property. use like
	 * @return
	 */
	public List<String> getUserIdsBySectionAllName(String sectionName, String sectionVague);
	
	/**
	 * Add sections to course site.
	 * @param sectionNames
	 * @return added sections
	 */
	public Collection<CourseSection> addSections(String[] sectionNames);

	/**
	 * 現在のサイトに登録されているメンバを返す
	 * @return
	 */
	public Set<Member> getSiteMember();
	
	/**
	 * get User by userId
	 * @param userId
	 * @return
	 */
	public User getUser(String userId);
	
	/**
	 * get User by userEid
	 * @param userEid
	 * @return
	 */
	public User getUserByEid(String userEid);
	
	/**
	 * get Users by type
	 * @param type
	 * @return
	 */
	public List<User> getUserListByType(String type);
	
	/**
	 * Site member ? or not ?
	 * @param userId
	 * @return
	 */
	public boolean isSiteMember(String userId);
	
	/**
	 * 
	 * @param eid
	 * @param lastName
	 * @param firstName
	 * @param email
	 * @param password
	 * @param type
	 * @param userProperties
	 * @return
	 */
	public UserEdit createOrUpdateUserAfterAuthentication(String eid,
			String lastName, String firstName, String email, String password,
			String type, ResourceProperties userProperties);
	
	/**
	 * カレントサイトにメンバとして追加する
	 * @param userId
	 * @param role
	 * @return true メンバとして追加 false すでにメンバ
	 */
	public boolean addSiteMember(String userId, Role role);
	public boolean addSiteMember(String userId, Role role, String siteId);
	
	/**
	 * return ServerConfigurationBoolean Value
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public boolean getServerBooleanValue(String key, boolean defaultValue);
}
