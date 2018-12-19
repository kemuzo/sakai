package org.sakaiproject.nssakura.section.dao;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.sakaiproject.nssakura.section.model.NSSectionJob;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class NSSectionJobDaoImpl extends HibernateDaoSupport implements NSSectionJobDao {

	public void init() {
	}
	
	/**
	 * 処理中のジョブを返す
	 * @param siteId
	 * @return
	 */
	public List<NSSectionJob> findProcessingBySiteId(final String siteId){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findProcessingBySiteId");
				   query.setParameter("siteId", siteId);
				   
				   List<NSSectionJob> bbsList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (bbsList != null && (! bbsList.isEmpty())){
					   Set<NSSectionJob> bbsSet = new LinkedHashSet<NSSectionJob>(bbsList);
					   bbsList.clear();
					   bbsList.addAll(bbsSet);
				   }
				   return bbsList;
			   }
		   };
		   return (List<NSSectionJob>)getHibernateTemplate().execute(hc);
		
	}

	/**
	 * サイトで実行されたバッチジョブ一覧を返す
	 * @param siteId
	 * @return
	 */
	public List<NSSectionJob> findBySiteId(final String siteId){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findBySiteId");
				   query.setParameter("siteId", siteId);
				   
				   List<NSSectionJob> bbsList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (bbsList != null && (! bbsList.isEmpty())){
					   Set<NSSectionJob> bbsSet = new LinkedHashSet<NSSectionJob>(bbsList);
					   bbsList.clear();
					   bbsList.addAll(bbsSet);
				   }
				   return bbsList;
			   }
		   };
		   return (List<NSSectionJob>)getHibernateTemplate().execute(hc);
		
	}

	/**
	 * サイトで実行されたバッチジョブ一覧を返す
	 * @param siteId
	 * @return
	 */
	public int findNumBySiteId(final String siteId){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findNumBySiteId");
				   query.setParameter("siteId", siteId);
				   
				   return query.uniqueResult();
			   }
		   };
		   return ((Integer)getHibernateTemplate().execute(hc)).intValue();
	}
	
	@Override
	public void save(NSSectionJob job) {
		getHibernateTemplate().save(job);
	}
	
	@Override
	public void update(NSSectionJob job) {
		getHibernateTemplate().update(job);
	}
	
	@Override
	public void delete(NSSectionJob job) {
		getHibernateTemplate().delete(job);
	}
}
