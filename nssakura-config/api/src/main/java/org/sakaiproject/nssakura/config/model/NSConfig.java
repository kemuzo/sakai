package org.sakaiproject.nssakura.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NSConfig {
	private String name;
	private String value;
	private String description;
}
