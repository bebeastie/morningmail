import com.morningmail.services.GlobalFeedService;

import com.morningmail.domain.*;
import com.google.appengine.api.datastore.Key;
import com.morningmail.services.*;
import grails.util.Environment;

class BootStrap {
	def messageSource
	
    def init = { servletContext ->
		
		
		
		if(Environment.getCurrent() == Environment.PRODUCTION){
			messageSource.basenames = ['WEB-INF/grails-app/i18n/messages']
			messageSource.clearCache()
		}
		
		//START FEED CONFIG 
		
		//Yahoo News
		if (!Feed.findBySystemName(GlobalFeedService.SN_YAHOO_NEWS)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.SN_YAHOO_NEWS
			feed.type = Feed.TYPE_SPECIFIC
			feed.url = "http://rss.news.yahoo.com/rss/topstories"
			feed.title = "News"
			feed.save()
		}
		
		//Dictionary.com WOTD
		if (!Feed.findBySystemName(GlobalFeedService.SN_DICTIONARY_DOT_COM_WOTD)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.SN_DICTIONARY_DOT_COM_WOTD
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://dictionary.reference.com/wordoftheday/wotd.rss"
			feed.title = "Word of the Day"
			feed.save()
		}
		
		//TechCrunch
		if (!Feed.findBySystemName(GlobalFeedService.SN_TECHCRUNCH)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.SN_TECHCRUNCH
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://feeds.feedburner.com/TechCrunch"
			feed.title = "TechCrunch"
			feed.save()
		}

		//A VC
		if (!Feed.findBySystemName(GlobalFeedService.SN_AVC)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.SN_AVC
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://feeds.feedburner.com/avc"
			feed.title = "A VC"
			feed.save()
		}
		
		//Wall Street Journal US Home
		if (!Feed.findBySystemName(GlobalFeedService.SN_WSJ_HOME_US)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.SN_WSJ_HOME_US
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://online.wsj.com/xml/rss/3_7011.xml"
			feed.title = "WSJ: U.S. Homepage"
			feed.save()
		}
		
		//Blog: Steve Blank
		if (!Feed.findBySystemName(GlobalFeedService.SN_BLOG_STEVE_BLANK)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.SN_BLOG_STEVE_BLANK
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://steveblank.com/feed/"
			feed.title = "Steve Blank"
			feed.save()
		}
		
		//Blog: Eric Ries
		if (!Feed.findBySystemName(GlobalFeedService.SN_BLOG_ERIC_RIES)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.SN_BLOG_ERIC_RIES
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://feeds.feedburner.com/startup/lessons/learned"
			feed.title = "Eric Ries"
			feed.save()
		}
		
		//Blog: 500 Hats
		if (!Feed.findBySystemName(GlobalFeedService.SN_BLOG_500_HATS)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.SN_BLOG_500_HATS
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://feeds.feedburner.com/typepad/500hats"
			feed.title = "Master of 500 Hats"
			feed.save()
		}
		
		//Blog: Both sides of the table
		if (!Feed.findBySystemName(GlobalFeedService.SN_BLOG_BOTH_SIDES_TABLE)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.SN_BLOG_BOTH_SIDES_TABLE
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://feeds.feedburner.com/BothSidesOfTheTable"
			feed.title = "Both Sides of the Table"
			feed.save()
		}
		
		//Blog: Sean Ellis
		if (!Feed.findBySystemName(GlobalFeedService.SN_BLOG_SEAN_ELLIS)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.SN_BLOG_SEAN_ELLIS
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://feeds2.feedburner.com/Startup-Marketing/"
			feed.title = "Startup Marketing - Sean Ellis"
			feed.save()
		}
		
		//Blog: SK Murphy
		if (!Feed.findBySystemName(GlobalFeedService.SN_BLOG_SK_MURPHY)) {
			Feed feed = new Feed()
			feed.systemName = GlobalFeedService.SN_BLOG_SK_MURPHY
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://feeds.feedburner.com/skmurphy/feed"
			feed.title = "SK Murphy"
			feed.save()
		}

		
		//END FEED CONFIG
		
		
		//START INTEREST CONFIG
		if (!Interest.findBySystemName(Interest.SN_TOP_NEWS)) {
			Interest interest = new Interest()
			interest.systemName = Interest.SN_TOP_NEWS
			interest.displayName = "Top News"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.SN_YAHOO_NEWS).id
			interest.maxStories = 4
			interest.maxWordsPerStory = 40
			interest.includeItemMoreLink = false
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.SN_WEATHER)) {
			Interest interest = new Interest()
			interest.systemName = Interest.SN_WEATHER
			interest.displayName = "Weather"
			interest.feedStyle = Interest.FEED_STYLE_PERSONAL
			interest.personalFeedId = PersonalFeed.TYPE_WEATHER
			interest.save()
		}

		if (!Interest.findBySystemName(Interest.SN_GOOGLE_CAL)) {
			Interest interest = new Interest()
			interest.systemName = Interest.SN_GOOGLE_CAL
			interest.displayName = "Google Calendar"
			interest.feedStyle = Interest.FEED_STYLE_PERSONAL
			interest.personalFeedId = PersonalFeed.TYPE_GOOGLE_CAL
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.SN_WOTD)) {
			Interest interest = new Interest()
			interest.systemName = Interest.SN_WOTD
			interest.displayName = "Word of the Day"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.SN_DICTIONARY_DOT_COM_WOTD).id
			interest.maxStories = 1
			interest.maxWordsPerStory = Interest.NO_MAX
			interest.includeItemMoreLink = false
			interest.includeItemTitle = false
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.SN_TECHCRUNCH)) {
			Interest interest = new Interest()
			interest.systemName = Interest.SN_TECHCRUNCH
			interest.displayName = "TechCrunch"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.SN_TECHCRUNCH).id
			interest.maxStories = 5
			interest.maxWordsPerStory = 40
			interest.includeItemMoreLink = false
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.SN_AVC)) {
			Interest interest = new Interest()
			interest.systemName = Interest.SN_AVC
			interest.displayName = "A VC"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.SN_AVC).id
			interest.maxStories = 5
			interest.maxWordsPerStory = 40
			interest.includeItemMoreLink = false
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.SN_WSJ_US_HOME)) {
			Interest interest = new Interest()
			interest.systemName = Interest.SN_WSJ_US_HOME
			interest.displayName = "WSJ: U.S. Homepage"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.SN_WSJ_HOME_US).id
			interest.maxStories = 4
			interest.maxWordsPerStory = 40
			interest.includeItemMoreLink = false
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.SN_BLOG_STEVE_BLANK)) {
			Interest interest = new Interest()
			interest.systemName = Interest.SN_BLOG_STEVE_BLANK
			interest.displayName = "Steve Blank"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.SN_BLOG_STEVE_BLANK).id
			interest.maxStories = 5
			interest.maxWordsPerStory = 40
			interest.includeItemMoreLink = false
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.SN_BLOG_ERIC_RIES)) {
			Interest interest = new Interest()
			interest.systemName = Interest.SN_BLOG_ERIC_RIES
			interest.displayName = "Eric Ries"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.SN_BLOG_ERIC_RIES).id
			interest.maxStories = 5
			interest.maxWordsPerStory = 40
			interest.includeItemMoreLink = false
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.SN_BLOG_500_HATS)) {
			Interest interest = new Interest()
			interest.systemName = Interest.SN_BLOG_500_HATS
			interest.displayName = "Master of 500 Hats"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.SN_BLOG_500_HATS).id
			interest.maxStories = 5
			interest.maxWordsPerStory = 40
			interest.includeItemMoreLink = false
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.SN_BLOG_BOTH_SIDES_TABLE)) {
			Interest interest = new Interest()
			interest.systemName = Interest.SN_BLOG_BOTH_SIDES_TABLE
			interest.displayName = "Both Sides of the Table"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.SN_BLOG_BOTH_SIDES_TABLE).id
			interest.maxStories = 5
			interest.maxWordsPerStory = 40
			interest.includeItemMoreLink = false
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.SN_BLOG_SEAN_ELLIS)) {
			Interest interest = new Interest()
			interest.systemName = Interest.SN_BLOG_SEAN_ELLIS
			interest.displayName = "Startup Marketing - Sean Ellis"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.SN_BLOG_SEAN_ELLIS).id
			interest.maxStories = 5
			interest.maxWordsPerStory = 40
			interest.includeItemMoreLink = false
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.SN_BLOG_SK_MURPHY)) {
			Interest interest = new Interest()
			interest.systemName = Interest.SN_BLOG_SK_MURPHY
			interest.displayName = "SKMurphy"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.globalFeedId = Feed.findBySystemName(GlobalFeedService.SN_BLOG_SK_MURPHY).id
			interest.maxStories = 5
			interest.maxWordsPerStory = 40
			interest.includeItemMoreLink = false
			interest.save()
		}
		
		if (!Interest.findBySystemName(Interest.SN_READ_LATER)) {
			Interest interest = new Interest()
			interest.systemName = Interest.SN_READ_LATER
			interest.displayName = "Read Later Items"
			interest.feedStyle = Interest.FEED_STYLE_PERSONAL
			interest.personalFeedId = PersonalFeed.TYPE_READ_LATER
			interest.save()
		}
		//END INTEREST CONFIG	
    }
    def destroy = {
    }
}
