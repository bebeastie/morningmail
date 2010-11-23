package com.morningmail.domain


import java.text.SimpleDateFormat;
import javax.persistence.*;
import com.google.appengine.api.datastore.Key;
import java.util.Set;

@Entity
class User implements Serializable {

	public static final Date DELIVERY_BASE_DATE =  new SimpleDateFormat("MM/dd/yyyy HH:mm:ss Z").parse("11/22/2010 00:00:00 -0500")
	
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key id

	@Basic 
	String email
	
	@Basic
	String name
	
	@Basic
	String zipCode
	
	@Basic
	Date deliveryTime
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<OAuthToken> tokens = new ArrayList<OAuthToken>();

	@Basic
	Set<Key> interests;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<PersonalFeed> pFeeds = new ArrayList<PersonalFeed>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<Email> emails = new ArrayList<Email>();
	
    static constraints = {
    	id(visible:false)
		email(email:true, nullable: false, blank:false)	
	}
}
