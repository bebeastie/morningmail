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
		if (!Feed.findById(GlobalFeedService.ID_YAHOO_NEWS)) {
			Feed feed = new Feed()
			feed.id = GlobalFeedService.ID_YAHOO_NEWS
			feed.type = Feed.TYPE_SPECIFIC
			feed.url = "http://rss.news.yahoo.com/rss/topstories"
			feed.title = "News"
			feed.maxStories = 5
			feed.maxWordsPerStory = Feed.NO_MAX
			feed.save()
		}
		
		//Dictionary.com WOTD
		if (!Feed.findById(GlobalFeedService.ID_DICTIONARY_DOT_COM_WOTD)) {
			Feed feed = new Feed()
			feed.id = GlobalFeedService.ID_DICTIONARY_DOT_COM_WOTD
			feed.type = Feed.TYPE_SPECIFIC
			feed.url = "http://dictionary.reference.com/wordoftheday/wotd.rss"
			feed.title = "Word of the Day"
			feed.maxStories = 1
			feed.maxWordsPerStory = Feed.NO_MAX
			feed.save()
		}
		
		//TechCrunch
		if (!Feed.findById(GlobalFeedService.ID_TECHCRUNCH)) {
			Feed feed = new Feed()
			feed.id = GlobalFeedService.ID_TECHCRUNCH
			feed.type = Feed.TYPE_GENERIC_RSS
			feed.url = "http://feeds.feedburner.com/TechCrunch"
			feed.title = "TechCrunch"
			feed.maxStories = 5
			feed.maxWordsPerStory = 40
			feed.save()
		}
		
		//END FEED CONFIG
		
		//START INTEREST CONFIG
		if (!Interest.findByType(Interest.TYPE_TOP_NEWS)) {
			Interest interest = new Interest()
			interest.type = Interest.TYPE_TOP_NEWS
			interest.displayName = "Top News"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.feedId = GlobalFeedService.ID_YAHOO_NEWS
			interest.save()
		}
		
		if (!Interest.findByType(Interest.TYPE_WEATHER)) {
			Interest interest = new Interest()
			interest.type = Interest.TYPE_WEATHER
			interest.displayName = "Weather"
			interest.feedStyle = Interest.FEED_STYLE_PERSONAL
			interest.feedId = PersonalFeed.TYPE_WEATHER
			interest.save()
		}

		if (!Interest.findByType(Interest.TYPE_GOOGLE_CAL)) {
			Interest interest = new Interest()
			interest.type = Interest.TYPE_GOOGLE_CAL
			interest.displayName = "Google Calendar"
			interest.feedStyle = Interest.FEED_STYLE_PERSONAL
			interest.feedId = PersonalFeed.TYPE_GOOGLE_CAL
			interest.save()
		}
		
		if (!Interest.findByType(Interest.TYPE_WOTD)) {
			Interest interest = new Interest()
			interest.type = Interest.TYPE_WOTD
			interest.displayName = "Word of the Day"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.feedId = GlobalFeedService.ID_DICTIONARY_DOT_COM_WOTD
			interest.save()
		}
		
		if (!Interest.findByType(Interest.TYPE_TECHCRUNCH)) {
			Interest interest = new Interest()
			interest.type = Interest.TYPE_TECHCRUNCH
			interest.displayName = "TechCrunch"
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.feedId = GlobalFeedService.ID_TECHCRUNCH
			interest.save()
		}
		//END INTEREST CONFIG	
		
    }
    def destroy = {
    }
}
