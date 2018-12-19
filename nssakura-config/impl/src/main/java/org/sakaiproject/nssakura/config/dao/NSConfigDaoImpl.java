package org.sakaiproject.nssakura.config.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.sakaiproject.nssakura.config.model.NSConfig;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class NSConfigDaoImpl extends HibernateDaoSupport implements NSConfigDao {
	private final String QUERY_FIND_BY_NAME = "findByName";
	private final String QUERY_FIND_ALL = "findAll";
	
	public void init() {
		
	}

	@Override
	public NSConfig findById(final String name) {
		HibernateCallback<NSConfig> hcb = new HibernateCallback<NSConfig>() {
			@Override
			public NSConfig doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query q = session.getNamedQuery(QUERY_FIND_BY_NAME);
				q.setParameter("name", name);
				return (NSConfig) q.uniqueResult();
			}
		};
		return getHibernateTemplate().execute(hcb);
	}

	@Override
	public List<NSConfig> findAll() {
		HibernateCallback<List<NSConfig>> hcb = new HibernateCallback<List<NSConfig>>() {
			@Override
			@SuppressWarnings("unchecked")
			public List<NSConfig> doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query q = session.getNamedQuery(QUERY_FIND_ALL);
				return (List<NSConfig>) q.list();
			}
		};
		return getHibernateTemplate().execute(hcb);
	}
	
	@Override
	public void save(NSConfig config) {
		getHibernateTemplate().save(config);
	}
	
	@Override
	public void update(NSConfig config) {
		getHibernateTemplate().update(config);
	}
	
	@Override
	public void delete(NSConfig config) {
		getHibernateTemplate().delete(config);
	}

}
