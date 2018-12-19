package org.sakaiproject.nssakura.section.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.nssakura.section.model.SectionModel;
import org.sakaiproject.section.api.SectionManager;
import org.sakaiproject.section.api.coursemanagement.Course;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.coursemanagement.ParticipationRecord;
import org.sakaiproject.section.api.facade.Role;

import lombok.Getter;
import lombok.Setter;

public class NssakuraSectionImpl implements NssakuraSection {
    private static final Logger log = Logger.getLogger(SakaiProxyImpl.class);
    private final String DELIMITER_PARAMETER="nssakura-section.delimiter";
    private final String LEVEL_PARAMETER="nssakura-section.level";
    private final int NO_LIMIT_LEVEL=-1;
    private static final int MAX_NAME_LENGTH = 10;

    private String delimiter = null;
    private Integer level = null;

    public void init(){
        this.delimiter = getDelimiter();
        this.level = getLevel();
    }

    /**
     * 組織グループ再構築
     */
    public void rebuildSections(){
        unsetExternallyManaged();
        Map<String,CourseSection> existSectionsMap = sakaiProxy.getSectionsMap();
        Map<String,CourseSection> removeSectionsMap = new HashMap<String, CourseSection>(existSectionsMap);
        HashSet<String> tas = new HashSet<String>();
        List<String> memberSections = sakaiProxy.getSectionNamesFromStudentMembers(tas);
        memberSections = getLevelLimitSectionsName(memberSections);
        Map<String,CourseSection> newSectionsMap = new HashMap<String, CourseSection>();
        //メンバ情報からセクショングループ一覧
        for(String memberSection:memberSections){
            List<String> hierarchyGroupNames = getHierarchyGroups(memberSection);
            //セクショングループ（階層化したすべてのグループ）で存在していないグループを抽出
            for(String groupName:hierarchyGroupNames){
                if( existSectionsMap.get(groupName) == null){
                    newSectionsMap.put(groupName, null);
                }else{
                    removeSectionsMap.remove(groupName);//最後に削除するセクショングループのみにする
                }
            }
        }
        Collection<CourseSection> newSections = sakaiProxy.addSections(transferMapToArray(newSectionsMap));
        //Mapに戻す
        for(CourseSection section:newSections){
            newSectionsMap.put(section.getTitle(), section);
        }
        //不要となったセクションを削除する
        sectionManager.disbandSections(getSectionIdsSet(removeSectionsMap));
        //受講者の割り当て
        reAssignedMember(tas);
        //TAの再割り当て;
        assignedTaToNewsection(newSections);
    }

    /**
     * 現在のサイトに登録されているメンバをプロパティから所属グループに所属させる
     * ta Roleのユーザを除外する
     */
    public void reAssignedMember(HashSet<String> tas){
        List<CourseSection>sectionList = sakaiProxy.getSections();
        for(CourseSection section:sectionList){

            List<String> userIdsAll = sakaiProxy.getUserIdsBySectionAllName(section.getTitle(), section.getTitle()+delimiter);
            List<String> userIds = new ArrayList<String>();
            for(String userId:userIdsAll){
                if(! tas.contains(userId)){
                    userIds.add(userId);
                }
            }
            removeMembers(section, userIds);
            if(userIds != null && userIds.size()>0){
                boolean result = sakaiProxy.setSectionMembers((String[])userIds.toArray(new String[0]), Role.STUDENT, section.getUuid());
            }
        }
    }

    /**
     * 新規作成したセクションの上位セクションのTAを割り当てる
     * @param newSections
     */
    public void assignedTaToNewsection(Collection<CourseSection> newSections){
        Map<String,CourseSection> existSectionsMap = sakaiProxy.getSectionsMap();

        for(CourseSection newSection:newSections){
            String upSection = getUplevelSection(newSection.getTitle());
            CourseSection existSection = existSectionsMap.get(upSection);
            if( existSection == null){
                continue;
            }
            List<ParticipationRecord> tas = sectionManager.getSectionTeachingAssistants(existSection.getUuid());
            sakaiProxy.setSectionMembersAndSiteMembers((String[])tas.toArray(new String[0]), Role.TA, existSection.getUuid());
        }
        Map<String,List<ParticipationRecord>> sectionTaMap = sectionManager.getSectionTeachingAssistantsMap((List)newSections);
    }

    /**
     * 組織グループの１個上位のグループ名を返す
     * @param sectionTitle
     * @return
     */
    public String getUplevelSection(String sectionTitle){
        String[] sections = sectionTitle.split(delimiter);
        if(sections == null || sections.length < 2){
            return null;
        }
        String result = sections[0];
        for(int i=1; i < sections.length-1; i++){
            result += delimiter + sections[i];
        }
        return result;
    }

    /**
     * 組織グループの１個上位のCourseSectionを返す
     * @param sectionTitle
     * @return
     */
    public CourseSection getUplevelCourseSection(String sectionTitle){
        String upSectionTitle = getUplevelSection(sectionTitle);
        Map<String, CourseSection> map = sakaiProxy.getSectionsMap();
        return map.get(upSectionTitle);
    }

    /**
     * セクショングループ名から構成する全階層グループを取得
     * @param sectionName
     * @return
     */
    public List<String> getHierarchyGroups(String sectionName){
        List<String> list = new ArrayList<String>();
        String[] names = sectionName.split(delimiter);
        String baseName = null;
        for(String name:names){
            if( baseName == null ){
                baseName = name;
            }else{
                baseName += delimiter + name;
            }
            list.add(baseName);
        }
        return list;
    }

    /**
     * セクション名の下位のセクションマップを返す
     * @param title
     * @return
     */
    public Map<String, CourseSection> getLowlevelGroups(String title){
        Map<String, CourseSection> sectionsMap = sakaiProxy.getSectionsMap();
        Map<String, CourseSection> resultMap = new HashMap<String, CourseSection>();
        Collection<CourseSection> sections = sectionsMap.values();
        Iterator it = sections.iterator();
        while( it.hasNext() ){
            CourseSection sec = (CourseSection)it.next();
            if(sec.getTitle().startsWith(title + delimiter)){
                resultMap.put(sec.getTitle(), sec);
            }
        }
        return resultMap;
    }

    /**
     * セクション名の上位のセクションマップを返す
     * @param title
     * @return
     */
    public Map<String, CourseSection> getHighlevelGroups(String title){
        Map<String, CourseSection> sectionsMap = sakaiProxy.getSectionsMap();
        Map<String, CourseSection> resultMap = new HashMap<String, CourseSection>();
        Collection<CourseSection> sections = sectionsMap.values();
        Iterator it = sections.iterator();
        while( it.hasNext() ){
            CourseSection sec = (CourseSection)it.next();
            if(title.startsWith(sec.getTitle() + delimiter)){
                resultMap.put(sec.getTitle(), sec);
            }
        }
        return resultMap;
    }

    /**
     * セクショングループメンバ削除(userIds)以外
     * @param section
     */
    public void removeMembers(CourseSection section, List<String> userIds){
        List<EnrollmentRecord> enrollments = sakaiProxy.getSectionEnrollment(section.getUuid());
        List<String> userIdList = new ArrayList<String>();
        for(EnrollmentRecord enrollment:enrollments){
            if(userIds.contains(enrollment.getUser().getUserUid())){
                continue;
            }
            userIdList.add(enrollment.getUser().getUserUid());
        }
        sakaiProxy.removeSectionMembers((String[])userIdList.toArray(new String[0]), section.getUuid());
    }

    /**
     * デリミタを返す
     * @return
     */
    public String getDelimiter(){
        try{
            if( delimiter == null ){
                delimiter = serverConfigurationService.getString(DELIMITER_PARAMETER);
            }
        }catch(Exception e){}
        if( delimiter == null ){
            delimiter = "/";
        }
        return delimiter;
    }

    public int getLevel(){
        try{
            if( level == null ){
                level = serverConfigurationService.getInt(LEVEL_PARAMETER, NO_LIMIT_LEVEL);
            }
        }catch(Exception e){}
        if( level == null ){
            level = NO_LIMIT_LEVEL;
        }
        return level;
    }
    /**
     * 表示用階層化IDを返す
     * @param title
     * @return
     */
    public String getTreeId(String title){
        if(title == null){
            return "";
        }
        return title.replaceAll(delimiter, "-");
    }

    /**
     * レベル範囲内のセクション一覧を返す
     * @return
     */
    public List<CourseSection> getSections(){
        List<CourseSection> sections = sakaiProxy.getSections();
        return getLevelLimitSections(sections);
    }

    /**
     * レベル範囲内のメンバユーザプロパティから取得したセクション名を返す
     * @return
     */
    public List<String> getSectionNamesFromStudentsWithinLevel(){
        List<String> sectionNames = sakaiProxy.getSectionNamesFromStudentMembers();
        return getLevelLimitSectionsName(sectionNames);
    }

    /**
     * 現在のサイトに設定されているセクションを取得する
     * TAの場合は、担当しているセクションのみを取得する
     * @return
     */
    public List<SectionModel> getSectionModels(){
        List<CourseSection> sections = getSections();
        sections = sortCourseSection(sections);
        List<SectionModel> models = new ArrayList<SectionModel>();
        if(sections == null || sections.size()<1){
            return models;
        }
        Map sectionSize = sakaiProxy.getEnrollmentCounts(sections);
        Map<String,List<ParticipationRecord>> sectionTAs = sakaiProxy.getSectionTas(sections);
        String userUid = sakaiProxy.getCurrentUserId();
        boolean taFlg = sakaiProxy.isSiteMemberInRoleTA();
        boolean adminFlg = sakaiProxy.isSuperUser();
        Set<String>idSet = new HashSet<String>();
        for(CourseSection section:sections) {
            List<ParticipationRecord> tas = (List<ParticipationRecord>) sectionTAs.get(section.getUuid());
            List<String> taNames = generateTaNames(tas);
            List<String> taUids = generateTaUids(tas);
            if( (! adminFlg) && taFlg && ( ! taUids.contains(userUid))) {
                if(log.isDebugEnabled()) log.debug("Filtering out " + section.getTitle() + ", since user " + userUid + " is not a TA");
                continue;
            }
            int totalEnrollments = sectionSize.containsKey(section.getUuid()) ?
                    (Integer) sectionSize.get(section.getUuid()) : 0;
            String id = section.getTitle();
            String parentId = getUplevelSection(section.getTitle());
            if(! idSet.contains(parentId)){
                //親なし
                parentId = null;
            }
            SectionModel model = new SectionModel(section, taNames,totalEnrollments,true,parentId,id, getDelimiter());
            idSet.add(id);
            models.add(model);
        }
        return models;
    }

    public List<CourseSection> sortCourseSection(List<CourseSection>sections){
        Collections.sort(sections, new Comparator<CourseSection>(){
            public int compare(CourseSection obj1, CourseSection obj2){
                String title1 = obj1.getTitle();
                String title2 = obj2.getTitle();
                return title1.compareTo(title2);
            }
        });
        return sections;
    }

    /**
     * セクション名を階層分解して登録する
     * @param sectionNames
     * @return
     */
    public Collection<CourseSection> addSections(String[] sectionNames){
        TreeSet<String> checkSection = new TreeSet<String>();
        for(String sectionName:sectionNames){
            List<String> hierarchyGroupNames = getHierarchyGroups(sectionName);
            checkSection.addAll(hierarchyGroupNames);
        }
        List<String> sections = new ArrayList<String>();
        sections.addAll(checkSection);
        return sakaiProxy.addSections((String[])sections.toArray(new String[0]));
    }

    // private method
    private List<String> generateTaNames(List<ParticipationRecord> tas) {
        // Generate the string showing the TAs
        List<String> taNames = new ArrayList<String>();
        for(Iterator taIter = tas.iterator(); taIter.hasNext();) {
            ParticipationRecord ta = (ParticipationRecord)taIter.next();
            taNames.add(StringUtils.abbreviate(ta.getUser().getSortName(), MAX_NAME_LENGTH));
        }

        Collections.sort(taNames);
        return taNames;
    }

    private List<String> generateTaUids(List<ParticipationRecord> tas) {
        List<String> taUids = new ArrayList<String>();
        for(Iterator<ParticipationRecord> iter = tas.iterator(); iter.hasNext();) {
            taUids.add(iter.next().getUser().getUserUid());
        }
        return taUids;
    }

    private List<CourseSection> getLevelLimitSections(List<CourseSection> sections){
        List<CourseSection> limitLevelSections = new ArrayList<CourseSection>();
        if(getLevel() == NO_LIMIT_LEVEL){
            return sections;
        }
        for(CourseSection section:sections){
            if( isLevelIn(section.getTitle())){
                limitLevelSections.add(section);
            }
        }
        return limitLevelSections;
    }

    private List<String> getLevelLimitSectionsName(List<String> titles){
        List<String> limitLevelSections = new ArrayList<String>();
        // for overlap exclusion
        TreeSet<String> checkSection = new TreeSet<String>();
        if(getLevel() == NO_LIMIT_LEVEL){
            return titles;
        }
        for(String title:titles){
            String levelTitle = getLevelInSection(title);
            checkSection.add(levelTitle);
        }
        limitLevelSections.addAll(checkSection);
        return limitLevelSections;
    }

    /**
     * 表示レベル内のセクション名を返す
     * @param title
     * @return
     */
    private String getLevelInSection(String title){
        if( title == null || title.isEmpty()){
            return title;
        }
        title = title.replaceAll("^/", "");
        title = title.replaceAll("/$", "");
        String[] titles = title.split(getDelimiter());
        if( titles == null || titles.length<=getLevel()){
            return title;
        }
        title = titles[0];
        for(int i=1; i<getLevel(); i++){
            title += getDelimiter() + titles[i];
        }
        return title;
    }

    /**
     * 表示レベルのセクションか否かを返す
     */
    private boolean isLevelIn(String title){
        if( title == null || title.isEmpty()){
            return true;
        }
        title = title.replaceAll("^/", "");
        title = title.replaceAll("/$", "");
        String[] titles = title.split(getDelimiter());
        if( titles == null || titles.length<getLevel()){
            return true;
        }
        if ( titles.length > getLevel()){
            return false;
        }
        return true;
    }

    private String[] transferMapToArray(Map<String, CourseSection> map){
        List<String> list = new ArrayList<String>();
        Iterator ite = map.keySet().iterator();
        int n=0;
        while(ite.hasNext()){
            list.add((String)ite.next());
        }
        return (String[])list.toArray(new String[list.size()]);
    }

    private Set<String> getSectionIdsSet(Map<String, CourseSection> map){
        Set<String> resultSet = new HashSet<String>();
        Iterator ite = map.keySet().iterator();
        int n=0;
        while(ite.hasNext()){
            CourseSection section= map.get(ite.next());
            resultSet.add(section.getUuid());
        }
        return resultSet;
    }
    /**
     * 手動でセクションを追加できるようにする
     */
    private void unsetExternallyManaged(){
        Course course = sectionManager.getCourse(sakaiProxy.getCurrentSiteId());
        if(sectionManager.isExternallyManaged(course.getUuid())){
            log.info("CHANGE: SECTION OPTION TO 'Interal Managed' " + course.getUuid());
            sectionManager.setExternallyManaged(course.getUuid(), false);
        }
    }
    //external service
    @Setter @Getter
    private ServerConfigurationService serverConfigurationService;
    @Setter @Getter
    private SectionManager sectionManager;

    @Setter @Getter
    private SakaiProxy sakaiProxy;

}
