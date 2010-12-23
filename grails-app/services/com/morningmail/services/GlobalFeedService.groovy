package com.morningmail.services

import com.morningmail.domain.*
import org.springframework.beans.factory.InitializingBean
import com.google.appengine.api.datastore.KeyFactory;

class GlobalFeedService implements FeedService, InitializingBean {
	public static final String ID_YAHOO_NEWS = "yahoo_news"
	public static final String ID_DICTIONARY_DOT_COM_WOTD = "dictionary_dot_com_wotd"
	public static final String ID_TECHCRUNCH = "techcrunch"
	public static final String ID_AVC = "avc"
	public static final String ID_WSJ_HOME_US = "wsj_us_home"
	public static final String ID_BLOG_STEVE_BLANK = "blog_steve_blank"
	public static final String ID_BLOG_ERIC_RIES = "blog_eric_ries"
	
	void afterPropertiesSet() {
	}
	
	FeedService yahooNewsFeedService
	FeedService genericRssFeedService
	FeedService romeFeedService
	
	public void fetch(Feed feed) {
		findService(feed).fetch(feed)
	}
	
	public String getHtml(Feed feed, String title) {
		return findService(feed).getHtml(feed, title)
	}
	
	public String getPlainText(Feed feed, String title) {
		return findService(feed).getPlainText(feed, title)
	}
	
	private FeedService findService(Feed feed) {
		if (feed.systemName.equals(ID_YAHOO_NEWS))
			return yahooNewsFeedService
		else if (feed.type.equals(Feed.TYPE_GENERIC_RSS)) 
			return romeFeedService
	}
		
	public String getHtml(Interest interest) {
		
	}
	
	public String getPlainText(Interest interest) {
		
	}
}
