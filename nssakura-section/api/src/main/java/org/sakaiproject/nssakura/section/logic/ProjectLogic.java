package org.sakaiproject.nssakura.section.logic;

import java.util.List;
import java.util.Map;

import org.sakaiproject.nssakura.section.exception.DuplicateGroupMemberException;
import org.sakaiproject.nssakura.section.exception.NoRegisteredUserException;
import org.sakaiproject.nssakura.section.exception.SiteSaveException;
import org.sakaiproject.nssakura.section.exception.UnmachedRoleException;
import org.sakaiproject.nssakura.section.model.SectionModel;
import org.sakaiproject.nssakura.section.model.SelectItem;
import org.sakaiproject.nssakura.section.model.UserCsvResultModel;
import org.sakaiproject.nssakura.section.model.UserModel;
import org.sakaiproject.section.api.exception.RoleConfigurationException;

/**
 * An example logic interface
 * 
 * @author Mike Jennings (mike_jennings@unc.edu)
 *
 */
public interface ProjectLogic {

	
	public SakaiProxy getSakaiProxy();
	
	/**
	 * return sections
	 * @return
	 */
	public List<SectionModel> getSectionModels();
	
	/**
	 * サイトに登録されていないセクション名リストを取得する
	 * @return
	 */
	public List<String> getUnregisteredSectionNames();
	
	/**
	 * get Tas SelectItem.
	 * @param sectionId
	 * @return
	 */
	public Map<String,List<SelectItem>> getSectionTasForRegister(String sectionId);
	
	/**
	 * get Enrollment Users in section
	 * @param sectionId
	 * @return
	 */
	public List<UserModel> getSectionEnrollmentUsers(String sectionId);
	
	/**
	 * set TA to Section
	 * @param selectedUsers
	 * @param sectionId
	 * @return resultList
	 */
	public List<String>[] setSectionTas(String[] selectedUsers, String sectionId);
	
	/**
	 * set Enrollment to section
	 * @param userEid
	 * @param sectionId
 	 * @throws UnmachedRoleException
	 * @throws SiteSaveException
	 * @throws RoleConfigurationException
	 * @throws DuplicateGroupMemberException
	 * @throws NoRegisteredUserException 
	 */
	public void addSectionEnrollmentUser(String userEid, String sectionId)
	throws UnmachedRoleException, SiteSaveException, RoleConfigurationException, DuplicateGroupMemberException, NoRegisteredUserException;

	/**
	 * remove Enrollment from section (hierarchy)
	 * 下位グループから削除
	 * @param userIds
	 * @param sectionId
	 */
	public void removeSecitonEnrollmentUser(String userIds[], String sectionId);
	/**
	 * get UserModel list from userId
	 * @param userIds
	 * @return
	 */
	public List<UserModel> getUserModelListByUserId(String[] userIds);
	
	/**
	 * CSVファイルを読み込んでUserリストを返す
	 * @param fileData
	 * @param encoding
	 * @return
	 */
	public UserCsvResultModel getImportUsers(byte[] fileData, String encoding);

	/**
	 * 読み込んだCSVファイルのデータから登録を行う
	 * @param model
	 */
	public void registerImportUsers(UserCsvResultModel model, boolean override);
	public void registerImportUsers(UserCsvResultModel model, boolean override, String siteId);
}
