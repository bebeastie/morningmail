import com.morningmail.services.GlobalFeedService;

import com.morningmail.domain.*;
import com.google.appengine.api.datastore.Key;
import com.morningmail.services.*;

class BootStrap {
	

    def init = { servletContext ->
		if (!User.findByEmail("blake.barnes@gmail.com")) {
			//TODO
		}
		
		//START FEED CONFIG
		
		//Yahoo News
		if (!Feed.findBySystemName(GlobalFeedService.ID_YAHOO_NEWS)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.ID_YAHOO_NEWS
			feed.type = Feed.TYPE_SPECIFIC
			feed.url = "http://rss.news.yahoo.com/rss/topstories"
			feed.title = "News"
			feed.maxStories = 4
			feed.maxWordsPerStory = 40
			feed.includeItemMoreLink = false
			feed.save()
		}
		
		//Dictionary.com WOTD
		if (!Feed.findBySystemName(GlobalFeedService.ID_DICTIONARY_DOT_COM_WOTD)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.ID_DICTIONARY_DOT_COM_WOTD
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://dictionary.reference.com/wordoftheday/wotd.rss"
			feed.title = "Word of the Day"
			feed.maxStories = 1
			feed.maxWordsPerStory = Feed.NO_MAX
			feed.includeItemMoreLink = false
			feed.includeItemTitle = false
			feed.save()
		}
		
		//TechCrunch
		if (!Feed.findBySystemName(GlobalFeedService.ID_TECHCRUNCH)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.ID_TECHCRUNCH
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://feeds.feedburner.com/TechCrunch"
			feed.title = "TechCrunch"
			feed.maxStories = 5
			feed.maxWordsPerStory = 40
			feed.includeItemMoreLink = false
			feed.save()
		}

		//A VC
		if (!Feed.findBySystemName(GlobalFeedService.ID_AVC)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.ID_AVC
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://feeds.feedburner.com/avc"
			feed.title = "A VC"
			feed.maxStories = 5
			feed.maxWordsPerStory = 40
			feed.includeItemMoreLink = false
			feed.save()
		}
		
		//Wall Street Journal US Home
		if (!Feed.findBySystemName(GlobalFeedService.ID_WSJ_HOME_US)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.ID_WSJ_HOME_US
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://online.wsj.com/xml/rss/3_7011.xml"
			feed.title = "WSJ: U.S. Homepage"
			feed.maxStories = 4
			feed.maxWordsPerStory = 40
			feed.includeItemMoreLink = false
			feed.save()
		}
		
		//Blog: Steve Blank
		if (!Feed.findBySystemName(GlobalFeedService.ID_BLOG_STEVE_BLANK)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.ID_BLOG_STEVE_BLANK
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://steveblank.com/feed/"
			feed.title = "Steve Blank"
			feed.maxStories = 5
			feed.maxWordsPerStory = 40
			feed.includeItemMoreLink = false
			feed.save()
		}
		
//		//Blog: Eric Ries
//		if (!Feed.findById(GlobalFeedService.ID_BLOG_ERIC_RIES)) {
//			Feed feed = new Feed()
//			feed.id = GlobalFeedService.ID_BLOG_ERIC_RIES
//			feed.type = Feed.TYPE_GENERIC_RSS
//			feed.url = "http://feeds.feedburner.com/startup/lessons/learned"
//			feed.title = "Eric Ries"
//			feed.maxStories = 5
//			feed.maxWordsPerStory = 40
//			feed.includeItemMoreLink = true
//			feed.save()
//		}
		
//		http://feeds.feedburner.com/startup/lessons/learned
		
		//END FEED CONFIG
		
		//START INTEREST CONFIG
		if (!Interest.findBySystemName(Interest.ID_TOP_NEWS)) {
			Interest interest = new Interest()
			interest.systemName = Interest.ID_TOP_NEWS
			interest.displayName = "Top News"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.ID_YAHOO_NEWS).id
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.ID_WEATHER)) {
			Interest interest = new Interest()
			interest.systemName = Interest.ID_WEATHER
			interest.displayName = "Weather"
			interest.feedStyle = Interest.FEED_STYLE_PERSONAL
			interest.personalFeedId = PersonalFeed.TYPE_WEATHER
			interest.save()
		}

		if (!Interest.findBySystemName(Interest.ID_GOOGLE_CAL)) {
			Interest interest = new Interest()
			interest.systemName = Interest.ID_GOOGLE_CAL
			interest.displayName = "Google Calendar"
			interest.feedStyle = Interest.FEED_STYLE_PERSONAL
			interest.personalFeedId = PersonalFeed.TYPE_GOOGLE_CAL
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.ID_WOTD)) {
			Interest interest = new Interest()
			interest.systemName = Interest.ID_WOTD
			interest.displayName = "Word of the Day"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.ID_DICTIONARY_DOT_COM_WOTD).id
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.ID_TECHCRUNCH)) {
			Interest interest = new Interest()
			interest.systemName = Interest.ID_TECHCRUNCH
			interest.displayName = "TechCrunch"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.ID_TECHCRUNCH).id
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.ID_AVC)) {
			Interest interest = new Interest()
			interest.systemName = Interest.ID_AVC
			interest.displayName = "Blog: A VC"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.ID_AVC).id
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.ID_WSJ_US_HOME)) {
			Interest interest = new Interest()
			interest.systemName = Interest.ID_WSJ_US_HOME
			interest.displayName = "WSJ: U.S. Homepage"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.ID_WSJ_HOME_US).id
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.ID_BLOG_STEVE_BLANK)) {
			Interest interest = new Interest()
			interest.systemName = Interest.ID_BLOG_STEVE_BLANK
			interest.displayName = "Blog: Steve Blank"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.ID_BLOG_STEVE_BLANK).id
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.ID_READ_LATER)) {
			Interest interest = new Interest()
			interest.systemName = Interest.ID_READ_LATER
			interest.displayName = "Read Later Items"
			interest.feedStyle = Interest.FEED_STYLE_PERSONAL
			interest.personalFeedId = PersonalFeed.TYPE_READ_LATER
			interest.save()
		}
		
//		if (!Interest.findByType(Interest.TYPE_BLOG_ERIC_RIES)) {
//			Interest interest = new Interest()
//			interest.type = Interest.TYPE_BLOG_ERIC_RIES
//			interest.displayName = "Blog: Eric Ries"
//			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
//			interest.feedId = GlobalFeedService.ID_BLOG_ERIC_RIES
//			interest.save()
//		}
		//END INTEREST CONFIG	
		
    }
    def destroy = {
    }
}
