package org.sakaiproject.nssakura.section.logic;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.nssakura.section.Constant;
import org.sakaiproject.nssakura.section.db.BaseHandlerSql;
import org.sakaiproject.nssakura.section.exception.DuplicateGroupMemberException;
import org.sakaiproject.nssakura.section.exception.SiteSaveException;
import org.sakaiproject.nssakura.section.exception.UnmachedRoleException;
import org.sakaiproject.nssakura.section.model.CourseSectionModel;
import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.section.api.SectionManager;
import org.sakaiproject.section.api.coursemanagement.Course;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.coursemanagement.ParticipationRecord;
import org.sakaiproject.section.api.exception.RoleConfigurationException;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserAlreadyDefinedException;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.user.api.UserIdInvalidException;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.api.UserPermissionException;

import lombok.Data;

/**
 * Implementation of {@link SakaiProxy}
 *
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
@Data
public class SakaiProxyImpl implements SakaiProxy {

    private static final Logger log = Logger.getLogger(SakaiProxyImpl.class);
    private static final String STUDENT_ROLE = "Student";
    private static final String TA_ROLE = "Teaching Assistant";
    /**
     * {@inheritDoc}
     */
    public String getCurrentSiteId(){
        return toolManager.getCurrentPlacement().getContext();
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrentUserId() {
        return sessionManager.getCurrentSessionUserId();
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrentUserDisplayName() {
       return userDirectoryService.getCurrentUser().getDisplayName();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSuperUser() {
        return securityService.isSuperUser();
    }

    /**
     * {@inheritDoc}
     */
    public void postEvent(String event,String reference,boolean modify) {
        eventTrackingService.post(eventTrackingService.newEvent(event,reference,modify));
    }

    /**
     * {@inheritDoc}
     */
    public String getSkinRepoProperty(){
        return serverConfigurationService.getString("skin.repo");
    }

    /**
     * {@inheritDoc}
     */
    public String getToolSkinCSS(String skinRepo){

        String skin = siteService.findTool(sessionManager.getCurrentToolSession().getPlacementId()).getSkin();

        if(skin == null) {
            skin = serverConfigurationService.getString("skin.default");
        }

        return skinRepo + "/" + skin + "/tool.css";
    }

    /**
     * init - perform any actions required here for when this bean starts up
     */
    public void init() {
        log.info("init");
        vendor = serverConfigurationService.getString("vendor@org.sakaiproject.db.api.SqlService", null);
        baseHandlerSql = (databaseBeans.containsKey(vendor) ? databaseBeans.get(vendor) : databaseBeans.get("default"));
        user_property_section = serverConfigurationService.getString(Constant.SAKAI_PROPERTY_NAME_SECTION__KEY_OF_USER_PROPERTY, "organization");
        if("true".equals(serverConfigurationService.getString("auto.ddl"))){
            sqlService.ddl(this.getClass().getClassLoader(), "sakai_nssakura-section");
        }
    }

    //for NSSakura
    /**
     * Get section groups in the current site.
     * @return section list
     */
    public List<CourseSection> getSections(){
        String siteId = getCurrentSiteId();
        List<CourseSection> sectionList = sectionManager.getSections(siteId);
        return sectionList;
    }

    /**
     * Get section groups map in the current site
     * @return section map
     */
    public Map<String, CourseSection> getSectionsMap(){
        List<CourseSection> sections = getSections();
        Map<String, CourseSection> map = new HashMap<String, CourseSection>();
        for(CourseSection section:sections){
            map.put(section.getTitle(), section);
        }
        return map;
    }

    /**
     * Get Section From SectionId
     * @param sectionId
     * @return
     */
    public CourseSection getSection(String sectionId){
        return sectionManager.getSection(sectionId);
    }

    /**
     * Get section Tas map
     * @param sections
     * @return
     */
    public Map<String,List<ParticipationRecord>> getSectionTas(List<CourseSection> sections){
        return sectionManager.getSectionTeachingAssistantsMap(sections);
    }

    /**
     * Get Tas from SectionId
     * @param sectionId
     * @return
     */
    public List<ParticipationRecord> getSectionTas(String sectionId){
        return sectionManager.getSectionTeachingAssistants(sectionId);
    }

    /**
     * Get Tas from SiteId
     * @param siteId
     * @return
     */
    public List<ParticipationRecord> getSiteTeachingAssistants(String siteId){
        return sectionManager.getSiteTeachingAssistants(siteId);
    }

    /**
     * set OverWrite Member to Section
     * @param selectedUsers
     * @param role
     * @param sectionId
     * @return result flg
     */
    public boolean setSectionMembersAndSiteMembers(String[] selectedUsers, Role role, String sectionId){
        unsetExternallyManaged();
        Set<String> set = new HashSet<String>();
        Site site = getSite(getCurrentSiteId());
        boolean chgFlg = false;
        for(String uid : selectedUsers){
            try{
                Member member = site.getMember(uid);
                if(member == null){
                    site.addMember(uid, getRoleString(role), true, false);
                    chgFlg = true;
                }
            }catch(Exception e){}
            set.add(uid);
        }
        if( chgFlg ){
            try {
                siteService.save(site);
            } catch (IdUnusedException e) {
                log.debug(e.getMessage());
                return false;
            } catch (PermissionException e) {
                log.debug(e.getMessage());
                return false;
            }
        }
        try {
            sectionManager.setSectionMemberships(set, role, sectionId);
        } catch (RoleConfigurationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * set OverWrite Member to Section
     * @param selectedUsers
     * @param role
     * @param sectionId
     * @return result flg
     */
    public boolean setSectionMembers(String[] selectedUsers, Role role, String sectionId){
        unsetExternallyManaged();
        Set<String> set = new HashSet<String>();
        Site site = getSite(getCurrentSiteId());
        boolean chgFlg = false;
        for(String uid : selectedUsers){
            try{
                Member member = site.getMember(uid);
                if(member != null){
                    set.add(uid);
                }
            }catch(Exception e){}
        }
        try {
            sectionManager.setSectionMemberships(set, role, sectionId);
        } catch (RoleConfigurationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * set Members to Section
     * @param userIdList
     * @param role
     * @param sectionId
     * @return
     */
    public List<String> addSectionMembersAndSiteMembers(List<String> userIdList, Role role, String sectionId){
        unsetExternallyManaged();
        List<String> resultList = new ArrayList<String>();
        for(String userId : userIdList){
            try {
                boolean result = addSectionMemberOnlySiteMember(userId, role, sectionId);
                if(result){
                    resultList.add(userId);
                }
            } catch (UnmachedRoleException e) {
            } catch (SiteSaveException e) {
                e.printStackTrace();
            } catch (RoleConfigurationException e) {
                e.printStackTrace();
            } catch (DuplicateGroupMemberException e) {
            }
        }
        return resultList;
    }

    /**
     * set Member to Section(for TA)
     * @param selectedUsers
     * @param role
     * @param sectionId
     * @throws UnmachedRoleException
     * @throws SiteSaveException
     * @throws RoleConfigurationException
     * @throws DuplicateGroupMemberException
     */
    public void addSectionMember(String userId, Role role, String sectionId)
        throws UnmachedRoleException,SiteSaveException, RoleConfigurationException, DuplicateGroupMemberException{
        unsetExternallyManaged();
        try {
            Site site = getSite(getCurrentSiteId());
            if(site.getGroup(sectionId).getMember(userId) != null){
                throw new DuplicateGroupMemberException();
            }
            Member member = site.getMember(userId);
            String roleId = getRoleString(role);
            if(member == null){
                log.info("Add member [" + userId + "] to " + site.getId());
                site.addMember(userId, getRoleString(role), true, false);
            }else{
                if(! roleId.equals(member.getRole().getId())){
                    log.debug("Already member [" + userId + "] to " + site.getId() + "but different role.");
                    throw new UnmachedRoleException("Different role [" + member.getRole().getId() + " ][" +userId + "] to " + site.getId() + "but different role.");
                }

            }
            try {
                siteService.save(site);
            } catch (IdUnusedException e) {
                throw new SiteSaveException(e);
            } catch (PermissionException e) {
                throw new SiteSaveException(e);
            }
            ParticipationRecord participationRecord = sectionManager.addSectionMembership(userId, role, sectionId);
        } catch (RoleConfigurationException e) {
            throw e;
        }
    }

    /**
     * set Member to Sections(for Student)
     * @param selectedUsers
     * @param role
     * @param sectionIds list
     * @throws PermissionException
     * @throws IdUnusedException
     * @throws UnmachedRoleException
     * @throws SiteSaveException
     * @throws RoleConfigurationException
     * @throws DuplicateGroupMemberException
     */
    public void addSectionMember(String userId, Role role, List<CourseSection> sections) throws IdUnusedException, PermissionException, UnmachedRoleException, RoleConfigurationException{
        Site site = getSite(getCurrentSiteId());
        Member member = site.getMember(userId);
        String roleId = getRoleString(role);
        if(member == null){
            log.info("Add member [" + userId + "] to " + site.getId());
            site.addMember(userId, getRoleString(role), true, false);
            siteService.save(site);
        }else{
            if(! roleId.equals(member.getRole().getId())){
                log.debug("Already member [" + userId + "] to " + site.getId() + "but different role.");
                throw new UnmachedRoleException("Different role [" + member.getRole().getId() + " ][" +userId + "] to " + site.getId() + "but different role.");
            }
        }
        for(CourseSection section: sections){
            String sectionId = section.getUuid();
            List<EnrollmentRecord> enrollments = sectionManager.getSectionEnrollments(sectionId);
            Set<String> studentIds = new HashSet<String>();
            studentIds.add(userId);
            for(EnrollmentRecord enrollment:enrollments){
                studentIds.add(enrollment.getUser().getUserUid());
            }
            sectionManager.setSectionMemberships(studentIds, Role.STUDENT, sectionId);
        }
    }

    /**
     * カレントサイトにメンバとして追加する
     * @param userId
     * @param role
     * @return true メンバとして追加 false すでにメンバ
     */
    public boolean addSiteMember(String userId, Role role){
        return addSiteMember(userId, role, getCurrentSiteId());
    }
    public boolean addSiteMember(String userId, Role role, String siteId){
        Site site =  getSite(siteId);
        Member member = site.getMember(userId);
        String roleId = getRoleString(role);
        //一時的に権限を与える
        securityService.pushAdvisor(new SecurityAdvisor() {
            public SecurityAdvice isAllowed(String userId, String function, String reference) {
                if (function.equals(SiteService.SECURE_UPDATE_SITE)||
                        function.equals(SiteService.SECURE_UPDATE_GROUP_MEMBERSHIP)) {
                    return SecurityAdvice.ALLOWED;
                } else {
                    return SecurityAdvice.NOT_ALLOWED;
                }
            }
        });
        if( member == null ){
            log.info("Add member [" + userId + "] to " + site.getId());
            site.addMember(userId, getRoleString(role), true, false);
            try {
                siteService.save(site);
            } catch (IdUnusedException e) {
                log.debug(e.getMessage());
            } catch (PermissionException e) {
                log.debug(e.getMessage());
            }finally{
                securityService.clearAdvisors();
            }
            return true;
        }
        securityService.clearAdvisors();
        return false;
    }

    private boolean addSectionMemberOnlySiteMember(String userId, Role role, String sectionId)
    throws UnmachedRoleException,SiteSaveException, RoleConfigurationException, DuplicateGroupMemberException{
    unsetExternallyManaged();
    try {
        Site site = getSite(getCurrentSiteId());
        if(site.getGroup(sectionId).getMember(userId) != null){
            throw new DuplicateGroupMemberException();
        }
        Member member = site.getMember(userId);
        String roleId = getRoleString(role);
        if(member == null){
            log.info("Add member [" + userId + "] to " + site.getId());
            return false;
        }else{
            if(! roleId.equals(member.getRole().getId())){
                log.debug("Already member [" + userId + "] to " + site.getId() + "but different role.");
                return false;
            }

        }
        sectionManager.addSectionMembership(userId, role, sectionId);
        return true;
    } catch (RoleConfigurationException e) {
        return false;
    }
}

    /**
     * remove members from section
     * @param userIds
     * @param sectionId
     */
    public void removeSectionMembers(String[] userIds, String sectionId){
        unsetExternallyManaged();
        for(String userId: userIds){
            try{
                sectionManager.dropSectionMembership(userId, sectionId);
            }catch(Exception e){
                log.debug(e.getMessage());
            }
        }
    }
    /**
     * Get number of enrollments
     * @param sections
     * @return
     */
    public Map<String, Integer> getEnrollmentCounts(List<CourseSection> sections){
        /**
         * sakaiの機能を使うと、ユーザ削除の場合に、権限が消されずに数が合わない
         * Enrollmentを取得するとユーザがいるかどうかを判断しているが、重たくなる
         */
        //return sectionManager.getEnrollmentCount(sections);
        String statement = baseHandlerSql.getEnrollmentCounts();
        statement += baseHandlerSql.getEnrollmentCountsWhere();
        Object fields[] = new Object[sections.size()+2];
        fields[0] = Constant.SAKAI_SECTION_ROLE_STUDENT;
        fields[1] = "1"; // Active
        for(int i=0; i < sections.size(); i++){
            fields[i+2] = sections.get(i).getUuid();
            if( i== 0){
                statement += " ? ";
            }else{
                statement += " , ? ";
            }
        }
        statement += " )";
        statement += baseHandlerSql.getEnrollmentCountsGroupby();
        List<String[]> resultList = sqlService.dbRead(statement,fields, new SqlReader()
        {
            public Object readSqlResultRecord(ResultSet result)
            {
                try{
                    String[] res = new String[2];
                    res[0] = result.getString(1);
                    res[1] = result.getString(2);
                    return res;
                }catch (Exception ignore){
                    log.debug(ignore.getMessage());
                };
                return null;
            }
        });
        Map<String, Integer>map = new HashMap<String, Integer>();
        for(String[] res : resultList){
            String num_ = res[1];
            try{
                int num = Integer.parseInt(num_);
                map.put(res[0],num);
            }catch(Exception e){
                map.put(res[0],0);
            }
        }
        return map;
    }

    /**
     * Get enrollments of section.
     * @param sectionId
     * @return
     */
    public List<EnrollmentRecord> getSectionEnrollment(String sectionId){
        return sectionManager.getSectionEnrollments(sectionId);
    }

    /**
     * role in Site TA or not ?
     * @return
     */
    public boolean isSiteMemberInRoleTA(){
        return sectionAwareness.isSiteMemberInRole(getCurrentSiteId(), getCurrentUserId(), Role.TA);
    }
    /**
     * role in Site Instructor or not ?
     * @return
     */
    public boolean isSiteMemberInRoleInstructor(){
        return sectionAwareness.isSiteMemberInRole(getCurrentSiteId(), getCurrentUserId(), Role.INSTRUCTOR);
    }

    /**
     * disband Sections
     * @param sectionIds
     */
    public void disbandSections(Set<String> sectionIds) {
        unsetExternallyManaged();
        sectionManager.disbandSections(sectionIds);
    }

    /**
     * Get section names from user's property.
     * @return section list
     */
    public List<String> getSectionNamesFromUsers(){
        String statement = baseHandlerSql.getSectionNamesSql();
        Object fields[] = new Object[2];
        fields[0] = user_property_section;
        fields[1] = STUDENT_PERMISSION;
        List<String> resultList = sqlService.dbRead(statement,fields,null);
        return resultList;
    }

    /**
     * Get section names from site member's property.
     * @return section list
     */
    public List<String> getSectionNamesFromMembers(){
        String statement = baseHandlerSql.getSectionNamesOfMembersSql();
        Object fields[] = new Object[3];
        fields[0] = getCurrentSiteId();
        fields[1] = STUDENT_PERMISSION;
        fields[2] = user_property_section;
        List<String> resultList = sqlService.dbRead(statement,fields,null);
        return resultList;
    }

    /**
     * Get section names from site students's property
     * @return
     */
    public List<String> getSectionNamesFromStudentMembers(){
        return getSectionNamesFromStudentMembers(new HashSet<String>());
    }
    public List<String> getSectionNamesFromStudentMembers(HashSet<String> tas){
        AuthzGroupService authzGroupService = ComponentManager.get(AuthzGroupService.class);
        String realmId = siteService.siteReference(getCurrentSiteId());
        List<String> resultList = new ArrayList<String>();
        AuthzGroup realm;
        try {
            realm = authzGroupService.getAuthzGroup(realmId);
            realm.getProviderGroupId();
        } catch (GroupNotDefinedException e) {
            log.debug(e.getMessage());
            return resultList;
        }
        Set<Member> grants = realm.getMembers();
        if (grants != null && !grants.isEmpty()){
            Set<String> rvEids = new HashSet<String>();
            for (Iterator<Member> i = grants.iterator(); i.hasNext();) {
                Member member = i.next();
                if(member.getRole().getId().equals(STUDENT_ROLE)){
                    rvEids.add(member.getUserEid());
                }else if(member.getRole().getId().equals(TA_ROLE)){
                    tas.add(member.getUserId());
                }
            }
            Map<String, User> eidToUserMap = new HashMap<String, User>();
            List<User> rvUsers = userDirectoryService.getUsersByEids(rvEids);
            TreeMap<String, String> tm = new TreeMap<String, String>();
            for(User user:rvUsers){
                ResourceProperties prop = user.getProperties();
                String sectionName = prop.getProperty(user_property_section);
                if( sectionName != null && sectionName.length()>0){
                    tm.put(sectionName, sectionName);
                }
            }
            Iterator it = tm.keySet().iterator();
            while( it.hasNext()){
                String sectionName = (String)it.next();
                resultList.add(sectionName);
            }
        }
        return resultList;
    }

    /**
     * Get userids from user's property.
     * @return
     */
    public List<String> getUserIdsBySectionName(String sectionName){
        String statement = baseHandlerSql.getUserIdsBySectionNameSql();
        Object fields[] = new Object[2];
        fields[0] = user_property_section;
        fields[1] = sectionName;
        List<String> resultList = sqlService.dbRead(statement,fields,null);
        return resultList;
    }

    /**
     * Get userids from user's property. use like
     * @return
     */
    public List<String> getUserIdsBySectionAllName(String sectionName, String sectionVague){
        String statement = baseHandlerSql.getUserIdsBySectionNameAndSiteIdSql();
        Object fields[] = new Object[5];
        fields[0] = user_property_section;
        fields[1] = sectionName;
        fields[2] = sectionVague + "%";
        fields[3] = STUDENT_PERMISSION;
        fields[4] = getCurrentSiteId();
        List<String> resultList = sqlService.dbRead(statement,fields,null);
        return resultList;
    }

    /**
     * Add sections to course site.
     * @param sectionNames
     * @return added sections
     */
    public Collection<CourseSection> addSections(String[] sectionNames){
        Collection<CourseSection> courseSections = new ArrayList<CourseSection>();
        Course course = sectionManager.getCourse(getCurrentSiteId());
        for(String sectionName : sectionNames ){
            CourseSection courseSection = new CourseSectionModel(course, sectionName, Constant.SECTION_DEFAULT_CATEGORY);
            courseSections.add(courseSection);
        }
        unsetExternallyManaged();
        Collection<CourseSection> addSections = sectionManager.addSections(course.getUuid(), courseSections);
        return addSections;
    }

    /**
     * 現在のサイトに登録されているメンバを返す
     * @return
     */
    public Set<Member> getSiteMember(){
        String siteId = getCurrentSiteId();
        Site site;
        try {
            site = siteService.getSite(siteId);
        } catch (IdUnusedException e1) {
            e1.printStackTrace();
            return null;
        }
        if(site != null){
            try {
                Set<Member> members = site.getMembers();
                return members;
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }
        return null;
    }

    public Site getSite(String siteId) {
        try {
            Site site = siteService.getSite(siteId);
            return site;
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        return null;
    }

    /**
     * get User by userId
     * @param userId
     * @return
     */
    public User getUser(String userId){
        User user = null;
        try {
            user = userDirectoryService.getUser(userId);
        } catch (UserNotDefinedException e) {
            return null;
        }
        return user;
    }

    /**
     * get User by userEid
     * @param userEid
     * @return
     */
    public User getUserByEid(String userEid){
        User user = null;
        try {
            user = userDirectoryService.getUserByEid(userEid);
        } catch (UserNotDefinedException e) {
            return null;
        }
        return user;
    }

    /**
     * get Users by type
     * @param type
     * @return
     */
    public List<User> getUserListByType(String type){
        try{
            String statement = baseHandlerSql.getUserIdsByType();
            Object fields[] = new Object[1];
            fields[0] = type;
            List<String> userIdList = sqlService.dbRead(statement,fields,null);
            List<User> users = userDirectoryService.getUsers(userIdList);
            return users;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Site member ? or not ?
     * @param userId
     * @return
     */
    public boolean isSiteMember(String userId){
        try{
            Site site = getSite(getCurrentSiteId());
            Member member = site.getMember(userId);
            if(member != null){
                return true;
            }
        }catch(Exception e){
            log.debug(e.getMessage());
        }
        return false;
    }

    //
    public boolean isMyWorkspace(String siteId){
        boolean isMyWorkspace = false;
        if (siteService.isUserSite(siteId)) {
            if (siteService.getSiteUserId(siteId).equals(
                    sessionManager.getCurrentSessionUserId())) {
                isMyWorkspace = true;
            }
        }
        return isMyWorkspace;
    }

    public UserEdit createOrUpdateUserAfterAuthentication(String eid,
            String lastName, String firstName, String email, String password,
            String type, ResourceProperties userProperties){
        UserEdit user = null;
        //一時的に権限を与える
        securityService.pushAdvisor(new SecurityAdvisor() {
            public SecurityAdvice isAllowed(String userId, String function, String reference) {
                if (function.equals(UserDirectoryService.SECURE_UPDATE_USER_ANY) ||
                        function.equals(UserDirectoryService.SECURE_ADD_USER)) {
                    return SecurityAdvice.ALLOWED;
                } else {
                    return SecurityAdvice.NOT_ALLOWED;
                }
            }
        });
        lastName = getName(lastName, firstName);
        firstName = "";
        try{
            user = (UserEdit)userDirectoryService.getUserByEid(eid);
            // get user for edit
            user = userDirectoryService.editUser(user.getId());
            user.getPropertiesEdit().addAll(userProperties);
            userDirectoryService.commitEdit(user);
        }catch (UserNotDefinedException e){
            try {
                user = (UserEdit)userDirectoryService.addUser(null, eid,
                        firstName, lastName, email, password, type, userProperties);
            } catch (UserIdInvalidException e1) {
                log.debug(e1.getMessage());
            } catch (UserAlreadyDefinedException e1) {
                log.debug(e1.getMessage());
            } catch (UserPermissionException e1) {
                log.debug(e1.getMessage());
            }
        } catch (UserAlreadyDefinedException e) {
            log.debug(e.getMessage());
        } catch (Exception e){
            log.debug(e.getMessage());
        }finally{
            securityService.clearAdvisors();
        }
        return user;
    }

    /**
     * lastNameとfirstNameを接続して返す
     * @param lastName
     * @param firstName
     * @return
     */
    private String getName(String lastName, String firstName){
        String name = lastName;
        if(firstName==null || firstName.isEmpty()){
            return name;
        }
        if(lastName==null || lastName.isEmpty()){
            return firstName;
        }
        return lastName + '\u3000' + firstName;
    }

    /**
     * return ServerConfigurationBoolean Value
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getServerBooleanValue(String key, boolean defaultValue){
        return serverConfigurationService.getBoolean(key, defaultValue);
    }

    /**
     * 手動でセクションを追加できるようにする
     */
    private void unsetExternallyManaged(){
        Course course = sectionManager.getCourse(getCurrentSiteId());
        if(sectionManager.isExternallyManaged(course.getUuid())){
            log.info("CHANGE: SECTION OPTION TO 'Interal Managed' " + course.getUuid());
            sectionManager.setExternallyManaged(course.getUuid(), false);
        }
    }

    //end
    private String getRoleString(Role role){
        if(role.equals(Role.INSTRUCTOR)){
            return "Instructor";
        }
        if(role.equals(Role.TA)){
            return "Teaching Assistant";
        }
        if(role.equals(Role.STUDENT)){
            return "Student";
        }
        return null;
    }

    private ToolManager toolManager;
    private SessionManager sessionManager;
    private UserDirectoryService userDirectoryService;
    private SecurityService securityService;
    private EventTrackingService eventTrackingService;
    private ServerConfigurationService serverConfigurationService;
    private SiteService siteService;
    //for NSSakura
    private SectionManager sectionManager;
    private SectionAwareness sectionAwareness;
    private SqlService sqlService;
    private String vendor;
    private Map<String, BaseHandlerSql> databaseBeans;
    private BaseHandlerSql baseHandlerSql;
    private String user_property_section="default";

    private final Integer STUDENT_PERMISSION=1;
    //end
}
