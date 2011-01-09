package com.morningmail.domain


import java.text.SimpleDateFormat;
import javax.persistence.*;
import com.google.appengine.api.datastore.Key;
import java.util.Set;

@Entity
class User implements Serializable {

	public static class TYPE {
		public static final List<String> ALL = [STANDARD,SUBSCRIBE_ONLY]
		public static final String STANDARD = "standard"
		public static final String SUBSCRIBE_ONLY = "subscribe_only"	
	} 
	
    @Id
	Key id

	@Basic 
	String email
	
	@Basic
	String password
	
	@Basic
	String name
	
	@Basic
	String type
	
	@Basic
	String zipCode
	
	@Basic
	Long lastTweetId
		
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<OAuthToken> tokens = new ArrayList<OAuthToken>();

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	List<Newsletter> newsletters = new ArrayList<Newsletter>();
	
	@Basic
	List<Key> subscriptions = new ArrayList<String>();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<PersonalFeed> pFeeds = new ArrayList<PersonalFeed>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<Email> emails = new ArrayList<Email>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
	List<ReadLaterItem> readLaters = new ArrayList<ReadLaterItem>();
	
    static constraints = {
    	id(visible:false)
		name(nullable:true, blank:true)
		password(nullable:true, blank:true)
		type(inList:TYPE.ALL, nullable:false, blank:false)
		zipCode(nullable:true, blank:true, matches:"\\d{5}(-\\d{4})?")
		email(email:true, nullable: false, blank:false)	
	}
}
