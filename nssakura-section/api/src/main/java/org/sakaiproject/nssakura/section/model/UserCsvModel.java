package org.sakaiproject.nssakura.section.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.section.api.facade.Role;

import lombok.Data;

@Data
public class UserCsvModel  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1578937824941010608L;
	private String eid;
	private String first_name;
	private String last_name;
	private String email;
	private String password;
	private String type;
	private Map<String,String> properties = new HashMap<String, String>();
	
	public void setValue(String key, String value){
		if("eid".equals(key)){
			this.eid = value;
		}else if ("first_name".equals(key)){
			this.first_name = value;
		}else if ("last_name".equals(key)){
			this.last_name = value;
		}else if ("email".equals(key)){
			this.email = value;
		}else if ("password".equals(key)){
			this.password = value;
		}else if ("type".equals(key)){
			this.type = value;
		}else{
			this.properties.put(key, value);
		}
	}
	
	public List<String> getPropertiesValue(){
		Iterator it = properties.keySet().iterator();
		List<String> result = new ArrayList<String>();
		while(it.hasNext()){
			String keyValue = (String)it.next();
			keyValue = keyValue + "=" + properties.get(keyValue);
			result.add(keyValue);
		}
		return result;
	}
}
