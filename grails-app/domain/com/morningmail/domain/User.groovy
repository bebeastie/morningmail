package com.morningmail.domain


import java.text.SimpleDateFormat;
import javax.persistence.*;
import com.google.appengine.api.datastore.Key;
import java.util.Set;

@Entity
class User implements Serializable {

	public static final Date DELIVERY_BASE_DATE =  new SimpleDateFormat("MM/dd/yyyy HH:mm:ss Z").parse("11/22/2010 00:00:00 -0500")
	
    @Id
	Key id

	@Basic 
	String email
	
	@Basic
	String password
	
	@Basic
	String name
	
	@Basic
	String zipCode
		
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<OAuthToken> tokens = new ArrayList<OAuthToken>();

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	List<Newsletter> newsletters = new ArrayList<Newsletter>();
	
	@Basic
	List<Key> subscriptions = new ArrayList<Key>();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<PersonalFeed> pFeeds = new ArrayList<PersonalFeed>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<Email> emails = new ArrayList<Email>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
	List<ReadLaterItem> readLaters = new ArrayList<ReadLaterItem>();
	
    static constraints = {
    	id(visible:false)
		name(nullable:false, blank:false)
		password(nullable:false, blank:false)
		zipCode(nullable:false, blank:false, matches:"\\d{5}(-\\d{4})?")
		email(email:true, nullable: false, blank:false)	
	}
}
