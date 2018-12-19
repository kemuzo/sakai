package org.sakaiproject.nssakura.section.exception;

public class SiteSaveException extends Exception{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SiteSaveException(Exception e){
		super(e);
	}

	public SiteSaveException(String message){
		super(message);
	}
	
	public SiteSaveException(){
		super();
	}
}
