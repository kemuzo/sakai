package org.sakaiproject.nssakura.section.exception;

public class NoRegisteredUserException extends Exception{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoRegisteredUserException(Exception e){
		super(e);
	}

	public NoRegisteredUserException(String message){
		super(message);
	}
	
	public NoRegisteredUserException(){
		super();
	}
	
}
