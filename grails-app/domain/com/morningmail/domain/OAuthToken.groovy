package com.morningmail.domain



import javax.persistence.*;
import com.google.appengine.api.datastore.Key;

@Entity
class OAuthToken implements Serializable {

	public static final String SERVICE_GOOGLE = "google"
	
	public static final String TYPE_REQUEST_TOKEN = "request_token"
	public static final String TYPE_ACCESS_TOKEN = "access_token"
	
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key id

	@ManyToOne(fetch = FetchType.EAGER)
	User user;
	
	@Basic
	String token
	
	@Basic
	String secret
	
	@Basic
	String verifier
	
	@Basic
	String service
	
	@Basic 
	String type
	
    static constraints = {
    	id(visible:false)
	}
}
