package com.morningmail.services;

import com.morningmail.domain.*;

public interface FeedService {
	
	public static interface FeedServiceHelper {
		public String getHtml();
		
		public String getPlainText();
 	}
	
	public void fetch(Feed feed); 
	
	public FeedServiceHelper process(Feed feed, Interest interest, String emailId);
	
}

