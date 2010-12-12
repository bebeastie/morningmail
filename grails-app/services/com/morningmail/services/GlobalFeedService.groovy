package com.morningmail.services

import com.morningmail.domain.*
import org.springframework.beans.factory.InitializingBean

class GlobalFeedService implements FeedService, InitializingBean {
	public static final String ID_YAHOO_NEWS = "yahoo_news"
	public static final String ID_DICTIONARY_DOT_COM_WOTD = "dictionary_dot_com_wotd"
	public static final String ID_TECHCRUNCH = "techcrunch"
		
	void afterPropertiesSet() {
//		TOP_NEWS = Interest.findByType(Interest.TYPE_TOP_NEWS)
//		WEATHER = Interest.findByType(Interest.TYPE_WEATHER)
//		GOOGLE_CAL = Interest.findByType(Interest.TYPE_GOOGLE_CAL)
//		WOTD = Interest.findByType(Interest.TYPE_WOTD)
	}
	
	FeedService yahooNewsFeedService
	FeedService dictionaryWotdService
	FeedService genericRssFeedService
	
	public void fetch(Feed feed) {
		findService(feed).fetch(feed)
	}
	
	public String getHtml(Feed feed) {
		return findService(feed).getHtml(feed)
	}
	
	public String getPlainText(Feed feed) {
		return findService(feed).getPlainText(feed)
	}
	
	private FeedService findService(Feed feed) {
		if (feed.id.equals(ID_YAHOO_NEWS))
			return yahooNewsFeedService
		else if (feed.id.equals(ID_DICTIONARY_DOT_COM_WOTD))
			return dictionaryWotdService
		else if (feed.type.equals(Feed.TYPE_GENERIC_RSS)) 
			return genericRssFeedService
	}
		
	public String getHtml(Interest interest) {
		
	}
	
	public String getPlainText(Interest interest) {
		
	}
}
