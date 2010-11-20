package com.morningmail.domain



import javax.persistence.*;
// import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text

@Entity
class PersonalFeed implements Serializable {
	public static final String TYPE_GOOGLE_CAL = "google_cal"
	
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id

	@ManyToOne(fetch = FetchType.LAZY)
	User user;
	
	@Basic
	String type
	
	@Basic
	Text html
	
	@Basic
	Text plainText
	
	@Basic
	Date lastUpdated
	
    static constraints = {
    	id visible:false
	}
}
