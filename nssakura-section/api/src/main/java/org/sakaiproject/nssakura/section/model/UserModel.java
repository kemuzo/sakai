package org.sakaiproject.nssakura.section.model;

import org.sakaiproject.user.api.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

	private User user;
	
	public void setUser(Object obj){
		this.user = (User)obj;
	}
	
	public String getChokugai(){
		String val = user.getProperties().getProperty("chokugai");
		return getDisplayVal(val);
	}
	
	public String getTypeofemproyment(){
		String val = user.getProperties().getProperty("type-of-employment");
		return getDisplayVal(val);
	}

	public String getBelongcompanyname(){
		String val = user.getProperties().getProperty("company");
		return getDisplayVal(val);
	}

		
	private String getDisplayVal(String val){
		if (val == null){
			return "";
		}
		return val;
	}
}
