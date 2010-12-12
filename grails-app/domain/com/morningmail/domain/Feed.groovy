package com.morningmail.domain

import javax.persistence.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text

@Entity
class Feed implements Serializable {

	public static final Integer NO_MAX = -1
	
	public static final String TYPE_SPECIFIC = "specific"
	public static final String TYPE_GENERIC_RSS = "generic_rss"
	
	@Id
	String id
	
	@Basic
	String type
	
	@Basic
	String title
	
	@Basic 
	Integer maxStories
	
	@Basic
	Integer maxWordsPerStory
	
	@Basic
	String url
	
	@Basic
	Text html
	
	@Basic
	Text plainText
	
	@Basic
	Date lastUpdated
		
    static constraints = {
    	id(visible:false)
	}
}
