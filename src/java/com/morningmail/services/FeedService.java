package com.morningmail.services;

import com.morningmail.domain.*;

public interface FeedService {

	public void fetch(Feed feed); 
	
	public String getHtml(Feed feed, String title);
	
	public String getPlainText(Feed feed, String title);
	
}
