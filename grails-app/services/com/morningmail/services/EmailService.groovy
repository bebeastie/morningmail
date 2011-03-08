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
import org.springframework.context.*

class EmailService implements InitializingBean, ApplicationContextAware {
	
	ApplicationContext applicationContext
	
	public static Interest WEATHER
	public static Interest GOOGLE_CAL
	public static Interest READ_LATER
	
	public static final String SUBJECT_BEGIN = "MorningMail - "
	
	public static final String getPlainTextHeader() {
		return "MorningMail - " + getTodaysDate() + "\n\n"
	}
	
	public static final String getPlainTextCuratorInfo(Newsletter nl) {
		StringBuffer cInfo = new StringBuffer();
		cInfo.append(nl.curatorInfo.getValue())
		return cInfo.toString();
	}
	
	public static final String getPlainTextFooter(String emailAddress, String emailId) {
		StringBuffer sb = new StringBuffer()
		sb.append("Have a great day,\nThe MorningMail Team")
		sb.append("Sent to: " + emailAddress + " | Unsubscribe:"  )
		sb.append("\n"+WebUtils.getUrl('newsletter', 'unsubscribe', [emailId:emailId]))
		return sb.toString()
	}
	
	public static final String getHtmlHeader(Newsletter nl, File css) {
		StringBuffer header = new StringBuffer()
		header.append("<html><head>")
			.append(getCss(css))
			.append("<title>")
			.append("MorningMail - ").append(nl.getName()).append(" Edition").append(" - ").append(getTodaysDate())
			.append("</title></head>")
			.append("<body>")
			.append("<div id=\"date\">").append(getTodaysDate()).append("</div>")
			.append("<div id=\"main-header\">")
			.append("<div id=\"logo\"><img src=\"").append(WebUtils.getUrl("images/logo2.png")).append("\"/></div>")
			.append("<div id=\"title\">").append(nl.getName()).append(" Edition</div>")
			.append("</div><hr/>")
		return header.toString()
	}
	
	public static final String getHtmlCuratorInfo(Newsletter nl) {
		StringBuffer cInfo = new StringBuffer();
		cInfo.append("<div>")
			.append("<b>")
			.append(nl.name.toUpperCase())
			.append("S".equals(nl.name.toUpperCase().substring(nl.name.length() - 1)) ? "'" : "'S")
			.append(" CURATORS")
			.append("</b>")
			.append("<br/>")
			.append(nl.curatorInfo.getValue())
			.append("</div><hr/>")
		return cInfo.toString();
	}
	
	public static final String getHtmlFooter(Newsletter nl, String emailAddress, String emailId) {
		StringBuffer footer = new StringBuffer()
			.append("Have a great day,<br/>The MorningMail Team").append("<br/><br/>")
		.append("<center>Sent to: " + emailAddress + " | ")
		.append(WebUtils.createLinkElement('newsletter', 'unsubscribe', [emailId:emailId], 'Unsubscribe'))
		.append(" | ").append(WebUtils.createLinkElement('newsletter', 'view', [name:nl.name], 'Subscribe'))
		.append("<br/>Want to make your own newsletter? <a href=\"mailto:info@getmorningmail.com?subject=Invite request\">Email Us</a> for an invitation")
		.append("</center>")
		.append("</body></html>")
		return footer.toString()
	}
	
	public static final String getCss(File css) {
		StringBuffer sb = new StringBuffer();
		sb.append("<style type=\"text/css\">")	
		String NL = System.getProperty("line.separator");
		Scanner scanner = new Scanner(new FileInputStream(css), "UTF-8");
		try {
		  while (scanner.hasNextLine()){
			sb.append(scanner.nextLine() + NL);
		  }
		} finally{
		  scanner.close();
		}
		sb.append("</style>")
		return sb.toString()
	}
	
	private static String getTodaysDate() {
		Date now = Calendar.getInstance().getTime()
		SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, MMM d yyyy")
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
			
			html.append(getHtmlHeader(nl, applicationContext.getResource("/css/email.css").getFile()))
			text.append(getPlainTextHeader())
			
			html.append(getHtmlCuratorInfo(nl));
			text.append(getPlainTextCuratorInfo(nl));
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
			
			html.append(getHtmlFooter(nl,u.email, KeyFactory.keyToString(emailId)))
			text.append(getPlainTextFooter(u.email, KeyFactory.keyToString(emailId)))
						
			Email email = new Email()
			email.id = emailId
			email.user = u
			email.newsletterKey = KeyFactory.stringToKey(nl.id)
			
			//set body
			email.html = new Text(html.toString())
			email.plainText = new Text(text.toString().trim())
			
			//set subject
			email.subject = SUBJECT_BEGIN + nl.name + " Edition - " + getTodaysDate()
			
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

			return email
		} catch (Exception e) {
			log.error("Can't render email for newsletter $nl", e)
		}
	}
	
	public void send(Email email) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
			
		String subject = email.subject
		
		try {
            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress("admin@getmorningmail.com", "MorningMail"));
			msg.addRecipient(Message.RecipientType.TO,
							new InternetAddress(email.user.email));
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
