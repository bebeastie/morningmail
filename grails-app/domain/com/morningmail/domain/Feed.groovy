package com.morningmail.domain



import javax.persistence.*;
// import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text

@Entity
class Feed implements Serializable {
	public static final String TYPE_YAHOO_NEWS = "yahoo_news"
	
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id

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
