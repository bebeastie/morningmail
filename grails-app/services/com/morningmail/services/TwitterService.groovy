package com.morningmail.services

import java.net.URL;
import java.util.List;

import javax.persistence.*;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import com.morningmail.services.PersonalFeedService;
import com.morningmail.domain.*;
import com.morningmail.services.OAuthService;
import com.morningmail.utils.DateUtils;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

class TwitterService implements PersonalFeedService, OAuthService{
	private static final String CONSUMER_KEY = "FhIKL7PHV1y3tOekw5FMvA"
	private static final String CONSUMER_SECRET = "hrPZTlhrwBjuV8VB6bkJDrGIXCVl3PpsDgUv522uNY"
	private static final List IGNORE_DOMAINS = new ArrayList<String>()
	private static final String HASH_MARKER = "digest";
	
	static {
		IGNORE_DOMAINS.add("foursquare.com")
		IGNORE_DOMAINS.add("4sq.com")
		IGNORE_DOMAINS.add("flic.kr")
		IGNORE_DOMAINS.add("flicksquare.me")
		IGNORE_DOMAINS.add("instagr.am")
		IGNORE_DOMAINS.add("twitpic.com")
		IGNORE_DOMAINS.add("listen.grooveshark.com")
		IGNORE_DOMAINS.add("open.spotify.com")
		IGNORE_DOMAINS.add("shz.am")
		IGNORE_DOMAINS.add("rnkpr.com")
		IGNORE_DOMAINS.add("gowal.la")
		IGNORE_DOMAINS.add("www.youtube.com")
		IGNORE_DOMAINS.add("x.im")
	}
	
	def entityManagerFactory
	EntityManager em
	
	def transactional = true
	
	public OAuthToken generateRequestToken(User u) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(CONSUMER_KEY)
			.setOAuthConsumerSecret(CONSUMER_SECRET)

		Twitter twitter = new TwitterFactory(cb.build()).getInstance()
		RequestToken requestToken = twitter.getOAuthRequestToken();
		
		OAuthToken token = new OAuthToken()
		token.service = OAuthToken.SERVICE_TWITTER
		token.token = requestToken.getToken()
		token.secret = requestToken.getTokenSecret()
		token.type = OAuthToken.TYPE_REQUEST_TOKEN
		token.authorizationUrl = requestToken.getAuthorizationURL()
		token.user = u
		u.tokens.add(token)
		token.save()
	}
	
	public OAuthToken upgradeToken(String token, String verifier) {
		em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory)
		
		OAuthToken oAuthToken = OAuthToken.findByTokenAndService(token, OAuthToken.SERVICE_TWITTER)
		RequestToken requestToken = new RequestToken(oAuthToken.token, oAuthToken.secret)

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(CONSUMER_KEY)
			.setOAuthConsumerSecret(CONSUMER_SECRET)

		Twitter twitter = new TwitterFactory(cb.build()).getInstance()
		AccessToken accessToken
		
		try{
			if(!verifier || verifier.length() > 0){
				accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
			}else{
				accessToken = twitter.getOAuthAccessToken(requestToken);
			}
			oAuthToken.token = accessToken.getToken()
			oAuthToken.secret = accessToken.getTokenSecret()
			oAuthToken.verifier = verifier
			oAuthToken.type = OAuthToken.TYPE_ACCESS_TOKEN
			em.merge(oAuthToken)
		}catch (TwitterException te) {
			if(401 == te.getStatusCode()){
				log.error("Unable to get the access token.");
			}else{
				log.error("Unable to get the acess token.",te)
			}
		}
		return oAuthToken
	}
	
	public void getFeed(User u) {
		em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory)
		def tx = em.getTransaction()

		Query q = em.createQuery("select t from OAuthToken t where t.service = :service and t.type = :type and t.user = :user")
		q.setParameter("service", OAuthToken.SERVICE_TWITTER)
		q.setParameter("type", OAuthToken.TYPE_ACCESS_TOKEN)
		q.setParameter("user", u)
		
		OAuthToken token
		
		try {
			token = q.getSingleResult()
			tx.commit()
		}catch (NoResultException e) {
			//do nothing
		} finally {
			if (tx.isActive())
				tx.rollback()
		}
				
		if (token) {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setOAuthConsumerKey(CONSUMER_KEY)
				.setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(token.getToken())
				.setOAuthAccessTokenSecret(token.getSecret())
				.setIncludeEntitiesEnabled(true)
			Twitter twitter = new TwitterFactory(cb.build()).getInstance()
			twitter4j.User tUser = twitter.verifyCredentials();
			
			/* We are going to gather all of the day's statuses, 
			 * up to a limit of 800 unique statuses
			 * (4 requests * 200 statuses/request). Retweets are not  
			 * included in this count and actually 
			 */
			
			twitter4j.Paging paging = new twitter4j.Paging()
			paging.setCount(800)
			
			if (u.lastTweetId)
				paging.setSinceId(u.lastTweetId)
						
			List<Status> statuses = twitter.getHomeTimeline(paging);
			
			Long newLastTweetId = null
			for (Status status : statuses) {
				if (!DateUtils.isWithin24Hours(status.getCreatedAt()))
					break
					
				for (twitter4j.URLEntity entity: status.getURLEntities()) {
					
					URL url = entity.getExpandedURL() == null ? 
						entity.getURL() : entity.getExpandedURL()
					
					if (IGNORE_DOMAINS.contains(url.getHost()))
						break
					
					
					TwitterLink tl = TwitterLink.findByUrlAndIsArchived(
						url.toString().toLowerCase(), false)	
					
					if (tl) {
						try {
							System.out.println("Adding click " + entity.toString())
							tl.addClick()
							tx.begin()
							tl.merge()
							tx.commit()
						} finally {
							if (tx.isActive())
								tx.rollback()
						}
					} else {
						tl = new TwitterLink()
						tl.userKey = u.id
						tl.firstScreenName = status.getUser().getScreenName()
						tl.firstDate = status.getCreatedAt()
						tl.firstTweet = status.getText()
						tl.url = url.toString().toLowerCase()
						tl.domain = url.getHost().toLowerCase()
						tl.numberTweets = 1
						if (tl.validate()) {
							try {
								tx.begin()
								tl.save()
								tx.commit()
							} finally {
								if (tx.isActive())
									tx.rollback()
							}
						}
					}
				}
				newLastTweetId = status.getId()
			}
			
			if (newLastTweetId) {
				try {
					u.lastTweetId = newLastTweetId
					tx.begin()
					u.merge()
					tx.commit()
				} finally {
					if (tx.isActive())
						tx.rollback()
				}
			}
		}
		//this is my dirty little hack for making sure the container has a transaction to end
		if (!tx.isActive())
			tx.begin()
	}
	
	public void getTweets(String screenName) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(CONSUMER_KEY)
			.setOAuthConsumerSecret(CONSUMER_SECRET)
			.setIncludeEntitiesEnabled(true)
			.setTrimUserEnabled(true)
		Twitter twitter = new TwitterFactory(cb.build()).getInstance()
		twitter4j.Paging paging = new twitter4j.Paging()
		paging.setCount(200)
		
		List<Status> statuses = twitter.getUserTimeline(screenName, paging)
		
		for(Status status: statuses) {
			if (!DateUtils.isWithin24Hours(status.getCreatedAt()))
				break
			
			for(twitter4j.HashtagEntity htag: status.getHashtagEntities()) {
				if(HASH_MARKER.equals(htag.getText().toLowerCase())) {
					for(twitter4j.URLEntity url: status.getURLEntities()) {
						
					}
					break
				}	
			}
		}
	}
	
	public void createTwitterLink(User user, URL url, String firstTweet) {

	}
	
	public OAuthToken getToken(User u) {}
	
	public void fetch(User u) {
		
	}
	
	public String getHtml(User u) {
		
	}
	
	public String getPlainText(User u) {
		
	}
	
}
