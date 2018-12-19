package org.sakaiproject.nssakura.config.logic;

import java.util.List;

import org.sakaiproject.nssakura.config.model.NSConfig;

public interface ProjectLogic {
	public List<NSConfig> getAllConfigurations();
	public NSConfig getConfigByName(String name);
	public void saveConfig(NSConfig config);
	public void updateConfig(NSConfig config);
	public void deleteConfig(NSConfig config);
}
