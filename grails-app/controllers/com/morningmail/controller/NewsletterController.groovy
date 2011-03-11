package com.morningmail.controller

import com.morningmail.domain.Newsletter;
import com.morningmail.domain.Interest;
import com.morningmail.domain.Email;
import com.morningmail.domain.User;
import com.morningmail.domain.Feed;
import com.morningmail.utils.WebUtils;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text

import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import javax.persistence.EntityManager;

class NewsletterController {

	def interestService
	def entityManagerFactory
	
	def view = {
		Newsletter nl
		
		try {
			if (params.newsletterId) {
				nl = Newsletter.findById(params.newsletterId)	
			} else if (params.name) {
				nl = Newsletter.findByNameUppercase(params.name.replace('-',' ').toUpperCase())
			}
			//@TODO check to make sure it is public
		} catch (Exception e) {
			log.info("Had trouble finding newsletter", e)
		}
		
		nl ? render(view:'view', model:[newsletter:nl]) :
			render(view:'unknown')
	}
	
	def subscribe = {
		Newsletter nl
		String view
		
		def em = EntityManagerFactoryUtils.
			getTransactionalEntityManager(entityManagerFactory)
		
		try {
			nl = Newsletter.findById(params.newsletterId)
		} catch (Exception e) {
			log.error("Had trouble finding the newsletter", e)
			//show newsletter not found error
			response.status = 404;
			return
		}
		
		if (!WebUtils.isValidEmailAddress(params.email)) {
			flash.message = "Please enter a valid email address"
			flash.email = params.email
			view = "view"
		} else {
			User u = User.findByEmail(params.email)
			
			if(!u) {
				//@TODO this should be done by a UserService!
				//create an account
				u = new User()
				u.id = KeyFactory.createKey(User.class.getSimpleName(), params.email)
				u.email = params.email
				u.type = User.TYPE.SUBSCRIBE_ONLY
			} else if (u.type == User.TYPE.STANDARD
				&& !params.email.equals(session.email)) {
				//that email belongs to a standard user and they are not logged in
				//@TODO get the user back to the subscribe page!
				redirect(controller:'user', action:'login')
				return
			} 
			
			if (!u.subscriptions.contains(nl.id)) {
				u.subscriptions.add(nl.id)
				User.withTransaction() {
					u.save(flush:true)
				}
					
				nl.subscribers.add(u.id)
				em.merge(nl)
				view="subscribe"
			} else {
				flash.message = "You are already subscribed to this newsletter"
				flash.email = params.email
				view="view"
			}
		} 
		render(view:view, model:[newsletter:nl])
	}
	
	def unsubscribe = {
		def em = EntityManagerFactoryUtils.
			getTransactionalEntityManager(entityManagerFactory)
		
		try {
			Email email = Email.findById(KeyFactory.stringToKey(params.emailId))	
			Newsletter nl = Newsletter.findById(email.newsletterKey)
			nl.subscribers.remove(email.user.id)
			nl.merge()
			
			User u = email.user
			u.subscriptions.remove(nl.id)
			u.merge()
			
			render(view:'unsubscribe', model:[newsletter:nl])
		} catch (Exception e) {
			log.error("Problem unsubscribing from newsletter.",e)
			render(view:'error')
		}
	}
		
	def create = {
		if (!session.userEmail) {
			redirect(uri:'/')
			return
		}
		User user = User.findByEmail(session.userEmail)

		Newsletter nl = Newsletter.create(user, params.title, params.deliveryTime, 
			params.timeZone)
		
		if (nl.validate()) {
			Newsletter.withTransaction() {
				nl.subscribers.add(user.id)
				nl.save(flush:true)
			}
			def em = EntityManagerFactoryUtils.
				getTransactionalEntityManager(entityManagerFactory)
			user.subscriptions.add(nl.id)
			em.merge(user)
			
			redirect(action:'edit', params:[id:nl.id])
		} else {
			log.error("ERROR trying to create:" + nl.errors.allErrors)
		}
	}
	
	def edit = {
		if (!session.userEmail) {
			redirect(uri:'/')
			return
		}
		
		User user = User.findByEmail(session.userEmail)
		
		Newsletter nl = Newsletter.findById(params.id)
		
		def interestList = interestService.getAll(user)
		def interestMap = new HashMap<Key, Interest>()
		
		for(Interest i: interestList)
			interestMap.put(i.id, i)
		
		if(params.save) {
			def em = EntityManagerFactoryUtils.
				getTransactionalEntityManager(entityManagerFactory)
			nl.setInterests(params.get("interests[]"))
			nl.curatorInfo = new Text(params.curatorInfo?.trim())
			em.merge(nl)
		}

		render(view:'edit', model:[
			newsletter: nl,
			user:user,
			interestList:interestList, 
			interestMap:interestMap])
	}
	
	def addInterest = {
		if (!session.userEmail) {
			redirect(uri:'/')
			return
		}
		
		String newsletterId = params.newsletterId
		
		if (!newsletterId) {
			response.sendError(500)
			return
		}
		
		if (!params.save) {
			render(view:'addInterest', model:[params:params])
			return
		}
		
		if (!params.displayName || !params.url) {
			flash.message = "Please supply a display name and a feed URL"
			render(view:'addInterest', model:[params:params])
			return
		}
		
		User u = User.findByEmail(session.userEmail)
		Newsletter nl = Newsletter.findById(newsletterId)
		
		if (u && nl && nl.owner.equals(u)) {
			Interest interest = new Interest()
			interest.displayName = params.displayName
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.owner = u.id
			interest.maxStories = new Integer(params.maxStories)
			interest.maxWordsPerStory = 40
			interest.includeItemMoreLink = false
			interest.includeItemTitle = true
			
			String feedUrl = params.url
			//safari displays this in the browser: feed://www.buzzfeed.com/index.xml
			if (feedUrl.length() >= 4 && feedUrl.substring(0, 4).equals("feed")) 
				feedUrl = "http" + feedUrl.substring(4)
			
			Feed feed = Feed.findByUrl(feedUrl)
			
			if (!feed && !WebUtils.isValidFeed(feedUrl)) {
				flash.message = "Invalid feed URL"
				render(view:'addInterest', model:[newsletterId:newsletterId])
				return
			} else if(!interest.validate()){
				flash.message = "Please enter a display name"
				render(view:'addInterest', model:[newsletterId:newsletterId])
				return
			}

			//now create or find feed
			if (!feed) {
				feed = new Feed()
				feed.type = Feed.TYPE_GENERIC_RSS
				feed.url = feedUrl
				
				Feed.withTransaction() {
					feed.save(flush:true)
				}
			}
			interest.globalFeedId = feed.id	

			Interest.withTransaction() {
				interest.save(flush:true)
			}
					
			Newsletter.withTransaction() {
				nl.interests.add(interest.id)
				nl.merge(flush:true)
			}
					
			redirect(controller:'newsletter', action:'edit', params:[id:newsletterId])
			return
		} else {
			response.sendError(500)
			return
		}
	}
}
