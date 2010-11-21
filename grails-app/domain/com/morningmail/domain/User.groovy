package com.morningmail.domain



import javax.persistence.*;
import com.google.appengine.api.datastore.Key;
import java.util.Set;

@Entity
class User implements Serializable {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key id

	@Basic 
	String email
	
	@Basic
	String zipCode
	
	@Basic
	Date desiredDeliveryTime
	
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
