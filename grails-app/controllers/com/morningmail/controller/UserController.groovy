package com.morningmail.controller

import com.morningmail.domain.*;
import com.morningmail.services.*;
import com.morningmail.utils.DateUtils;
import java.text.ParseException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.net.URLDecoder

class UserController {
	static allowedMethods = [completeGoogAuth: ["POST", "GET"]]
	
	static final String LOGIN_DISPLAY_MINIMAL = "login_minimal"
	
	def googleCalendarService
	def interestService

	/**
	 * Called when we create a user
	 */
	def register = {	
		User user = new User()
		def existingUser = false
		
		if (params.regEmail)
			existingUser = User.findByEmail(params.regEmail)
	
		user.lastRenderedDate = new Date(0);
		user.name = params.name
		user.email = params.regEmail
		user.zipCode = params.zipCode
		user.localDeliveryTime = params.deliveryTime
		user.timeZone = params.timeZone
		user.password = params.regPassword
		
		try {
			user.deliveryTime = DateUtils.
				getNormalizedDeliveryTime(user.localDeliveryTime, DateUtils.getOffsetTimeZone(user.timeZone))
		} catch (Exception e) {
			//do nothing, the User domain object will throw a validation error	
		}
		
		boolean correctCode = "tufts".equals(params.inviteCode)
				
		if (!correctCode)
			user.errors.rejectValue('', 'Uh oh, wrong invite code')
			
		if (existingUser) 
			user.errors.rejectValue('', 'That email address is already registered')
			
		if (user.validate() && correctCode && !existingUser) {
			user.id = KeyFactory.createKey(User.class.getSimpleName(), user.email)
			user.save()
			session.userEmail = user.email
			redirect(action:'personalize', model:[user:user])
			return
		}
			
		render(view:'/index', model:[user:user, inviteCode:params.inviteCode])
	}
	
	def login = {
		String view = "/index"
		
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
						redirect(action:'personalize', model:[user:user])
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
		redirect(uri:'/')
		return
	}
	
	
	/**
	 * Called once a user is created and they would like
	 * to personalize the content they have
	 */
	def personalize = {
		if (!session.userEmail) {
			redirect(uri:'/')
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
	
	def printInterests = {
		if (!session.user) {
			redirect(action:'index')
		}
		
		User user = session.user
		
		System.out.println("Interests for user: " + user.id)
		for (Interest interest : user.interests) {
			System.out.println("Interest: " + interest.systemName)
		}
		
		render(view:'index')
	}


}
