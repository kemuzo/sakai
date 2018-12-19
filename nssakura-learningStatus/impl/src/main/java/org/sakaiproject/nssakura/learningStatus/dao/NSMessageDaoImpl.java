package org.sakaiproject.nssakura.learningStatus.dao;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.sakaiproject.nssakura.learningStatus.model.MessageDaoModel;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class NSMessageDaoImpl extends HibernateDaoSupport implements NSMessageDao{

	public MessageDaoModel findById(final Long messageId){
		HibernateCallback hc = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Query q = session.getNamedQuery("findById");
				q.setParameter("id", messageId);
				MessageDaoModel messageModel = (MessageDaoModel)q.uniqueResult();
				return messageModel;
			}
		};
		return(MessageDaoModel)getHibernateTemplate().execute(hc);
	}
	
	@Override
	public List<MessageDaoModel> findMessageBySiteIdAndUserId(final String siteId,
			final String userId) {
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findBySiteIdAndUserId");
				   query.setParameter("siteId", siteId);
				   query.setParameter("userId", userId);
				   
				   List<MessageDaoModel> messageList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (messageList != null && (! messageList.isEmpty())){
					   Set<MessageDaoModel> bbsSet = new LinkedHashSet<MessageDaoModel>(messageList);
					   messageList.clear();
					   messageList.addAll(bbsSet);
				   }
				   return messageList;
			   }
		   };
		   return (List<MessageDaoModel>)getHibernateTemplate().execute(hc);
	}

	public List<MessageDaoModel> findMessageBySiteId(final String siteId) {
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException, SQLException {
				   Query query = session.getNamedQuery("findMessagesBySiteId");
				   query.setParameter("siteId", siteId);
				   
				   List<MessageDaoModel> messageList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (messageList != null && (! messageList.isEmpty())){
					   Set<MessageDaoModel> bbsSet = new LinkedHashSet<MessageDaoModel>(messageList);
					   messageList.clear();
					   messageList.addAll(bbsSet);
				   }
				   return messageList;
			   }
		   };
		   return (List<MessageDaoModel>)getHibernateTemplate().execute(hc);
	}

	@Override
	public void save(MessageDaoModel message) {
		getHibernateTemplate().save(message);
	}

	@Override
	public void update(MessageDaoModel message) {
		getHibernateTemplate().update(message);
	}

	@Override
	public void delete(MessageDaoModel message) {
		getHibernateTemplate().delete(message);
	}
	
	public void init() {
	}
}
