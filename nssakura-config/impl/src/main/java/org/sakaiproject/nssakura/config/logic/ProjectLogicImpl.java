package org.sakaiproject.nssakura.config.logic;

import java.util.List;

import lombok.Setter;

import org.apache.log4j.Logger;
import org.sakaiproject.nssakura.config.dao.NSConfigDao;
import org.sakaiproject.nssakura.config.model.NSConfig;

/**
 * Implementation of {@link ProjectLogic}
 */
public class ProjectLogicImpl implements ProjectLogic {

	private static final Logger log = Logger.getLogger(ProjectLogicImpl.class);
	
	@Setter
	private NSConfigDao nsConfigDao;
	
	/**
	 * init - perform any actions required here for when this bean starts up
	 */
	public void init() {
		log.info("init");
	}

	@Override
	public List<NSConfig> getAllConfigurations() {
		return nsConfigDao.findAll();
	}
	
	@Override
	public void saveConfig(NSConfig config) {
		nsConfigDao.save(config);
	}

	@Override
	public void updateConfig(NSConfig config) {
		nsConfigDao.update(config);
	}

	@Override
	public void deleteConfig(NSConfig config) {
		nsConfigDao.delete(config);
	}

	@Override
	public NSConfig getConfigByName(String name) {
		return nsConfigDao.findById(name);
	}

}
