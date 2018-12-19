package org.sakaiproject.nssakura.section.logic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.nssakura.section.Constant;
import org.sakaiproject.nssakura.section.exception.DuplicateGroupMemberException;
import org.sakaiproject.nssakura.section.exception.NoRegisteredUserException;
import org.sakaiproject.nssakura.section.exception.SiteSaveException;
import org.sakaiproject.nssakura.section.exception.UnmachedRoleException;
import org.sakaiproject.nssakura.section.model.SectionModel;
import org.sakaiproject.nssakura.section.model.SelectItem;
import org.sakaiproject.nssakura.section.model.UserCsvModel;
import org.sakaiproject.nssakura.section.model.UserCsvResultModel;
import org.sakaiproject.nssakura.section.model.UserModel;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.coursemanagement.ParticipationRecord;
import org.sakaiproject.section.api.coursemanagement.User;
import org.sakaiproject.section.api.exception.RoleConfigurationException;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.util.BaseResourceProperties;

import au.com.bytecode.opencsv.CSVReader;
import lombok.Data;


/**
 * Implementation of {@link ProjectLogic}
 *
 * @author Mike Jennings (mike_jennings@unc.edu)
 *
 */
@Data
public class ProjectLogicImpl implements ProjectLogic {

    private static final Logger log = Logger.getLogger(ProjectLogicImpl.class);

    /**
     * init - perform any actions required here for when this bean starts up
     */
    public void init() {
        log.info("init");
        user_ta_type = ServerConfigurationService.getString(Constant.SAKAI_PROPERTY_NAME_TA_NAME_OF_USER_TYPE, "");
    }

    //for NSSakura
    private SakaiProxy sakaiProxy;
    private NssakuraSection nssakuraSection;
    private String user_ta_type="default";

    /**
     * return sections
     * @return
     */
    public List<SectionModel> getSectionModels(){
        return nssakuraSection.getSectionModels();
    }

    /**
     * サイトに登録されていないセクション名リストを取得する
     * セクション
     * @return
     */
    public List<String> getUnregisteredSectionNames(){
        List<CourseSection> sections = nssakuraSection.getSections();
        List<String> sectionNames = nssakuraSection.getSectionNamesFromStudentsWithinLevel();
        TreeMap<String,String> hash = new TreeMap<String,String>();
        for ( CourseSection section : sections){
            hash.put(section.getTitle(), section.getTitle());
        }
        List<String> resultList = new ArrayList<String>();
        for ( String sectionName : sectionNames){
            String sectionName_ = hash.get(sectionName);
            if(sectionName_ == null){
                resultList.add(sectionName);
            }
        }
        return resultList;
    }

    /**
     * get Tas SelectItem.
     * @param sectionId
     * @return
     */
    public Map<String,List<SelectItem>> getSectionTasForRegister(String sectionId){
        String siteId = sakaiProxy.getCurrentSiteId();
        CourseSection section = sakaiProxy.getSection(sectionId);
        List<ParticipationRecord> selectedManagers = sakaiProxy.getSectionTas(sectionId);
        Collections.sort(selectedManagers, sortNameComparator);
        List<ParticipationRecord> availableManagers = sakaiProxy.getSiteTeachingAssistants(siteId);
        Collections.sort(availableManagers, sortNameComparator);
        List<UserModel> tas= getTaUserModels();

        Set<String> selectedUserUuids = new HashSet<String>();
        for(Iterator<ParticipationRecord> iter = selectedManagers.iterator(); iter.hasNext();) {
            ParticipationRecord manager = iter.next();
            selectedUserUuids.add(manager.getUser().getUserUid());
        }

        List<SelectItem>selectedUsers = new ArrayList<SelectItem>();
        for(Iterator iter =selectedManagers.iterator(); iter.hasNext();) {
            ParticipationRecord record = (ParticipationRecord)iter.next();
            SelectItem item = new SelectItem(record.getUser().getUserUid(),
                    record.getUser().getSortName());
            selectedUsers.add(item);
        }

        List<SelectItem>availableUsers = new ArrayList<SelectItem>();
        HashMap<String,String>au_map = new HashMap<String, String>();
        for(Iterator iter = availableManagers.iterator(); iter.hasNext();) {
            User manager = ((ParticipationRecord)iter.next()).getUser();
            if( ! selectedUserUuids.contains(manager.getUserUid())) {
                availableUsers.add(new SelectItem(manager.getUserUid(), manager.getSortName()));
                au_map.put(manager.getUserUid(), manager.getSortName());
            }
        }

        // サイトメンバーではないTAユーザをリストに追加する
        if( tas != null){
            for(Iterator iter = tas.iterator(); iter.hasNext();){
                UserModel model = (UserModel)iter.next();
                String uid = model.getUser().getId();
                if(au_map.get(uid) == null && (! selectedUserUuids.contains(uid)) && (! sakaiProxy.isSiteMember(uid))){
                    availableUsers.add(new SelectItem(uid,model.getUser().getSortName() + Constant.CHAR_UNREGISTERED_MEMBER));
                }
            }
        }
        Map<String,List<SelectItem>> map = new HashMap<String,List<SelectItem>>();
        map.put("selectedUsers", selectedUsers);
        map.put("availableUsers", availableUsers);
        return map;
    }

    /**
     * CSVファイルを読み込んでUserリストを返す
     * @param fileData
     * @param encoding
     * @return UserCsvModel
     */
    public UserCsvResultModel getImportUsers(byte[] fileData, String encoding){
        InputStream inputStream = new ByteArrayInputStream(fileData);
        UserCsvResultModel result = new UserCsvResultModel();
        int num=0;
        try {
            InputStreamReader inReader = new InputStreamReader(inputStream, encoding);
            CSVReader reader = new CSVReader(inReader);
            List <String[]> lines = reader.readAll();
            if(lines == null){
                result.setErrorCode(UserCsvResultModel.FILE_INVALID);
                return result;
            }
            String[] headers = lines.get(0);
            if(! checkHeader(headers)){
                result.setErrorCode(UserCsvResultModel.INVALID_HEADER);
                return result;
            }
            for(int i=1; i<lines.size(); i++){
                UserCsvModel model = new UserCsvModel();
                String[] values = lines.get(i);
                if(values == null || values.length<1 || values[0].isEmpty()){
                    continue;
                }
                for(int n=0; n < headers.length; n++){
                    try{
                        model.setValue(UserCsvResultModel.getReplaceHeader(headers[n]), values[n]);
                    }catch(Exception e){
                        break;
                    }
                }
                result.addUserCsvModel(model);
                num++;
            }
            inReader.close();
        } catch (UnsupportedEncodingException e) {
            log.debug(e.getMessage());
            result.setErrorCode(UserCsvResultModel.INVALID_CHARCODE);
        } catch (IOException e) {
            log.debug(e.getMessage());
            result.setErrorCode(UserCsvResultModel.FILE_INVALID);
        }
        if( num < 1){
            result.setErrorCode(UserCsvResultModel.NO_DATA);
        }
        return result;
    }

    /**
     *
     * @param model
     */
    public void registerImportUsers(UserCsvResultModel model, boolean override){
        registerImportUsers(model, override, sakaiProxy.getCurrentSiteId());
    }

    public void registerImportUsers(UserCsvResultModel model, boolean override, String siteId){
        List<UserCsvModel> userModelList = model.getUserCsvList();

        for(UserCsvModel userModel:userModelList){
            String password = userModel.getPassword();
            if(password == null || password.isEmpty()){
                password = userModel.getEid();
            }
            boolean existFlg = false;
            if(sakaiProxy.getUserByEid(userModel.getEid())!=null){
                existFlg = true;
            }
            UserEdit user = null;
            String userId = null;
            if( !existFlg || override ){
                user = sakaiProxy.createOrUpdateUserAfterAuthentication(
                        userModel.getEid(), userModel.getLast_name(), userModel.getFirst_name(),
                        userModel.getEmail(), password, userModel.getType(), changeProperties(userModel.getProperties()));
                if( user == null){
                    model.getErrorUser().add(userModel.getEid());
                    continue;
                }else if( existFlg ){
                    model.getAlterUser().add(userModel.getEid());
                }else{
                    model.getAddUser().add(userModel.getEid());
                }
                userId = user.getId();
            }else{
                userId = sakaiProxy.getUserByEid(userModel.getEid()).getId();
            }
            // メンバの登録
            if( userId != null && sakaiProxy.addSiteMember(userId, Role.STUDENT, siteId)){
                model.getAddMember().add(userModel.getEid());
            }
            // ×組織グループ作成（TA登録）
            // ×組織グループに参加
        }
    }

    private ResourceProperties changeProperties(Map<String, String> map){
        ResourceProperties properties = new BaseResourceProperties();
        Iterator it = map.keySet().iterator();
        while(it.hasNext()){
            String key = (String)it.next();
            String val = map.get(key);
            if(val != null || val.length()>0){
                properties.addProperty(key, val);
            }
        }
        return properties;
    }

    /**
     * 必須のヘッダーが含まれているかをチェックする
     * @param headers
     * @return
     */
    private boolean checkHeader(String[] headers){
        Map<String, String>map = new HashMap<String, String>();
        for(String header:headers){
            map.put(header, header);
        }
        String[] required = UserCsvResultModel.requiredHeaders;
        for(String header:required){
            String val = map.get(header);
            if( val == null){
                return false;
            }
        }
        return true;
    }

    /**
     * TypeがTAでSakaiに登録されているユーザ一覧を取得
     * @return
     */
    private List<UserModel> getTaUserModels(){
        if(user_ta_type == null || user_ta_type.length()<1){
            return null;
        }
        List tas = sakaiProxy.getUserListByType(user_ta_type);
        List<UserModel> users = new ArrayList<UserModel>();
        for(Object ta : tas){
            UserModel model = new UserModel();
            model.setUser(ta);
            users.add(model);
        }
        return users;
    }

    /**
     * get Enrollment Users in section
     * @param sectionId
     * @return
     */
    public List<UserModel> getSectionEnrollmentUsers(String sectionId){
        List<EnrollmentRecord> enrollments = sakaiProxy.getSectionEnrollment(sectionId);
        List<UserModel> users = new ArrayList<UserModel>();
        for ( EnrollmentRecord enrollment : enrollments){
            String userId = enrollment.getUser().getUserUid();
            UserModel model = new UserModel();
            model.setUser(sakaiProxy.getUser(userId));
            users.add(model);
        }
        return users;
    }

    /**
     * set TA to Section
     * 下位レベルのセクションにもTAとして登録する
     * @param selectedUsers
     * @param sectionId
     * @return resultList [add,del,undel]
     */
    public List<String>[] setSectionTas(String[] selectedUsers, String sectionId){
        CourseSection section = sakaiProxy.getSection(sectionId);
        List<ParticipationRecord> tas = sakaiProxy.getSectionTas(sectionId);
        List<String> addUsers = new ArrayList<String>();
        List<String> delUsers = new ArrayList<String>();
        List<String> undelUsers = new ArrayList<String>();
        Set<String> userMap = toHashFromArray(selectedUsers);
        HashMap<String, ParticipationRecord> tasMap= new HashMap<String, ParticipationRecord>();
        CourseSection upSection = nssakuraSection.getUplevelCourseSection(section.getTitle());
        List<ParticipationRecord> upTas = null;
        if( upSection != null){
            upTas = sakaiProxy.getSectionTas(upSection.getUuid());
        }
        //割り当て解除TA
        for(ParticipationRecord ta_:tas){
            String uid = ta_.getUser().getUserUid();
            tasMap.put(uid, ta_);
            if(! userMap.contains(uid)){
                if( ! isMember(uid, upTas)){
                    delUsers.add(uid);
                }else{
                    undelUsers.add(uid);
                }
            }
        }
        //割り当て追加TA
        for(String uid: selectedUsers){
            if(! tasMap.containsKey(uid)){
                addUsers.add(uid);
            }
        }
        Map<String, CourseSection> sectionsMap = nssakuraSection.getLowlevelGroups(section.getTitle());
        List<CourseSection> allSections = new ArrayList<CourseSection>(sectionsMap.values());
        allSections.add(section);
        String[] ids = (String[])delUsers.toArray(new String[0]);
        boolean changeFlg = false;
        for(CourseSection sec: allSections){
            if( sec != null ){
                //TAを割り当て
                for(String userId: addUsers){
                    try {
                        sakaiProxy.addSectionMember(userId, Role.TA, sec.getUuid());
                        changeFlg = true;
                    } catch (Exception e) {
                        log.debug(e.getMessage());
                    }
                }
                //TAの割り当て解除
                if( ids != null && ids.length > 0){
                    try{
                        sakaiProxy.removeSectionMembers(ids, sec.getUuid());
                        changeFlg = true;
                    }catch(Exception e){
                        log.debug(e.getMessage());
                    }
                }
            }
        }
        List[] result = new List[3];
        result[0] = addUsers;
        result[1] = delUsers;
        result[2] = undelUsers;
        return  result;
    }

    private HashSet<String> toHashFromArray(String[] datas){
        HashSet<String> map = new HashSet<String>();
        for(String data: datas){
            map.add(data);
        }
        return map;
    }

    private boolean isMember(String userId, List<ParticipationRecord> taList){
        if( taList == null || taList.isEmpty()){
            return false;
        }
        for(ParticipationRecord ta: taList){
            if(userId.equals(ta.getUser().getUserUid())){
                return true;
            }
        }
        return false;
    }

    /**
     * set Enrollment to section(hierarchy)
     * 上位グループにも登録
     * @param userEid
     * @param sectionId
     * @throws UnmachedRoleException
     * @throws SiteSaveException
     * @throws RoleConfigurationException
     * @throws DuplicateGroupMemberException
     * @throws NoRegisteredUserException
     */
    public void addSectionEnrollmentUser(String userEid, String sectionId)
        throws UnmachedRoleException, SiteSaveException, RoleConfigurationException, DuplicateGroupMemberException, NoRegisteredUserException{
        String userId = null;
        try{
            userId = sakaiProxy.getUserByEid(userEid).getId();
        }catch(Exception e){
            throw new NoRegisteredUserException(e);
        }
        try {
            CourseSection section = sakaiProxy.getSection(sectionId);
            Map<String, CourseSection> sectionsMap = nssakuraSection.getHighlevelGroups(section.getTitle());
            List<CourseSection> sections = new ArrayList<CourseSection>(sectionsMap.values());
            sections.add(section);
            sakaiProxy.addSectionMember(userId, Role.STUDENT, sections);
        } catch (UnmachedRoleException e) {
            throw e;
        } catch (RoleConfigurationException e) {
            throw e;
        } catch (IdUnusedException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } catch (PermissionException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }

    /**
     * remove Enrollment from section (hierarchy)
     * 下位グループから削除
     * @param userIds
     * @param sectionId
     */
    public void removeSecitonEnrollmentUser(String userIds[], String sectionId){
        CourseSection section= sakaiProxy.getSection(sectionId);
        Map<String, CourseSection> sectionsMap = nssakuraSection.getLowlevelGroups(section.getTitle());
        List<CourseSection> sections = new ArrayList<CourseSection>(sectionsMap.values());
        sections.add(section);
        for(CourseSection sec : sections){
            sakaiProxy.removeSectionMembers(userIds, sec.getUuid());
        }
    }

    /**
     * get UserModel list from userId
     * @param userIds
     * @return
     */
    public List<UserModel> getUserModelListByUserId(String[] userIds){
        List<UserModel> resultList = new ArrayList<UserModel>();
        for(String userId : userIds ){
            UserModel model = new UserModel(sakaiProxy.getUser(userId));
            resultList.add(model);
        }
        return resultList;
    }

    /**
     * Compares ParticipationRecords by users' sortNames.
     */
    static Comparator sortNameComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            ParticipationRecord manager1 = (ParticipationRecord)o1;
            ParticipationRecord manager2 = (ParticipationRecord)o2;
            return manager1.getUser().getSortName().compareTo(manager2.getUser().getSortName());
        }
    };

    //end
}
