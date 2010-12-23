package com.morningmail.domain



import javax.persistence.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text

@Entity
class Interest implements Serializable {
	public static final Integer NO_MAX = -1
	
	public static final String SN_TOP_NEWS = "top_news"
	public static final String SN_GOOGLE_CAL = "google_cal"
	public static final String SN_WEATHER = "weather"
	public static final String SN_WOTD = "word_of_the_day"
	public static final String SN_TECHCRUNCH = "techcrunch"
	public static final String SN_AVC = "avc"
	public static final String SN_WSJ_US_HOME = "wsj_us_home"
	public static final String SN_BLOG_STEVE_BLANK = "blog_steve_blank"
	public static final String SN_BLOG_ERIC_RIES = "blog_eric_ries"
	public static final String SN_READ_LATER = "read_later"
	
	public static final String FEED_STYLE_GLOBAL = "global"
	public static final String FEED_STYLE_PERSONAL = "personal"
	
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key id

	@Basic
	String systemName
	
	@Basic
	String displayName

	@Basic
   	Integer maxStories
   
   	@Basic
   	Integer maxWordsPerStory
	   
	@Basic
	Boolean includeItemTitle = true

	@Basic
   	Boolean includeItemMoreLink
   
	//if a user created interest then this will be set
	@Basic
	Key owner
	
	@Basic
	String feedStyle
			
	@Basic
	Key globalFeedId
	
	@Basic
	String personalFeedId
	
    static constraints = {
    	id visible:false
	}
}
