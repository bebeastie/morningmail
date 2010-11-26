package com.morningmail.services;

public interface FeedService {

	public void fetch(); 
	
	public String getHtml();
	
	public String getPlainText();
	
	public String getShortPlainText();
	
}
