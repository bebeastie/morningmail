package com.morningmail.controller

import com.morningmail.domain.*;
import com.morningmail.services.*;
import com.morningmail.utils.DateUtils;
import java.text.ParseException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.net.URLDecoder

import javax.persistence.EntityManager;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

class UserController {
	static allowedMethods = [completeGoogAuth: ["POST", "GET"]]
	
	static final String LOGIN_DISPLAY_MINIMAL = "login_minimal"
	
	def entityManagerFactory
	EntityManager em
	
	def googleCalendarService
	def interestService
	def newsletterService
	
	def index = {
		render(view:'index')
	}
	/**
	 * Called when we create a user
	 */
	def register = {	
		User user = new User()
		def existingUser = false
		
		if (params.regEmail)
			existingUser = User.findByEmail(params.regEmail)
	
		user.name = params.name
		user.email = params.regEmail
		user.zipCode = params.zipCode
		user.password = params.regPassword
		user.type = User.TYPE.STANDARD
		
		boolean correctCode = "tufts".equals(params.inviteCode)
				
		if (!correctCode)
			user.errors.rejectValue('', 'Uh oh, wrong invite code')
			
		if (existingUser) 
			user.errors.rejectValue('', 'That email address is already registered')
		
		Newsletter nl = Newsletter.create(
			user, params.newsletterName, params.deliveryTime, params.timeZone)
		
		boolean validUser = user.validate()
		boolean validNewsletter = nl.validate();	
		
		if (validUser && validNewsletter && correctCode && !existingUser) {
			user.id = KeyFactory.createKey(User.class.getSimpleName(), user.email)
			
			User.withTransaction() {
				user.save(flush:true)
			}
			
			session.userEmail = user.email
			
			params.title = params.newsletterName //this will be used to name the newsletter
			
			redirect(controller:'newsletter', action:'create', 
				params:params)
			return
		}
			
		render(view:'index', model:[user:user, newsletter:nl, inviteCode:params.inviteCode])
	}
	
	def login = {
		String view = "index"
		
		if (LOGIN_DISPLAY_MINIMAL.equals(params.display)) {
			view = LOGIN_DISPLAY_MINIMAL
		}
		
		if (params.email) {
			User user
			try {
				user = User.findByEmail(params.email)
				
				//@TODO store encrypted passwords
				if (user && user.password.equals(params.password)) {
					session.userEmail = user.email
					
					if (params.jump) {
						redirect(uri:URLDecoder.decode(params.jump, "UTF-8"))	
					} else {
						redirect(controller:'dashboard',
							action:'index', model:[user:user])
					}
					return
				}
			} catch (Exception e) {
				log.error("Error processing login", e)
			}
		
		} 
		
		render(view:view, model:[jump:params.jump,
			display:params.display])
	}
	
	def logout = {
		session.invalidate()
		redirect(uri:'/user')
		return
	}
	
	
	/**
	 * Called once a user is created and they would like
	 * to personalize the content they have
	 */
	def personalize = {
		if (!session.userEmail) {
			redirect(uri:'/user')
			return
		}
				 
		User user = User.findByEmail(session.userEmail)

		def interestList = interestService.getAll(user)
		def interestMap = new HashMap<Key, Interest>()
		for(Interest i: interestList) 
			interestMap.put(i.id, i)	
		
		if(params.saveInterests) {
			def interests = params.get("interests[]")
			if (interests) 
				interestService.setSelected(user, interests)
		} 

		render(view:'personalize', model:[user:user, 
			interestList:interestList, interestMap:interestMap])
	}

	/**
	 * Called by Google once the user grants or denies access
	 */
	def completeGoogleAuth = {
		googleCalendarService.upgradeRequestToken(params.oauth_token, params.oauth_verifier)
		redirect(action:'personalize', params: params)
	
	}
}
