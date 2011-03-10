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
	
	private static final String SUBJECT_BEGIN = "MorningMail - "
	
	private static final TEMPLATE_TITLE = "<!-- *|TITLE|* -->"
	private static final TEMPLATE_LOGO = "<!-- *|LOGO|* -->"
	private static final TEMPLATE_DATE = "<!-- *|DATE|* -->"
	private static final TEMPLATE_PERSONALIZE = "<!-- *|PERSONALIZE|* -->"
	private static final TEMPLATE_EDITION = "<!-- *|EDITION|* -->"
	private static final TEMPLATE_CURATOR_INFO = "<!-- *|CURATOR_INFO|* -->"
	private static final TEMPLATE_CONTENT = "<!-- *|CONTENT|* -->"
	private static final TEMPLATE_SENT_TO = "<!-- *|SENT_TO|* -->"
	private static final TEMPLATE_UNSUBSCRIBE = "<!-- *|UNSUBSCRIBE|* -->"
	private static final TEMPLATE_SUBSCRIBE = "<!-- *|SUBSCRIBE|* -->"
	private static final TEMPLATE_ARCHIVE = "<!-- *|ARCHIVE|* -->"
	
	private static final String getSubject(Newsletter nl) {
		SUBJECT_BEGIN + nl.name + " Edition - " + getTodaysDate()
	}
	
	private static final String getLogoUrl() {
		return WebUtils.getAbsoluteUrl("/images/logo2.png");
	}
	
	private static final String getUnsubscribeUrl(String emailId) {
		return WebUtils.getAbsoluteUrl('newsletter', 'unsubscribe', [emailId:emailId])
	}
	
	private static final String getSubscribeUrl(Newsletter nl) {
		return WebUtils.getAbsoluteUrl('newsletter', 'view', [name:nl.name])
	}
	
	private static final String getViewBrowserUrl(String emailId) {
		return WebUtils.getAbsoluteUrl('email','view',[emailId:emailId])
	}
	
	private static final String getPersonalizeUrl() {
		return "mailto:admin@getmorningmail.com?subject=Request+invite+to+MorningMail"
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
					
			//generate key
			Key emailId = new KeyFactory.Builder(User.class.getSimpleName(), u.email)
				.addChild(Email.class.getSimpleName(), UUID.randomUUID().toString().replaceAll('-', ''))
				.getKey()
			
			//iterate through interests
			StringBuffer textBody = new StringBuffer()
			StringBuffer htmlBody = new StringBuffer()
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
					htmlBody.append(htmlFeed).append("<br/>")
				} 
				
				if (textFeed != "") {
					textBody.append(textFeed).append("\n\n")
				} 		
			}
			
			//build HTML version
			File htmlTemplate = applicationContext.getResource("/emailTemplates/basic.html").getFile()			
			Scanner htmlScanner = new Scanner(new FileInputStream(htmlTemplate), "UTF8");

			try {
			  String NL = System.getProperty("line.separator");
			  while (htmlScanner.hasNextLine()){
				String line = htmlScanner.nextLine()
				String originalLine = new String(line);
				
				if (line.contains(new String("<!-- *|"))) {
					line = line.replace(TEMPLATE_ARCHIVE, getViewBrowserUrl(KeyFactory.keyToString(emailId)))
					line = line.replace(TEMPLATE_TITLE, getSubject(nl))
					line = line.replace(TEMPLATE_LOGO, getLogoUrl())
					line = line.replace(TEMPLATE_DATE, getTodaysDate())
					line = line.replace(TEMPLATE_PERSONALIZE, getPersonalizeUrl())
					line = line.replace(TEMPLATE_EDITION, nl.name)
					line = line.replace(TEMPLATE_CURATOR_INFO, nl.curatorInfo.getValue())
					line = line.replace(TEMPLATE_CONTENT, htmlBody.toString())
					line = line.replace(TEMPLATE_SENT_TO, u.email)
					line = line.replace(TEMPLATE_UNSUBSCRIBE, getUnsubscribeUrl(KeyFactory.keyToString(emailId)))
					line = line.replace(TEMPLATE_SUBSCRIBE, getSubscribeUrl(nl))
				}
				html.append(line + NL);
			  }
			} finally{
			  htmlScanner.close();
			}

			//build text version
			File textTemplate = applicationContext.getResource("/emailTemplates/basic.txt").getFile()
			Scanner textScanner = new Scanner(new FileInputStream(textTemplate), "UTF8");

			log.info("TXTER: " + textBody.toString())
			try {
			  String NL = System.getProperty("line.separator");
			  while (textScanner.hasNextLine()){
				String line = textScanner.nextLine()
				String originalLine = new String(line);
				
				if (line.contains(new String("<!-- *|"))) {
					line = line.replace(TEMPLATE_ARCHIVE, getViewBrowserUrl(KeyFactory.keyToString(emailId)))
					line = line.replace(TEMPLATE_DATE, getTodaysDate())
					line = line.replace(TEMPLATE_EDITION, nl.name)
					line = line.replace(TEMPLATE_CONTENT, textBody.toString())
					line = line.replace(TEMPLATE_UNSUBSCRIBE, getUnsubscribeUrl(KeyFactory.keyToString(emailId)))
				}
				text.append(line + NL);
			  }
			} finally{
			  textScanner.close();
			}
				
			Email email = new Email()
			email.id = emailId
			email.user = u
			email.newsletterKey = KeyFactory.stringToKey(nl.id)
			
			//set body
			email.html = new Text(html.toString())
			email.plainText = new Text(text.toString())
			
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

            msg.setFrom(new InternetAddress("blake.barnes@gmail.com", "MorningMail"));
			msg.addRecipient(Message.RecipientType.TO,
							new InternetAddress(email.user.email));
			msg.setSubject(subject);
           
			log.info("Email:" + email.plainText.getValue())
            MimeMultipart mp = new MimeMultipart();
            BodyPart txt = new MimeBodyPart();
			txt.setText(email.plainText.getValue(), "utf-8");
			txt.setHeader("Content-Type","text/plain; charset=\"utf-8\"");
			txt.setHeader("Content-Transfer-Encoding", "quoted-printable");
			
            mp.addBodyPart(txt);

            BodyPart html = new MimeBodyPart();
            html.setContent(email.html.getValue()
				, "text/html; charset=utf-8");
			html.setHeader("Content-Type","text/html; charset=\"utf-8\"");
			html.setHeader("Content-Transfer-Encoding", "quoted-printable");
            mp.addBodyPart(html);

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
