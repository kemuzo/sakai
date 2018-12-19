package org.sakaiproject.nssakura.learningStatus.logic;

import java.util.List;

import org.sakaiproject.nssakura.learningStatus.model.GradeSectionModel;
import org.sakaiproject.nssakura.learningStatus.model.GradeUserModel;
import org.sakaiproject.nssakura.learningStatus.model.MailForm;
import org.sakaiproject.nssakura.learningStatus.model.MessageModel;
import org.sakaiproject.nssakura.section.model.SectionModel;
import org.sakaiproject.user.api.User;

/**
 * An interface to abstract all Sakai related API calls in a central method that can be injected into our app.
 * 
 * @author Mike Jennings (mike_jennings@unc.edu)
 *
 */
public interface LearningStatusSakaiProxy {

	/**
	 * get Ta by sectionId
	 * @param sectionId
	 * @return
	 */
	public List<User> getTa(String sectionId);
	
	/**
	 * sectionごとの課題別受講率一覧を返す
	 */
	public List<GradeSectionModel> getGradebookData();

	/**
	 * セクションメンバごとのgradebookの点数一覧を返す
	 * @param sectionId
	 * @return
	 */
	public List<GradeUserModel> getGradebookDataBySection(String sectionId);

	/**
	 * sectionIdからsection名を返す
	 * @param sectionId
	 * @return
	 */
	public String getSectionTitle(String sectionId);

	/**
	 * sectionId, assignmentIdから未完了ユーザ一覧を返す
	 * @param sectionId
	 * @param assignmentIdStr
	 * @return
	 */
	public List<User> getNonexecUsers(String sectionId, String assignmentIdStr);

	/**
	 * 選択されたユーザにメールを送信する
	 * @param mailForm
	 */
	public int notify(MailForm mailForm);	

	/**
	 * messageIdからMailFormを返す
	 * @param messageId
	 * @return
	 */
	public MailForm getMailFormFromDB(Long messageId);
	
	/**
	 * currentSiteに登録されているRole一覧を返す
	 * @return
	 */
	public List<String> getRoles();
	
	/**
	 * SectionModel一覧を返す
	 * @return
	 */
	public List<SectionModel> getSectionModels ();
	
	/**
	 * サイトでユーザが送ったメッセージ一覧を返す
	 * @return
	 */
	public List<MessageModel> getMessageModels();
	
	/**
	 * CurrentUserを返す
	 * @return
	 */
	public String getSenderEmail();
	
	/**
	 * messageIdで指定されたメッセージをDBから削除する
	 * @param messageId
	 * @return
	 */
	public boolean removeMessage(Long messageId);
	
	//-----------------------------------------------------------------------//
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
	 * Is the current user a instructor?
	 * @return
	 */
	public boolean isInstructor();
	
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
}
