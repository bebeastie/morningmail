package com.morningmail.domain



import javax.persistence.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text

@Entity
class Interest implements Serializable {
	public static final String TYPE_TOP_NEWS = "top_news"
	public static final String TYPE_GOOGLE_CAL = "google_cal"
	public static final String TYPE_WEATHER = "weather"
	public static final String TYPE_WOTD = "word_of_the_day"
	public static final String TYPE_TECHCRUNCH = "techcrunch"
	public static final String TYPE_AVC = "avc"
	public static final String TYPE_WSJ_US_HOME = "wsj_us_home"
	public static final String TYPE_BLOG_STEVE_BLANK = "blog_steve_blank"
	public static final String TYPE_BLOG_ERIC_RIES = "blog_eric_ries"
	public static final String TYPE_READ_LATER = "read_later"
	
	public static final String FEED_STYLE_GLOBAL = "global"
	public static final String FEED_STYLE_PERSONAL = "personal"
	
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key id

	@Basic
	String type
	
	@Basic
	String displayName
	
	//if a user created interest then this will be set
	@Basic
	User owner
	
	@Basic
	String feedStyle
			
	@Basic
	String feedId
	
    static constraints = {
    	id visible:false
	}
}
