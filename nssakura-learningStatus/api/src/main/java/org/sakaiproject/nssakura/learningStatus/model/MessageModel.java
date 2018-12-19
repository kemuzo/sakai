package org.sakaiproject.nssakura.learningStatus.model;

import org.sakaiproject.user.api.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageModel {
	private MessageDaoModel daoModel;
	private User user;
	
	public String getSenderName(){
		return user.getDisplayName()+"(" + user.getDisplayId() + ")";
	}
}
