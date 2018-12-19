package org.sakaiproject.nssakura.config.dao;

import java.util.List;

import org.sakaiproject.nssakura.config.model.NSConfig;

public interface NSConfigDao {
	public NSConfig findById(String name);
	public List<NSConfig> findAll();
	public void save(NSConfig config);
	public void delete(NSConfig config);
	public void update(NSConfig config);
}
