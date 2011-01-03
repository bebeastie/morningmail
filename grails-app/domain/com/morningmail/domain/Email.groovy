package com.morningmail.domain

import javax.persistence.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text

@Entity
class Email implements Serializable {
    public static final String STATUS_PENDING = "pending"
	public static final String STATUS_QUEUED = "queued"
	public static final String STATUS_SENT = "sent"
	
	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key id

	@ManyToOne(fetch = FetchType.LAZY)
	User user;
	
	@Basic
	Text plainText
	
	@Basic
	Text html

	@Basic
	Date desiredDeliveryDate	
	
	@Basic
	String status
	
	@Basic
	Text deliveryNotes
	
	@Basic
	Date deliveryDate
		
	@Basic
	Date lastUpdated
		
    static constraints = {
    	id visible:false
	}
}
