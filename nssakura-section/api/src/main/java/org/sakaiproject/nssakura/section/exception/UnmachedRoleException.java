package org.sakaiproject.nssakura.section.exception;

public class UnmachedRoleException extends Exception{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnmachedRoleException(Exception e){
		super(e);
	}

	public UnmachedRoleException(String message){
		super(message);
	}
	
	public UnmachedRoleException(){
		super();
	}
}
