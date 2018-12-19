package org.sakaiproject.nssakura.config.api.impl;

import lombok.Setter;

import org.sakaiproject.nssakura.config.api.NSConfigurationService;
import org.sakaiproject.nssakura.config.dao.NSConfigDao;
import org.sakaiproject.nssakura.config.model.NSConfig;

public class NSConfigurationServiceImpl implements NSConfigurationService {
	
	@Setter
	private NSConfigDao nsConfigDao;
	
	@Override
	public String getString(String name) {
		return getValue(name);
	}

	@Override
	public String getStringOrDefault(String name, String defaultValue) {
		String value = getValue(name);
		return value == null ? defaultValue : value;
	}

	@Override
	public Boolean getBoolean(String name) {
		String value = getValue(name);
		if (value == null) {
			return null;
		}
		if (value.equalsIgnoreCase("true")) {
			return true;
		} else if (value.equalsIgnoreCase("false")) {
			return false;
		} else {
			return null;
		}
	}

	@Override
	public boolean getBooleanOrDefault(String name, boolean defaultValue) {
		String value = getValue(name);
		if (value == null) {
			return defaultValue;
		}
		if (value.equalsIgnoreCase("true")) {
			return true;
		} else if (value.equalsIgnoreCase("false")) {
			return false;
		} else {
			return defaultValue;
		}
	}
	
	private String getValue(String name) {
		NSConfig config = nsConfigDao.findById(name);
		if (config == null || config.getValue() == null) {
			return null;
		}
		return config.getValue();
	}
}
