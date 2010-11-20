package com.morningmail.domain



import javax.persistence.*;
import com.google.appengine.api.datastore.Key;

@Entity
class User implements Serializable {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key id

	@Basic 
	String email
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<OAuthToken> tokens = new ArrayList<OAuthToken>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<PersonalFeed> pFeeds = new ArrayList<PersonalFeed>();
	
    static constraints = {
    	id(visible:false)
		email(nullable: false, blank:false)	
	}
}
