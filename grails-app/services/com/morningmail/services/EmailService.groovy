package com.morningmail.services

import com.morningmail.domain.Interest
import com.morningmail.domain.User
import com.morningmail.domain.Newsletter
import com.morningmail.domain.Email
import com.morningmail.domain.Feed
import com.morningmail.domain.PersonalFeed;
import com.morningmail.services.FeedService
import com.morningmail.services.PersonalFeedService
import com.morningmail.utils.WebUtils;
import com.google.appengine.api.datastore.KeyFactory;
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
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.BodyPart;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.InitializingBean
import java.text.SimpleDateFormat
import com.google.appengine.api.datastore.Key
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

class EmailService implements InitializingBean {

	public static Interest WEATHER
	public static Interest GOOGLE_CAL
	public static Interest READ_LATER
	
	public static final String SUBJECT_BEGIN = "MorningMail - "
	
	public static final String getPlainTextHeader() {
		return "MorningMail - " + getTodaysDate() + "\n\n"
	}
	
	public static final String getPlainTextFooter(String emailAddress, String emailId) {
		StringBuffer sb = new StringBuffer()
		sb.append("Have a great day,\nThe MorningMail Team")
		sb.append("Sent to: " + emailAddress + " | Unsubscribe:"  )
		sb.append("\n"+WebUtils.getUrl('newsletter', 'unsubscribe', [emailId:emailId]))
		return sb.toString()
	}
	
	public static final String getHtmlHeader() {
		StringBuffer header = new StringBuffer()
		header.append("<html><head>")
			.append(getCss()).append("<title>")
			.append(getTodaysDate())
			.append("</title></head>")
			.append("<body>")
			.append("<center><b>MorningMail - " + getTodaysDate() + "</b><br/><br/></center>")
		return header.toString()
	}
	
	public static final String getHtmlFooter(String emailAddress, String emailId) {
		StringBuffer footer = new StringBuffer()
			.append("Have a great day,<br/>The MorningMail Team").append("</br>")
		.append("<center>Sent to: " + emailAddress + " | ")
		.append(WebUtils.createLinkElement('newsletter', 'unsubscribe', [emailId:emailId], 'Unsubscribe'))
		.append("</center>")
		.append("</body></html>")
		return footer.toString()
	}
	
	public static final String getCss() {
		return "<style>body{font-size: 12px;color: #111111;line-height: 150%;font-family: Verdana;" +
			"background-color: #FFFFFF;padding: 5px;border: 0px none #FFFFFF;}</style>"
	}
	
	private static String getTodaysDate() {
		Date now = Calendar.getInstance().getTime()
		SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, MMM d")
		dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT-5"))
		return dateFormatter.format(now)
	}
	
	private static String getItemTitleHtml(String title) {
		return "<b>"+title.toUpperCase()+"</b><br/>";
	}
	
	void afterPropertiesSet() {
		WEATHER = Interest.findBySystemName(Interest.SN_WEATHER)
		GOOGLE_CAL = Interest.findBySystemName(Interest.SN_GOOGLE_CAL)
		READ_LATER = Interest.findBySystemName(Interest.SN_READ_LATER)
	}
	
	PersonalFeedService googleWeatherService
	PersonalFeedService googleCalendarService
	PersonalFeedService readLaterFeedService
	
	FeedService globalFeedService
	
	def entityManagerFactory
	def em
	
	public void fetchPersonalFeeds(Newsletter nl){
		try {
			if (nl.interests.contains(GOOGLE_CAL.id)) 
				googleCalendarService.fetch(nl.owner)
			
			if (nl.interests.contains(WEATHER.id))
				googleWeatherService.fetch(nl.owner)
				
			if (nl.interests.contains(READ_LATER.id))
				readLaterFeedService.fetch(nl.owner)
			
		} catch(Exception e) {
			log.error("Can't fetch personal feeds for user $u \n", e)
		}	
	}
	
	public Email render(Newsletter nl, User u) {
		try {
			em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory)
			def tx = em.getTransaction()
			
			StringBuffer text = new StringBuffer()
			StringBuffer html = new StringBuffer()
			
			html.append(getHtmlHeader())
			text.append(getPlainTextHeader())
			
			//now time to save it
			
			Key emailId = new KeyFactory.Builder(User.class.getSimpleName(), u.email)
				.addChild(Email.class.getSimpleName(), UUID.randomUUID().toString().replaceAll('-', ''))
				.getKey()
				
			for (Key k: nl.interests) {
				Interest interest = Interest.findById(k)
				
				String htmlFeed = new String()
				String textFeed = new String()
			
				if (interest.feedStyle == Interest.FEED_STYLE_GLOBAL) {
					Feed feed = Feed.findById(interest.globalFeedId)
					FeedService.FeedServiceHelper fsHelper = 
						globalFeedService.process(feed, interest, KeyFactory.keyToString(emailId))
					htmlFeed = fsHelper.getHtml()
					textFeed = fsHelper.getPlainText()
				} else if (interest.feedStyle == Interest.FEED_STYLE_PERSONAL) {
					PersonalFeedService pFeed;
					
					if (interest.personalFeedId.equals(PersonalFeed.TYPE_GOOGLE_CAL)) {
						pFeed = googleCalendarService
					} else if (interest.personalFeedId.equals(PersonalFeed.TYPE_WEATHER)) {
						pFeed = googleWeatherService
					} else if (interest.personalFeedId.equals(PersonalFeed.TYPE_READ_LATER)) {
						pFeed = readLaterFeedService
					} 
					
					if (pFeed != null) {
						htmlFeed = pFeed.getHtml(nl.owner)
						textFeed = pFeed.getPlainText(nl.owner)
					} else {
						log.error("Couldn't find service for interest " + interest)
					}
				}
				
				if (htmlFeed != "") {
					html.append(htmlFeed).append("<br/>")
				} 
				
				if (textFeed != "") {
					text.append(textFeed).append("<\n\n>")
				} 		
			}
			
			html.append(getHtmlFooter(u.email, KeyFactory.keyToString(emailId)))
			text.append(getPlainTextFooter(u.email, KeyFactory.keyToString(emailId)))
						
			Email email = new Email()
			email.id = emailId
			email.user = u
			email.newsletterKey = KeyFactory.stringToKey(nl.id)
			email.html = new Text(html.toString())
			email.plainText = new Text(text.toString().trim())
			email.status = Email.STATUS_PENDING
			email.lastUpdated = new Date()

			try {
				u.emails.add(email) //this actually saves the email too
				tx.commit()
			} finally {
				if (tx.isActive())
					tx.rollback()
			}
			
			tx.begin() //have to start another transaction, it will be closed by the container
			nl.lastRenderedDate = Calendar.getInstance().getTime()

			return email
		} catch (Exception e) {
			log.error("Can't render email for newsletter $nl", e)
		}
	}
	
	public void send(Email email) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
			
		String subject = SUBJECT_BEGIN + getTodaysDate()
		
		try {
            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress("blake.barnes@gmail.com", "MorningMail"));
			msg.addRecipient(Message.RecipientType.TO,
							new InternetAddress(email.user.email, email.user.name));
			msg.addRecipient(Message.RecipientType.BCC, 
							new InternetAddress("bebeastie@gmail.com", "Blake Barnes"));
			msg.setSubject(subject);
           
            MimeMultipart mp = new MimeMultipart();
            BodyPart tp = new MimeBodyPart();
            tp.setText(email.plainText.getValue(), "UTF-8");
            mp.addBodyPart(tp);

            tp = new MimeBodyPart();
            tp.setContent(email.html.getValue()
				, "text/html");
            mp.addBodyPart(tp);

            mp.setSubType("alternative");

            msg.setContent(mp);

            Transport.send(msg);
	  
//			Message msg = new MimeMessage(session);
//			msg.setFrom(new InternetAddress("blake.barnes@gmail.com", "MorningMail"));
//			msg.addRecipient(Message.RecipientType.TO,
//							 new InternetAddress(email.user.email, email.user.name));
//			msg.setSubject(subject);
//			msg.setText(msgBody)
//			Transport.send(msg);
			
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
