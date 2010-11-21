package com.morningmail.domain



import javax.persistence.*;
 import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text

@Entity
class Interest implements Serializable {
	public static final String TYPE_TOP_NEWS = "top_news"
	public static final String TYPE_GOOGLE_CAL = "google_cal"
	
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Key id

	@Basic
	String type
	
	@Basic
	String displayName
				
    static constraints = {
    	id visible:false
	}
}
