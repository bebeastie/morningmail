package com.morningmail.domain



import javax.persistence.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text

@Entity
class ReadLaterItem implements Serializable {
	
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key id

	@ManyToOne(fetch = FetchType.LAZY)
	User user;
	
	@Basic
	String url
	
	@Basic
	String title
	
	@Basic
	String description
	
	@Basic
	Date savedDate
	
    static constraints = {
    	id(visible:false)
		title(nullable:false, blank:false)
		url(nullable:false, blank:false)
		savedDate(nullable:false, blank:false)
	}
}
