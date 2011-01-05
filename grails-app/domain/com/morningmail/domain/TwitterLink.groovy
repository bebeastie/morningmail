package com.morningmail.domain

import javax.persistence.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text

@Entity
class TwitterLink implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key id
		
	@Basic
	Key userKey
	
	@Basic
	Date firstDate
	
	@Basic
	String firstScreenName
	
	@Basic
	String firstTweet
	
	@Basic
	String url
	
	@Basic 
	String domain
		
	@Basic
	Integer numberTweets = 0
	
	@Basic
	Boolean isArchived = false
	
    static constraints = {
    	id(visible:false)
	}
	
	public void addClick() {
		numberTweets++;
	}
}
