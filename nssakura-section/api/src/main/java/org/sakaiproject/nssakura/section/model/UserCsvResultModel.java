package org.sakaiproject.nssakura.section.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.Data;
import java.util.HashMap;

import org.sakaiproject.user.api.User;

@Data
public class UserCsvResultModel  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int SUCCESS=-1;
	public static final int FILE_INVALID=0;
	public static final int INVALID_CHARCODE=1;
	public static final int INVALID_HEADER=2;
	public static final int NO_DATA=3;
	public static final String[] requiredHeaders = {"user id", "first name", "last name", "email", "password","type"};
	public static final Map<String, String> replaceHeaderMap =
		new HashMap<String, String>() {{
			put("user id", "eid");
			put("first name", "first_name");
			put("last name", "last_name");
		}};
	public static String[] errorMsgKey={"error_file_invalid", "error_invalid_charcode", "error_invalid_header","error_no_data"};
	public static String getReplaceHeader(String org){
		String rep = replaceHeaderMap.get(org);
		return rep==null ? org : rep;
	}
	
	private List<UserCsvModel> userCsvList=new ArrayList<UserCsvModel>();
	private List<String> addUser = new ArrayList<String>();
	private List<String> alterUser = new ArrayList<String>();
	private List<String> addMember = new ArrayList<String>();
	private List<String> errorUser=new ArrayList<String>();

	private int errorCode=SUCCESS;
	//画面を複数開く等への対策
	private long checkCode;
	
	public UserCsvResultModel(){
		Random r = new Random();
		checkCode = System.currentTimeMillis() + r.nextLong();
	}

	public boolean isError(){
		return errorCode != SUCCESS;
	}
	
	public String getErrorMsgKey(){
		try{
			String key = errorMsgKey[errorCode];
			return key;
		}catch(Exception e){}
		return "";
	}
	
	public void addUserCsvModel(UserCsvModel model){
		userCsvList.add(model);
	}
	
	public boolean isNotSet(){
		if(userCsvList == null || userCsvList.isEmpty()){
			return true;
		}
		return false;
	}

	public boolean isSet(){
		if(userCsvList != null && userCsvList.size()>0){
			return true;
		}
		return false;
	}
	
	public boolean isValidate(String checkCodeStr){
		try{
			long value = Long.parseLong(checkCodeStr);
		return value == this.checkCode;
		}catch(Exception e){}
		return false;
	}
	
	public String getErrorUsersStr(){
		if(errorUser == null || errorUser.isEmpty()){
			return null;
		}
		String result = errorUser.get(0);
		for(int i=1; i<errorUser.size(); i++){
			result += "," + errorUser.get(i);
		}
		return result;
	}
}
