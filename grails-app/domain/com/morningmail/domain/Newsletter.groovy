package com.morningmail.domain

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text

import com.morningmail.utils.DateUtils;
import org.datanucleus.jpa.annotations.Extension;

@Entity
class Newsletter implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
	String id

	@Basic
	String name;
	
	@ManyToOne(fetch = FetchType.LAZY)
	User owner;

	@Basic
	List<Key> interests = new ArrayList<Key>();
	
	@Basic
	List<Key> subscribers = new ArrayList<Key>();
	
	@Basic
	Text curatorInfo = new Text("");
	
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
	
	/**
	 * This is needed to do case insensitive queries
	 */
	@Basic
	String nameUppercase
	
	@PrePersist
	@PreUpdate
	public void prePersist() {
		nameUppercase = name == null ? null : name.toUpperCase();
	}
	
    static constraints = {
    	id(visible:false)
		owner(nullable:false)
		deliveryTime(nullable:false)
		localDeliveryTime(nullable:false)
		timeZone(nullable:false, blank:false)
	}
	
	public void subscribe(User user) {
		subscribers.add(user.id)
	}
	
	public void unsubscribe(User user) {
		subscribers.remove(user.id)
	}
	
	public void setInterests(List<Key> interestKeys) {
		this.interests.clear()
		this.interests = interestKeys
	}
	
	public void setInterests(String[] keys) {
		this.interests.clear()
		for(String k: keys)
			this.interests.add(KeyFactory.stringToKey(k))
	}
	
	public void setInterests(String key) {
		String[] arr = new String[1]
		arr[0] = key
		setInterests(arr)
	}
	
	public static Newsletter create(User owner, String title, 
		String deliveryTime, String timeZone) {
		Newsletter nl = new Newsletter()
		nl.owner = owner
		nl.name = title
		nl.localDeliveryTime = deliveryTime
		nl.timeZone = timeZone
		nl.lastRenderedDate = new Date(0)
		
		try {
			nl.deliveryTime = DateUtils.
				getNormalizedDeliveryTime(deliveryTime,
					DateUtils.getOffsetTimeZone(timeZone))
		} catch (Exception e) {
			//do nothing, the domain object will throw a validation error
		}
		return nl
	}

}
