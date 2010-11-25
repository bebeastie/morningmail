package com.morningmail.services

import com.morningmail.domain.Interest
import com.morningmail.domain.User
import com.morningmail.domain.Email
import com.morningmail.services.FeedService
import com.morningmail.services.PersonalFeedService
import com.google.appengine.api.datastore.Text

import java.util.Calendar;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.InitializingBean
import java.text.SimpleDateFormat

class EmailService implements InitializingBean {
	public static Interest TOP_NEWS
	public static Interest WEATHER
	public static Interest GOOGLE_CAL
	
	public static final String SUBJECT_BEGIN = "MorningMail - "
	
	public static final String getPlainTextHeader() {
		String header = new String()
		header = "MorningMail - "
		return header
	}
	
	public static final String getPlainTextFooter() {
		String footer = new String()
		footer = "Thanks,\nMorningMail"
		return footer
	}
	
	
	private static String getTodaysDate() {
		Date now = Calendar.getInstance().getTime()
		SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, MMM d")
		dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT-5"))
		return dateFormatter.format(now)
	}
	
	void afterPropertiesSet() {
		TOP_NEWS = Interest.findByType(Interest.TYPE_TOP_NEWS)
		WEATHER = Interest.findByType(Interest.TYPE_WEATHER)
		GOOGLE_CAL = Interest.findByType(Interest.TYPE_GOOGLE_CAL)
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
			log.error("Can't fetch personal feeds for user $u \n", e)
		}	
	}
	
	public Email render(User u) {
		try {
			
			StringBuffer contents = new StringBuffer()
			contents.append(getPlainTextHeader() + getTodaysDate() + "\n\n")
			
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
		
			email.contents = new Text(contents.toString().trim())
			email.status = Email.STATUS_PENDING
			email.lastUpdated = new Date()
		
			//need to set deliverydate
				
			email.user = u
			
			//add a reference to the user
			u.emails.add(email)
			//mark the user as well
			u.lastRenderedDate = Calendar.getInstance().getTime()
			
			email.save()

			return email
		} catch (Exception e) {
			log.error("Can't render email for user $u", e)
		}
	}
	
	public void send(Email email) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		String msgBody = email.contents.getValue()
			
		String subject = SUBJECT_BEGIN + getTodaysDate()
		
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("blake.barnes@gmail.com", "MorningMail"));
			msg.addRecipient(Message.RecipientType.TO,
							 new InternetAddress(email.user.email, email.user.name));
			msg.setSubject(subject);
			msg.setText(msgBody)
			Transport.send(msg);
			
			//now mark it sent
			email.status = Email.STATUS_SENT
			email.deliveryDate = msg.getSentDate()
			
		} catch (AddressException e) {
			log.error("Problems sending email $email.id ", e)
		} catch (MessagingException e) {
			log.error("Problems sending email $email.id ", e)
		}
	}
}
