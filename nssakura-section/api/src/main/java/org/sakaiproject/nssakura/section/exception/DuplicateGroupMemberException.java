package org.sakaiproject.nssakura.section.exception;

public class DuplicateGroupMemberException extends Exception{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateGroupMemberException(Exception e){
		super(e);
	}

	public DuplicateGroupMemberException(String message){
		super(message);
	}
	
	public DuplicateGroupMemberException(){
		super();
	}
}
