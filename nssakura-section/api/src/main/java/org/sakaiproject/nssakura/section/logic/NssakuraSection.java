package org.sakaiproject.nssakura.section.logic;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.sakaiproject.nssakura.section.model.SectionModel;
import org.sakaiproject.section.api.coursemanagement.CourseSection;

public interface NssakuraSection {

	/**
	 * 組織グループ再構築
	 */
	public void rebuildSections();
	
	/**
	 * 組織グループの１個上位のグループ名を返す
	 * @param sectionTitle
	 * @return
	 */
	public String getUplevelSection(String sectionTitle);

	/**
	 * 組織グループの１個上位のCourseSectionを返す
	 * @param sectionTitle
	 * @return
	 */
	public CourseSection getUplevelCourseSection(String sectionTitle);
	
	/**
	 * 表示用階層化IDを返す
	 * @param title
	 * @return
	 */
	public String getTreeId(String title);
	
	/**
	 * デリミタを返す
	 * @return
	 */
	public String getDelimiter();
	
	/**
	 * レベル範囲内のセクション一覧を返す
	 * @return
	 */
	public List<CourseSection> getSections();
	
	/**
	 * レベル範囲内のStudentメンバのユーザプロパティから取得したセクション名を返す
	 * @return
	 */
	public List<String> getSectionNamesFromStudentsWithinLevel();
	
	/**
	 * セクショングループ名から構成する全階層グループを取得
	 * @param sectionName
	 * @return
	 */
	public List<String> getHierarchyGroups(String sectionName);
	
	/**
	 * セクション名の下位のセクションマップを返す
	 * @param title
	 * @return
	 */
	public Map<String, CourseSection> getLowlevelGroups(String title);
	
	/**
	 * セクション名の上位のセクションマップを返す
	 * @param title
	 * @return
	 */
	public Map<String, CourseSection> getHighlevelGroups(String title);

	/**
	 * 現在のサイトに設定されているセクションを取得する
	 * TAの場合は、担当しているセクションのみを取得する
	 * @return
	 */
	public List<SectionModel> getSectionModels();
	
	/**
	 * セクション名でソートする
	 * @param sections
	 * @return
	 */
	public List<CourseSection> sortCourseSection(List<CourseSection>sections);

	
	/**
	 * セクション名を階層分解して登録する
	 * @param sectionNames
	 * @return
	 */
	public Collection<CourseSection> addSections(String[] sectionNames);
}
