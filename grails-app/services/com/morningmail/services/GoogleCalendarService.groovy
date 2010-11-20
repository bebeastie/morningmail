package com.morningmail.services

import javax.persistence.Query;
import java.util.Calendar;

import org.springframework.beans.factory.InitializingBean
import com.google.gdata.client.GoogleService;
import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.authn.oauth.OAuthSigner;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.Feed;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.ColorProperty;
import com.google.gdata.data.calendar.HiddenProperty;
import com.google.gdata.data.calendar.TimeZoneProperty;
import com.google.gdata.data.extensions.Where;
import com.google.gdata.util.ServiceException;
import com.google.gdata.client.*;
import com.google.gdata.client.calendar.*;
import com.google.gdata.data.*;
import com.google.gdata.data.acl.*;
import com.google.gdata.data.calendar.*;
import com.google.gdata.data.extensions.*;
import com.google.gdata.util.*;
import com.google.appengine.api.datastore.Text

import com.morningmail.domain.OAuthToken;
import com.morningmail.domain.User;
import com.morningmail.domain.PersonalFeed;

import javax.persistence.*
import org.springframework.orm.jpa.EntityManagerFactoryUtils

class GoogleCalendarService implements InitializingBean, PersonalFeedService {

	static class InvalidTokenStringException extends Exception {

	}
	
	static class FailedCalendarAccessException extends Exception {
		
	}
	
	public static final String CALLBACK = "http://apponthebowery.appspot.com/signup/completeGoogleAuth"
	
	private static final String CONSUMER_KEY = "apponthebowery.appspot.com"
	private static final String CONSUMER_SECRET = "4GyRDvv47fm9Kn3xhIhCxqxb"
	private static final String SCOPE = "http://www.google.com/calendar/feeds/"
//	private static final String CALENDAR_FEED = "https://www.google.com/calendar/feeds/default/owncalendars/full"
	private static final String CALENDAR_FEED = "https://www.google.com/calendar/feeds/default/private/full"
	 
	def entityManagerFactory
	EntityManager em
	
	void afterPropertiesSet() {
		
	}
	
	/**
	 * Checks whether we have access to the user's calendar already
	 * @param u
	 * @return true if we have access, false if not
	 */
	def OAuthToken getAccessToken(User u) {
		em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory)

		Query q = em.createQuery("select t from OAuthToken t where t.service = :service and t.type = :type and t.user = :user")
		
		q.setParameter("service", OAuthToken.SERVICE_GOOGLE)
		q.setParameter("type", OAuthToken.TYPE_ACCESS_TOKEN)
		q.setParameter("user", u)
		
		try {
			OAuthToken token = q.getSingleResult()
			log.info("Returning a token with key: " + token.token + " and secret: " + token.secret)
			return token
		} catch (NoResultException e) {
			return null
		}
	}
	
//	public String getCalendarNames(User u) throws FailedCalendarAccessException {
//
//		OAuthToken token = getAccessToken(u) 
//		
//		log.info("Attempting to use the following token: " + token.token + 
//			" with secret " + token.secret + " and verifier " + token.verifier)
//		
//		if (token) {
//			GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters()
//			oauthParameters.setOAuthConsumerKey(CONSUMER_KEY);
//			oauthParameters.setOAuthConsumerSecret(CONSUMER_SECRET);
//			oauthParameters.setOAuthToken(token.token)
//			oauthParameters.setOAuthTokenSecret(token.secret)
//
//			oauthParameters.setOAuthSignatureMethod("HMAC-SHA1")
//			
//			OAuthSigner signer = new OAuthHmacSha1Signer();
//			CalendarService service = new CalendarService("apponthebowery.appspot.com");
//			
//			
//			
//			service.setOAuthCredentials(oauthParameters, signer)
//		
//
//			URL feedUrl = new URL(CALENDAR_FEED);
//			CalendarQuery myQuery = new CalendarQuery(feedUrl);
//			myQuery.setMinimumStartTime(DateTime.parseDateTime("2010-11-17T00:00:00"));
//			myQuery.setMaximumStartTime(DateTime.parseDateTime("2010-11-17T23:59:59"));
//			CalendarEventFeed resultFeed = service.query(myQuery, CalendarEventFeed.class);
//			
//
//			String calendarNames = new String();
//			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
//			  CalendarEventEntry entry = resultFeed.getEntries().get(i);
//			  calendarNames = calendarNames + "<br/>" + entry.getTitle().getPlainText();
//			}
//			
//			return calendarNames
//			
//		} else {
//			throw new FailedCalendarAccessException("Can't find token to access calendar with!");
//		}
//	}
	
	
	/**
	* Called when we need to generate an authorization URL for the user
	* to click on
	*/
	public String generateRequestTokenAndUrl(User u, String callbackUrl) {
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters()
		oauthParameters.setOAuthConsumerKey(CONSUMER_KEY);
		oauthParameters.setOAuthConsumerSecret(CONSUMER_SECRET);
		oauthParameters.setScope(SCOPE);
		oauthParameters.setOAuthCallback(callbackUrl);
	   
		OAuthSigner signer = new OAuthHmacSha1Signer();
		GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(signer);
		
		oauthHelper.getUnauthorizedRequestToken(oauthParameters);

		OAuthToken token = new OAuthToken()
		token.service = OAuthToken.SERVICE_GOOGLE
		token.type = OAuthToken.TYPE_REQUEST_TOKEN
		token.token = oauthParameters.getOAuthToken()
		token.secret = oauthParameters.getOAuthTokenSecret()
		token.verifier = oauthParameters.getOAuthVerifier()
		
		token.user = u
		u.tokens.add(token)
		
		token.save()
	   
		String requestUrl = oauthHelper.createUserAuthorizationUrl(oauthParameters);
	   
		return requestUrl
   }
   
   /**
	* Called when we need to convert a temporary token
	*/
   public void upgradeRequestToken(String oauth_token, String oauth_verifier) throws InvalidTokenStringException {
	   log.info("Entering upgradeRequestToken")
	   
	   em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory)
	   
	   GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters()
	   oauthParameters.setOAuthConsumerKey(CONSUMER_KEY);
	   oauthParameters.setOAuthConsumerSecret(CONSUMER_SECRET);

	   Query q = em.createQuery("select t from OAuthToken t where t.service = :service and t.type = :type and t.token = :token")
	   q.setParameter("service", OAuthToken.SERVICE_GOOGLE)
	   q.setParameter("type", OAuthToken.TYPE_REQUEST_TOKEN)
	   q.setParameter("token", oauth_token)
	   
	   log.info("Running the following query: " + q)
	   
	   OAuthToken token = q.getSingleResult()
	   
	   log.info("Got the following back with token " + token.token + " and secret " + token.secret)
	   
	   if (!token) {
		   throw new InvalidTokenStringException("Can't find a Google request token that" +
			   "needs to be upgraded for token:" + oauth_token); 
	   }
	   
	   oauthParameters.setOAuthToken(token.token)
	   oauthParameters.setOAuthTokenSecret(token.secret)
	   oauthParameters.setOAuthVerifier(oauth_verifier)
	   
	   OAuthSigner signer = new OAuthHmacSha1Signer();
	   GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(signer);
	    
	   try {
		   oauthHelper.getAccessToken(oauthParameters)
		   token.setToken(oauthParameters.getOAuthToken())
		   token.setSecret(oauthParameters.getOAuthTokenSecret())
		   token.setVerifier(oauthParameters.getOAuthVerifier())
		   token.setType(OAuthToken.TYPE_ACCESS_TOKEN)
		   token.save()
		   log.info("Just saved the token")
	   } catch (OAuthException e) {
	   		throw new InvalidTokenStringException("Found the token " + tokenString + 
				   " in the db but couldn't convert it.", e)
	   }
   }
   
   
   public void fetch(User u) {
	   
	   OAuthToken token = getAccessToken(u)
   
	   if (token) {
		   GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters()
		   oauthParameters.setOAuthConsumerKey(CONSUMER_KEY);
		   oauthParameters.setOAuthConsumerSecret(CONSUMER_SECRET);
		   oauthParameters.setOAuthToken(token.token)
		   oauthParameters.setOAuthTokenSecret(token.secret)

		   oauthParameters.setOAuthSignatureMethod("HMAC-SHA1")
		   
		   OAuthSigner signer = new OAuthHmacSha1Signer();
		   CalendarService service = new CalendarService("apponthebowery.appspot.com");		   
		   service.setOAuthCredentials(oauthParameters, signer)
	   

		   Calendar jCalBegin = Calendar.getInstance();
		   jCalBegin.set(Calendar.HOUR, 0);
		   jCalBegin.set(Calendar.MINUTE, 0);
		   jCalBegin.set(Calendar.SECOND, 0);
		   
		   Calendar jCalEnd = Calendar.getInstance();
		   jCalEnd.set(Calendar.HOUR, 23);
		   jCalEnd.set(Calendar.MINUTE, 59);
		   jCalEnd.set(Calendar.SECOND, 59);
		   
		   
		   URL feedUrl = new URL(CALENDAR_FEED);
		   CalendarQuery myQuery = new CalendarQuery(feedUrl);
		   myQuery.setMinimumStartTime(new DateTime(jCalBegin.getTime()));
		   myQuery.setMaximumStartTime(new DateTime(jCalEnd.getTime()));
		   
		   
		   CalendarEventFeed resultFeed = service.query(myQuery, CalendarEventFeed.class);
		   
		  
		   String events = new String();
		   for (int i = 0; i < resultFeed.getEntries().size(); i++) {
			 CalendarEventEntry entry = resultFeed.getEntries().get(i);
			 events = events + "<br/>" + entry.getTitle().getPlainText();
		   }
		   
		   
		   //now save the feed
		   PersonalFeed feed = PersonalFeed.findByTypeAndUser(PersonalFeed.TYPE_GOOGLE_CAL, u);
		   
		   if (!feed) {
			   feed = new PersonalFeed()
			   feed.type = PersonalFeed.TYPE_GOOGLE_CAL
			   feed.user = u
			   u.pFeeds.add(feed)
			   feed.save()
		   }
		   
		   feed.html = new Text(events)
		   feed.lastUpdated = new Date()
		   		   
	   } else {
		   log.error("Couldn't fetch calendar for User $u because we didn't have a token.")
	   }
   }
   
   public String getHtml(User u) {
	   try {
		   PersonalFeed feed = PersonalFeed.findByTypeAndUser(PersonalFeed.TYPE_GOOGLE_CAL, u)
		   return feed.html.getValue()
	   }catch (Exception e) {
	   	   log.error("Couldn't find feed for $u")
		   return ""
	   }
   }
   
   public String getPlainText(User u) {
	   //@TODO
	   return " "
   }
   
}

