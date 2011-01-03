package com.morningmail.services

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.*;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import com.morningmail.services.PersonalFeedService;
import com.morningmail.domain.User;
import com.morningmail.domain.OAuthToken;
import com.morningmail.services.OAuthService;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

class TwitterUserTimelineService implements PersonalFeedService, OAuthService{

	def entityManagerFactory
	EntityManager em
	
	public OAuthToken generateRequestToken(User u) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey("FhIKL7PHV1y3tOekw5FMvA")
			.setOAuthConsumerSecret("hrPZTlhrwBjuV8VB6bkJDrGIXCVl3PpsDgUv522uNY")

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
		cb.setOAuthConsumerKey("FhIKL7PHV1y3tOekw5FMvA")
			.setOAuthConsumerSecret("hrPZTlhrwBjuV8VB6bkJDrGIXCVl3PpsDgUv522uNY")

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
		Query q = em.createQuery("select t from OAuthToken t where t.service = :service and t.type = :type and t.user = :user")
		q.setParameter("service", OAuthToken.SERVICE_TWITTER)
		q.setParameter("type", OAuthToken.TYPE_ACCESS_TOKEN)
		q.setParameter("user", u)
			
		try {
			OAuthToken token = q.getSingleResult()
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setOAuthConsumerKey("FhIKL7PHV1y3tOekw5FMvA")
				.setOAuthConsumerSecret("hrPZTlhrwBjuV8VB6bkJDrGIXCVl3PpsDgUv522uNY")
				.setOAuthAccessToken(token.getToken())
				.setOAuthAccessTokenSecret(token.getSecret())
				.setDebugEnabled(true)
				.setIncludeEntitiesEnabled(true)
			Twitter twitter = new TwitterFactory(cb.build()).getInstance()
			twitter4j.User tUser = twitter.verifyCredentials();
			
			/*We are going to gather all of the day's statuses, 
			 * up to a limit of 800 unique statuses
			 * (4 requests * 200 statuses/request). Retweets are not  
			 * included in this count and actually 
			 */
			List<Status> statuses = twitter.getHomeTimeline();
			
			for (Status status : statuses) {

				for (twitter4j.URLEntity entity: status.getURLEntities()) {
					System.out.println(entity.getExpandedURL());
					System.out.println(entity.getURL());
				}
				System.out.println("-@" + status.getUser().getScreenName() + " - " + status.getText());
			}
		}catch (NoResultException e) {
			//do nothing for now
		}				
	}
	
	public OAuthToken getToken(User u) {}
	
	public void fetch(User u) {
		
	}
	
	public String getHtml(User u) {
		
	}
	
	public String getPlainText(User u) {
		
	}
}