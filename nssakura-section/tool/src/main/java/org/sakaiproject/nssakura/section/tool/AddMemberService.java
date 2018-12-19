package org.sakaiproject.nssakura.section.tool;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.nssakura.section.dao.NSSectionJobDao;
import org.sakaiproject.nssakura.section.logic.ProjectLogic;
import org.sakaiproject.nssakura.section.model.NSSectionJob;
import org.sakaiproject.nssakura.section.model.UserCsvResultModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AddMemberService {

	@Setter @Getter
	private ProjectLogic projectLogic = null;
	@Setter @Getter
	private SecurityService securityService;
	@Setter @Getter
	private NSSectionJobDao dao;
	
	@Async
	public void registerImportUsersJob(UserCsvResultModel model, boolean override, String siteId, String userId){
		try{
			registerImportUsers(model,override, siteId, userId );
		}catch(Exception e){
			
		}
	}

	public void registerImportUsers(UserCsvResultModel model, boolean override, String siteId, String userId){
		try{
			NSSectionJob jobObj = new NSSectionJob(siteId, userId, model.getUserCsvList().size());
			dao.save(jobObj);
			projectLogic.registerImportUsers(model, override, siteId);
			jobObj.setEndDate(new Date());
			jobObj.setAddMemberNum(model.getAddMember().size());
			jobObj.setAddUserNum(model.getAddUser().size());
			jobObj.setAlterUserNum(model.getAlterUser().size());
			jobObj.setErrorUserNum(model.getErrorUser().size());
			dao.update(jobObj);
		}catch(Exception e){
			
		}
	}

	/**
	 * 処理中のジョブがあれば開始日時を返す
	 * @return
	 */
	public String getProcessing(){
		String siteId = projectLogic.getSakaiProxy().getCurrentSiteId();
		List<NSSectionJob> list = dao.findProcessingBySiteId(siteId);
		if( list == null || list.isEmpty()){
			return null;
		}
		try{
			return list.get(0).getStartDateStr();
		}catch(Exception e){
			
		}
		return null;
	}
	
	public List<NSSectionJob> getJobs(){
		String siteId = projectLogic.getSakaiProxy().getCurrentSiteId();
		return dao.findBySiteId(siteId);
	}
	
	public int getJobsNum(){
		String siteId = projectLogic.getSakaiProxy().getCurrentSiteId();
		return dao.findNumBySiteId(siteId);
	}
}
