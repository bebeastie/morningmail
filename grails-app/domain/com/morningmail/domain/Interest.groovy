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
