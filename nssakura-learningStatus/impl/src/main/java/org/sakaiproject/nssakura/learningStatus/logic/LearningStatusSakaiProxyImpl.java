package org.sakaiproject.nssakura.learningStatus.logic;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.nssakura.learningStatus.dao.NSMessageDao;
import org.sakaiproject.nssakura.learningStatus.db.BaseHandlerSql;
import org.sakaiproject.nssakura.learningStatus.model.GradeAssignmentItem;
import org.sakaiproject.nssakura.learningStatus.model.GradeModel;
import org.sakaiproject.nssakura.learningStatus.model.GradeSectionModel;
import org.sakaiproject.nssakura.learningStatus.model.GradeUserModel;
import org.sakaiproject.nssakura.learningStatus.model.MailForm;
import org.sakaiproject.nssakura.learningStatus.model.MessageDaoModel;
import org.sakaiproject.nssakura.learningStatus.model.MessageModel;
import org.sakaiproject.nssakura.section.logic.NssakuraSection;
import org.sakaiproject.nssakura.section.model.SectionModel;
import org.sakaiproject.section.api.SectionManager;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.coursemanagement.ParticipationRecord;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

/**
 * Implementation of {@link LearningStatusSakaiProxy}
 *
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class LearningStatusSakaiProxyImpl implements LearningStatusSakaiProxy {

    private static final Logger log = Logger.getLogger(LearningStatusSakaiProxyImpl.class);
    private long EXECUTED_POINTS;
    private static final String[] ROLE_STRING={"Instructor", "TA", "Student"};
    private static final String DELIMITER=",";

    /**
     * get Ta by sectionId
     * @param sectionId
     * @return
     */
    public List<User> getTa(String sectionId){
        List<ParticipationRecord> tas = sectionManager.getSectionTeachingAssistants(sectionId);
        List<User> users = new ArrayList<User>();
        for(ParticipationRecord ta:tas){
            String userId = ta.getUser().getUserUid();
            User user;
            try {
                user = userDirectoryService.getUser(userId);
                users.add(user);
            } catch (UserNotDefinedException e) {
                log.debug(e.getMessage());
            }
        }
        return users;
    }

    /**
     * sectionごとの課題別受講率一覧を返す
     */
    public List<GradeSectionModel> getGradebookData(){
        String siteId = getCurrentSiteId();
        long stime1 = System.currentTimeMillis();
        List<SectionModel> sections = nssakuraSection.getSectionModels();
        long stime2 = System.currentTimeMillis();
        Map<String, HashSet<SectionModel>> studentSectionMap = getStudentSectionMap(sections);
        long stime3 = System.currentTimeMillis();
        long stime4 = System.currentTimeMillis();
        List<Assignment> gbitems = getGradeAssignments(siteId);
        TreeMap<String, GradeSectionModel> sectionAssignmentMap = new TreeMap<String, GradeSectionModel>();
        for(SectionModel section:sections){
            GradeSectionModel gradeSectionModel = new GradeSectionModel(section, gbitems);
            sectionAssignmentMap.put(section.getSection().getTitle(), gradeSectionModel);
        }
        //Total集計用
        int studentTotalNum = studentSectionMap.keySet().size();
        GradeSectionModel totalGradeSectionModel = new GradeSectionModel(gbitems, studentTotalNum);

        long stime5 = System.currentTimeMillis();
        TreeMap<Long, List<String>> gradeRecordMap = getGradeRecordMap(gbitems);
        long stime6 = System.currentTimeMillis();
        for (Assignment assignment : gbitems) {
            List<String> executedUserIds = gradeRecordMap.get(assignment.getId());
            for (String studentId: executedUserIds) {
                HashSet<SectionModel> courseSections = studentSectionMap.get(studentId);
                if( courseSections == null){
                    continue;
                }
                totalGradeSectionModel.addExecuteNum(assignment);//section参加者のみ加算
                Iterator it = courseSections.iterator();
                while(it.hasNext()){
                    SectionModel co = (SectionModel)it.next();
                    GradeSectionModel gradeSectionModel = sectionAssignmentMap.get(co.getSection().getTitle());
                    if(gradeSectionModel == null){
                        gradeSectionModel = new GradeSectionModel(co, gbitems);
                    }
                    gradeSectionModel.addExecuteNum(assignment);
                }
            }
        }
        long stime7 = System.currentTimeMillis();
        Iterator it = sectionAssignmentMap.keySet().iterator();
        List<GradeSectionModel> resultList = new ArrayList<GradeSectionModel>();
        resultList.add(0,totalGradeSectionModel);
        while(it.hasNext()){
            resultList.add(sectionAssignmentMap.get(it.next()));
        }
        long stime8 = System.currentTimeMillis();
        log.debug("Time for get gradebook data=" + (stime2-stime1)+":" + (stime3-stime2) + ":" + (stime4-stime3) + ":" + (stime5-stime4) + ":" + (stime6-stime5) + ":" + (stime7-stime6) + ":" + (stime8-stime7));
        return resultList;
    }

    /**
     * セクションメンバごとのgradebookの点数一覧を返す
     * @param sectionId
     * @return
     */
    public List<GradeUserModel> getGradebookDataBySection(String sectionId){
        String siteId = getCurrentSiteId();
        log.info("Admin or instructor or TA accesssing gradebook of site "
                + siteId);
        List<EnrollmentRecord> enrollments = sectionManager.getSectionEnrollments(sectionId);
        List<Assignment> gbitems = getGradeAssignments(siteId);
        TreeMap<String, GradeUserModel> userAssignmentMap = new TreeMap<String, GradeUserModel>();
        for(EnrollmentRecord enrollment:enrollments){
            String studentId = enrollment.getUser().getUserUid();
            User user;
            try {
                user = userDirectoryService.getUser(studentId);
            } catch (UserNotDefinedException e) {
                log.debug(e.getMessage());
                continue;
            }
            GradeUserModel gumodel = new GradeUserModel(user,gbitems);
            userAssignmentMap.put(studentId,gumodel);
        }
        getGradebookRecordsGroupbyUser(gbitems, userAssignmentMap);
        List<GradeUserModel> resultList = new ArrayList<GradeUserModel>(userAssignmentMap.values());
        sortGradeUserModelByEid(resultList);
        return resultList;

    }

    /**
     * sectionIdからsection名を返す
     * @param sectionId
     * @return
     */
    public String getSectionTitle(String sectionId){
        return sectionManager.getSection(sectionId).getTitle();
    }

    /**
     * sectionId, assignmentIdから未完了ユーザ一覧を返す
     * @param sectionId
     * @param assignmentIdStr
     * @return
     */
    public List<User> getNonexecUsers(String sectionId, String assignmentIdStr){
        List<EnrollmentRecord> students = sectionManager.getSectionEnrollments(sectionId);
        Long assignmentId = Long.valueOf(assignmentIdStr);
        List<String> passedStudentList = getPassedStudentsfromGradebookRecord(assignmentId);
        List<User> usersList = new ArrayList<User>();
        for(EnrollmentRecord student:students){
            String userId = student.getUser().getUserUid();
            if(! passedStudentList.contains(userId)){
                User user;
                try {
                    user = userDirectoryService.getUser(userId);
                    usersList.add(user);
                } catch (UserNotDefinedException e) {
                    log.debug(e.getMessage());
                }
            }
        }
        sortByEid(usersList);
        return usersList;
    }

    /**
     * ユーザEID順にソート
     * @param usersList
     */
    private void sortByEid(final List<User>usersList){
        Collections.sort(usersList, new Comparator<User>(){
            public int compare(User a1, User a2){
                String s1 = a1.getEid();
                String s2 = a2.getEid();
                return s1.compareTo(s2);
            }
        });
    }
    private void sortGradeUserModelByEid(final List<GradeUserModel>gbUserList){
        Collections.sort(gbUserList, new Comparator<GradeUserModel>(){
            public int compare(GradeUserModel a1, GradeUserModel a2){
                String s1 = a1.getUser().getEid();
                String s2 = a2.getUser().getEid();
                return s1.compareTo(s2);
            }
        });
    }

    /**
     * 選択されたユーザにメールを送信する
     * @param mailForm
     */
    public int notify(MailForm mailForm){
        String currentUserId = getCurrentUserId();
        User sender;
        try {
            sender = userDirectoryService.getUser(currentUserId);
        } catch (UserNotDefinedException e) {
            log.debug(e.getMessage());
            return -1;
        }
        String currentSiteId = getCurrentSiteId();
        Site site;
        try {
            site = siteService.getSite(currentSiteId);
        } catch (IdUnusedException e) {
            log.debug(e.getMessage());
            return -1;
        }
        MessageDaoModel model = new MessageDaoModel();
        model.setSiteId(currentSiteId);
        model.setUserId(currentUserId);
        model.setContent(mailForm.getContent());
        model.setSubject(mailForm.getSubject());
        TreeSet<User> usersTree = new TreeSet<User>();
        getUsersFromTo(mailForm, usersTree);
        model.setRecipientTo(getSendUsers(new ArrayList<User>(usersTree)));
        if(isSelectedRecipientCategory(mailForm)){
            getUsersFromRoleAndSection(mailForm, usersTree);
        }
        List<User> users = new ArrayList<User>(usersTree);
        int n = users.size();
        if(n < 1){
            return n;
        }
        int maxmailsend = Integer.MAX_VALUE;
        try{
            maxmailsend = serverConfigurationService.getInt("nssakura.mail.max", Integer.MAX_VALUE);
        }catch(Exception e){}
        int mailinterval = 0;
        try{
            mailinterval = serverConfigurationService.getInt("nssakura.mail.interval", 0);
        }catch(Exception e){}
        userNotificationService.NotifyNewmessages(users, getSenderEmail(), sender.getDisplayName(), model, maxmailsend, mailinterval);
        model.setSendDate(new Date());
        model.setRecipientRoleFromArrays(mailForm.getRoles());
        model.setRecipientSectionGroupFromArrays(mailForm.getSections());
        dao.save(model);
        return n;
    }

    /**
     * messageIdからMailFormを返す
     * @param messageId
     * @return
     */
    public MailForm getMailFormFromDB(Long messageId){
        try{
            MessageDaoModel model = dao.findById(messageId);
            String currentSiteId = getCurrentSiteId();
            if( ! model.getSiteId().equals(currentSiteId)){
                log.debug("Site Id is invalid." + currentSiteId + ":" + model.getSiteId());
                return null;
            }
            MailForm mailForm = new MailForm();
            mailForm.setContent(model.getContent());
            mailForm.setSubject(model.getSubject());
            String to = joinEnter(model.getRecipientTo());
            if( to != null){
                mailForm.setTo(to);
            }
            String[] roles = splitByDelimiter(model.getRecipientRole());
            if( roles != null){
                mailForm.setRoles(roles);
            }
            String[] sections = splitByDelimiter(model.getRecipientSectionGroup());
            if( sections != null){
                mailForm.setSections(sections);
            }
            return mailForm;
        }catch(Exception e){

        }
        return null;
    }

    /**
     * messageIdで指定されたメッセージをDBから削除する
     * @param messageId
     * @return
     */
    public boolean removeMessage(Long messageId){
        try{
            MessageDaoModel model = dao.findById(messageId);
            dao.delete(model);
            return true;
        }catch(Exception e){
            log.debug(e.getMessage());
        }
        return false;
    }

    private String joinEnter(String data){
        String[] dataArray = splitByDelimiter(data);
        if(dataArray == null){
            return null;
        }
        return StringUtils.join(dataArray,"\n");
    }

    private String[] splitByDelimiter(String data){
        if(data==null){
            return null;
        }
        String[] result = data.split(DELIMITER);
        return result;
    }


    private List<User> getUserFromMailForm(MailForm mailForm){
        TreeSet<User> users = new TreeSet<User>();
        getUsersFromTo(mailForm, users);
        if(isSelectedRecipientCategory(mailForm)){
            getUsersFromRoleAndSection(mailForm, users);
        }
        return new ArrayList<User>(users);
    }

    private String getSendUsers(List<User>users){
        try{
            String result = users.get(0).getDisplayId();
            for(int i=1; i < users.size(); i++){
                result += DELIMITER + users.get(i).getDisplayId();
            }
            return result;
        }catch(Exception e){}
        return null;
    }

    /**
     * ロールまたは組織グループが選択されたか否かを返す
     * @param mailForm
     * @return true:選択されている false:選択されていない
     */
    private boolean isSelectedRecipientCategory(MailForm mailForm){
        String[] roles = mailForm.getRoles();
        String[] sections = mailForm.getSections();
        if(roles != null && roles.length>0){
            return true;
        }
        if(sections != null && sections.length>0){
            return true;
        }
        return false;
    }

    private void getUsersFromTo(MailForm mailForm, TreeSet<User> users){
        String toUsers = mailForm.getTo();
        if( toUsers == null || toUsers.isEmpty()){
            return;
        }
        toUsers.replaceAll("\r\n", "\n");
        toUsers.replaceAll("\n\r", "\n");
        String[]usersArray = toUsers.split("\n");
        for(String userEid:usersArray){
            User user;
            try {
                user = userDirectoryService.getUserByEid(userEid);
                users.add(user);
            } catch (UserNotDefinedException e) {
                log.debug(e.getMessage());
            }
        }
        return;
    }

    private void getUsersFromRoleAndSection(MailForm mailForm, TreeSet<User> users){
        String[] roles = mailForm.getRoles();
        String[] sections = mailForm.getSections();
        boolean instructorFlg = false;
        boolean taFlg = false;
        boolean studentFlg = false;
        if(roles == null || roles.length<1){
            instructorFlg = true;
            taFlg = true;
            studentFlg = true;
        }else{
            for(String role:roles){
                if(ROLE_STRING[0].equals(role)){
                    instructorFlg = true;
                }
                if(ROLE_STRING[1].equals(role)){
                    taFlg = true;
                }
                if(ROLE_STRING[2].equals(role)){
                    studentFlg = true;
                }
            }
        }
        if(sections==null || sections.length<1){
            setUserFromRole(users,instructorFlg, taFlg, studentFlg);
        }else{
            for(String sectionId: sections){
                setUserFromRoleBySection(sectionId,users,taFlg, studentFlg);
            }
            if( instructorFlg ){
                setUserFromRole(users, true,false, false);
            }
        }
    }

    private void setUserFromRole(TreeSet<User>users, boolean instFlg, boolean taFlg, boolean stFlg){
        List<ParticipationRecord> prs=new ArrayList<ParticipationRecord>();
        if(instFlg){
            List list = sectionManager.getSiteInstructors(getCurrentSiteId());
            if( list != null && list.size()>0){
                prs.addAll(list);
            }
        }else if (taFlg){
            List list = sectionManager.getSiteTeachingAssistants(getCurrentSiteId());
            if( list != null && list.size()>0){
                prs.addAll(list);
            }
        }else if(stFlg){
            List list = sectionManager.getSiteEnrollments(getCurrentSiteId());
            if( list != null && list.size()>0){
                prs.addAll(list);
            }
        }
        for(ParticipationRecord pr:prs){
            try {
                User user = userDirectoryService.getUser(pr.getUser().getUserUid());
                users.add(user);
            } catch (UserNotDefinedException e) {
                log.debug(e.getMessage());
            }
        }
    }

    private void setUserFromRoleBySection(String sectionId, TreeSet<User>users, boolean taFlg, boolean stFlg){
        List<ParticipationRecord> prs=new ArrayList<ParticipationRecord>();
        if(taFlg){
            List list = sectionManager.getSectionTeachingAssistants(sectionId);
            if( list != null && list.size()>0){
                prs.addAll(list);
            }
        }else if(stFlg){
            List list = sectionManager.getSectionEnrollments(sectionId);
            if( list != null && list.size()>0){
                prs.addAll(list);
            }
        }
        for(ParticipationRecord pr:prs){
            try {
                User user = userDirectoryService.getUser(pr.getUser().getUserUid());
                users.add(user);
            } catch (UserNotDefinedException e) {
                log.debug(e.getMessage());
            }
        }
    }

    /**
     * currentSiteに登録されているRole一覧を返す
     * @return
     */
    public List<String> getRoles(){
        return Arrays.asList(ROLE_STRING);
        /**String siteId = getCurrentSiteId();
        Site site;
        List<String> resultArray = new ArrayList<String>();
        try {
            site = siteService.getSite(siteId);
            Set<Role> roles = site.getRoles();
            for(Role role:roles){
                resultArray.add(role.getId());
            }
        } catch (IdUnusedException e) {
            log.debug(e.getMessage());
        }
        return resultArray;*/
    }

    //---------------------------------------------------------------------------//
    private Map<String, HashSet<SectionModel>> getStudentSectionMap(List<SectionModel> sectionModels){
        Map<String, HashSet<SectionModel>> studentSectionMap = new HashMap<String, HashSet<SectionModel>>();
        for(SectionModel sectionModel: sectionModels){
            CourseSection section = sectionModel.getSection();
            List<EnrollmentRecord> students = sectionManager.getSectionEnrollments(section.getUuid());
            for(EnrollmentRecord student:students){
                String userId = student.getUser().getUserUid();
                HashSet<SectionModel> courseSet = studentSectionMap.get(userId);
                if( courseSet == null){
                    courseSet = new HashSet<SectionModel>();
                }
                courseSet.add(sectionModel);
                studentSectionMap.put(userId, courseSet);
            }
        }
        return studentSectionMap;
    }

    /**
     * assignmentIdをキーとする受講済みユーザリストMapを返す
     * @param assignments
     * @return
     */
    private TreeMap<Long, List<String>> getGradeRecordMap(List<Assignment> assignments){
        TreeMap<Long, List<String>> map = new TreeMap<Long, List<String>>();
        for(Assignment assignment:assignments){
            List<String> resultList = getPassedStudentsfromGradebookRecord(assignment.getId());
            map.put(assignment.getId(), resultList);
        }
        return map;
    }

    /**
     * assignmentを受講済みのユーザリストを返す
     * @param assignment
     * @return
     */
    private List<String> getPassedStudentsfromGradebookRecord(Long assignmentId){
        String statement = baseHandlerSql.getPassedStudentsfromGradebookRecordSql();
        Object fields[] = new Object[2];
        fields[0] = assignmentId;
        fields[1] = EXECUTED_POINTS;
        List<String> userIdList = sqlService.dbRead(statement,fields,null);
        return userIdList;
    }

    /**
     * userAssignmentMapにpointを入れる
     * @param assignments
     * @param userAssignmentMap
     */
    private void getGradebookRecordsGroupbyUser(List<Assignment> assignments, TreeMap<String, GradeUserModel> userAssignmentMap){
        for(Assignment assignment:assignments){
            HashMap<String, GradeModel> grades = getGradebookRecord(assignment);
            for(String userId: userAssignmentMap.keySet()){
                GradeModel grade = grades.get(userId);
                if( grade == null){
                    continue;
                }
                GradeUserModel guModel = userAssignmentMap.get(userId);
                if( guModel == null){
                    continue;
                }
                List<GradeAssignmentItem> asList = guModel.getAssignmentItems();
                for(GradeAssignmentItem gaitem:asList){
                    if(gaitem.getItemId().equals(assignment.getId())){
                        gaitem.setPoints(grade.getPoint());
                        continue;
                    }
                }

            }
        }
    }

    /**
     * assingmentに対するgradebookRecordを取得する
     * @param assignment
     * @return
     */
    private HashMap<String, GradeModel> getGradebookRecord(Assignment assignment){
        String statement = baseHandlerSql.getGradebookRecordSql();
        Object fields[] = new Object[1];
        fields[0] = assignment.getId();
        final List<GradeModel> list = sqlService.dbRead(statement,fields, new SqlReader()
        {
            public GradeModel readSqlResultRecord(ResultSet result)
            {
                try{
                    String userId = result.getString("STUDENT_ID");
                    Double point = result.getDouble("POINTS_EARNED");
                    Date date = result.getDate("DATE_RECORDED");
                    GradeModel model = new GradeModel(userId,point,date);
                    return model;
                }catch(Exception ignore){
                    log.debug(ignore.getMessage());
                };
                return null;
            }
        });
        HashMap<String, GradeModel> map = new HashMap<String,GradeModel>();
        for(GradeModel model:list){
            map.put(model.getUserId(), model);
        }
        return map;
    }
    /**
     * 成績簿に登録されているアイテムをソート順で返す
     * @param siteId
     * @return
     */
    private List<Assignment> getGradeAssignments(String siteId){
        String statement = baseHandlerSql.getGradableObjectSql();
        Object fields[] = new Object[1];
        fields[0] = siteId;
        final List<Long> sortedList = sqlService.dbRead(statement,fields, new SqlReader()
        {
            public Object readSqlResultRecord(ResultSet result)
            {
                try{
                    return result.getLong("ID");
                }catch (Exception ignore){
                    log.debug(ignore.getMessage());
                };
                return null;
            }
        });
        List<Assignment> gbitems = gradebookService.getAssignments(siteId);
        //gbitems をsortedListの順にソートする
        Collections.sort(gbitems, new Comparator<Assignment>(){
            public int compare(Assignment a1, Assignment a2){
                long n1 = a1.getId();
                long n2 = a2.getId();
                int index1 = sortedList.indexOf(n1);
                int index2 = sortedList.indexOf(n2);
                if(index2 == -1){
                    return -1;
                }
                if(index1 == -1){
                    return 1;
                }
                return index1 - index2;
            }
        });
        return gbitems;
    }

    /**
     * SectionModel一覧を返す
     * @return
     */
    public List<SectionModel> getSectionModels (){
        return nssakuraSection.getSectionModels();
    }

    /**
     * サイトでユーザが送ったメッセージ一覧を返す
     * @return
     */
    public List<MessageModel> getMessageModels(){
        List<MessageDaoModel> daoModels = null;
        String siteId = getCurrentSiteId();
        if( isSuperUser() || isInstructor())
        {
            daoModels = dao.findMessageBySiteId(siteId);
        }else{
            daoModels = dao.findMessageBySiteIdAndUserId(siteId, getCurrentUserId());
        }
        List<MessageModel> models = new ArrayList<MessageModel>();
        for(MessageDaoModel daoModel:daoModels){
            String userId = daoModel.getUserId();
            try{
                User user = userDirectoryService.getUser(userId);
                MessageModel model = new MessageModel(daoModel, user);
                models.add(model);
            }catch(Exception e){
                log.debug(e.getMessage());
            }
        }
        return models;
    }

    /**
     * カレントユーザのメールアドレスを返す
     * 未登録の場合はno-reply
     * @return
     */
    public String getSenderEmail(){
        User user =userDirectoryService.getCurrentUser();
        String replyToEmail = user.getEmail();
        if(replyToEmail == null || replyToEmail.isEmpty()){
            String siteId = getCurrentSiteId();
            replyToEmail = "no-reply@" + serverConfigurationService.getServerName();
        }
        return replyToEmail;
    }

    //------------------------------------------------------------------//
    private boolean isExecuted(String grade){
        if( grade == null ){
            return false;
        }
        try{
            int n = Integer.parseInt(grade);
            return n>0;
        }catch(Exception e){}
        return true;
    }
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

    public boolean isInstructor(){
        return securityService.unlock(getCurrentUserId(),"site.upd", "/site/"+getCurrentSiteId());
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
    private String vendor;
    public void init() {
        log.info("init");
        vendor = serverConfigurationService.getString("vendor@org.sakaiproject.db.api.SqlService", null);
        baseHandlerSql = (databaseBeans.containsKey(vendor) ? databaseBeans.get(vendor) : databaseBeans.get("default"));
        EXECUTED_POINTS = serverConfigurationService.getInt("nssakura_executed_point", 0);
    }

    //-------------------------------------------//

    /**
     * original 遅い
     */
    private Collection<String> getStudentList(String siteId) {
        // this only works in the post-2.5 gradebook -AZ
        // Let the gradebook tell use how it defines the students The
        // gradebookUID is the siteId
        String gbID = siteId;
        if (!gradebookService.isGradebookDefined(gbID)) {
            throw new IllegalArgumentException(
                    "No gradebook found for course ("
                            + siteId
                            + "), gradebook must be installed in each course to use with this");
        }

        ArrayList<String> result = new ArrayList<String>();

//        @SuppressWarnings("unchecked")
        Map<String, String> studentToPoints = Collections.emptyMap(); // gradebookService.getFixedPoint(gbID);

        ArrayList<String> eids = new ArrayList<String>(studentToPoints.keySet());

        List<User> users = userDirectoryService.getUsersByEids(eids);
        for(User u: users) {
            result.add(u.getId());
        }

        Collections.sort(result);

        return result;
    }

    /**
     * userIdからDisplayNameを返す
     * @param uid
     * @return
     */
    private String getUserDisplayName(String uid) {
        try {
            User user = userDirectoryService.getUser(uid);
            return user.getDisplayName();
        } catch (UserNotDefinedException e) {
            log.warn("Undefined user id (" + uid + ")");
            return null;
        }
    }
    //-------------------------------------------//
    @Getter @Setter
    private ToolManager toolManager;

    @Getter @Setter
    private SessionManager sessionManager;

    @Getter @Setter
    private UserDirectoryService userDirectoryService;

    @Getter @Setter
    private SecurityService securityService;

    @Getter @Setter
    private EventTrackingService eventTrackingService;

    @Getter @Setter
    private ServerConfigurationService serverConfigurationService;

    @Getter @Setter
    private SiteService siteService;

    @Getter @Setter
    private GradebookService gradebookService;

    @Getter @Setter
    private SectionManager sectionManager;

    @Getter @Setter
    private NssakuraSection nssakuraSection;

    @Getter @Setter
    private Map<String, BaseHandlerSql> databaseBeans;
    @Getter @Setter
    private BaseHandlerSql baseHandlerSql;
    @Getter @Setter
    private SqlService sqlService;
    @Getter @Setter
    private UserNotificationService userNotificationService;
    @Getter @Setter
    private NSMessageDao dao;
}
