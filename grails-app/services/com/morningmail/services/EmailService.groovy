package com.morningmail.services

import com.morningmail.domain.Interest
import com.morningmail.domain.User
import com.morningmail.domain.Email
import com.morningmail.services.FeedService
import com.morningmail.services.PersonalFeedService
import com.google.appengine.api.datastore.Text

class EmailService {
	public static final Interest TOP_NEWS = Interest.findByType(Interest.TYPE_TOP_NEWS)
	public static final Interest WEATHER = Interest.findByType(Interest.TYPE_WEATHER)
	public static final Interest GOOGLE_CAL = Interest.findByType(Interest.TYPE_GOOGLE_CAL)
	
	public static final String getPlainTextHeader() {
		String header = new String()
		header = "Good Morning!\n"
		return header
	}
	
	public static final String getPlainTextFooter() {
		String footer = new String()
		footer = "\n Thanks!"
		return footer
	}
	
	PersonalFeedService googleWeatherService
	PersonalFeedService googleCalendarService
	FeedService yahooNewsFeedService
	
	public void fetchPersonalFeeds(User u){
		try {
			if (u.interests.contains(GOOGLE_CAL.id)) 
				googleCalendarService.fetch(u)
			
			if (u.interests.contains(WEATHER.id))
				googleWeatherService.fetch(u)
			
		} catch(Exception e) {
			log.error("Can't fetch personal feeds for user $u \n" + e)
		}	
	}
	
	public Email render(User u) {
		try {
			StringBuffer contents = new StringBuffer()
			contents.append(getPlainTextHeader())
			
			if (u.interests.contains(WEATHER.id)) {
				contents.append(googleWeatherService.getPlainText(u)) 
				contents.append("\n\n")
			}
			
			if (u.interests.contains(TOP_NEWS.id)) {
				contents.append(yahooNewsFeedService.getPlainText())
				contents.append("\n\n")
			}
			
			if (u.interests.contains(GOOGLE_CAL.id)) {
				contents.append(googleCalendarService.getPlainText(u))
				contents.append("\n\n")
			}
			
			contents.append(getPlainTextFooter())
			
			//now time to save it
			Email email = new Email()
		
			email.contents = new Text(contents.toString())
			email.status = Email.STATUS_PENDING
			email.lastUpdated = new Date()
		
			//need to set deliverydate
				
			email.user = u
			u.emails.add(email)
			email.save()

			return email
		} catch (Exception e) {
			log.error("Can't render email for user $u \n" + e)
		}
	}
	
	public void send(User u) {
		
	}
}
