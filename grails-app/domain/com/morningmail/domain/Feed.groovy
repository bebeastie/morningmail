package com.morningmail.domain

import javax.persistence.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text

@Entity
class Feed implements Serializable {
	
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
	 * The URL that should be used to request the feed
	 */
	@Basic
	String url
	
	@Basic
	Text raw
		
	@Basic
	Date lastUpdated
		
    static constraints = {
    	id(visible:false)
	}
}
