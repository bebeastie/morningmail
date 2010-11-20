package com.morningmail.services;

import com.morningmail.domain.User;

public interface PersonalFeedService {

	public void fetch(User u); 
	
	public String getHtml(User u);
	
	public String getPlainText(User u);
	
}
