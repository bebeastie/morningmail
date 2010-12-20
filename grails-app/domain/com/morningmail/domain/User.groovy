package com.morningmail.domain


import java.text.SimpleDateFormat;
import javax.persistence.*;
import com.google.appengine.api.datastore.Key;
import java.util.Set;

@Entity
class User implements Serializable {

	public static final Date DELIVERY_BASE_DATE =  new SimpleDateFormat("MM/dd/yyyy HH:mm:ss Z").parse("11/22/2010 00:00:00 -0500")
	
    @Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key id

	@Basic 
	String email
	
	@Basic
	String password
	
	@Basic
	String name
	
	@Basic
	String zipCode
	
	/**
	 * The normalized delivery time, stored as time surpassed since the
	 * epoch (Jan 1 1970)
	 */
	@Basic
	Date deliveryTime
	
	@Basic
	String timeZone
	
	/** 
	 * A string representation of the desired delivery time stored as
	 * backup (in case we mess up the normalized time somehow) and
	 * to assist with loading the registration form. 	
	 */
	@Basic
	String localDeliveryTime
	
	/** 
	 * The last time we rendered an email to the database
	 */
	@Basic
	Date lastRenderedDate
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<OAuthToken> tokens = new ArrayList<OAuthToken>();

	@Basic
	List<Key> interests = new ArrayList<Key>();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<PersonalFeed> pFeeds = new ArrayList<PersonalFeed>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<Email> emails = new ArrayList<Email>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
	List<ReadLaterItem> readLaters = new ArrayList<ReadLaterItem>();
	
    static constraints = {
    	id(visible:false)
		email(email:true, nullable: false, blank:false)	
	}
}
