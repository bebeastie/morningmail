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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key id
	
	@Basic
	String systemName
	
	@Basic
	String type
	
	@Basic
	String title
	
	@Basic
	String description
	
	/*
	 * Max # of stories to list
	 */
	@Basic 
	Integer maxStories
	
	@Basic
	Integer maxWordsPerStory
	
	/*
	 * Set this to add a "More" link after every item. 
	 * Primarily used for items that are summarized. 
	 */
	@Basic 
	Boolean includeItemMoreLink
	
	@Basic
	Boolean includeItemTitle = true

	/*
	 * The URL that should be used to request the feed
	 */
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
