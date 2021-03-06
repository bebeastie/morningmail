package com.morningmail.domain

import javax.persistence.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text

@Entity
class LinkClick implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key id
	
	@Basic
	Key email
	
	@Basic
	Key user
	
	@Basic
	Key feed

	@Basic
	Key interest
	
	@Basic
	String url
		
	@Basic
	Date timeClicked
		
    static constraints = {
    	id(visible:false)
		email(nullable:false)
		user(nullable:false)
		feed(nullable:false)
		interest(nullable:false)
		url(nullable:false)
		timeClicked(nullable:false)
	}
}
