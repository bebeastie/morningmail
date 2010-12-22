package com.morningmail.controller

import com.morningmail.domain.*;
import com.morningmail.services.*;
import com.morningmail.utils.DateUtils;
import java.text.ParseException;
import com.google.appengine.api.datastore.KeyFactory;
import java.net.URLDecoder

class UserController {
	static allowedMethods = [completeGoogAuth: ["POST", "GET"]]
	
	static final String LOGIN_DISPLAY_MINIMAL = "login_minimal"
	
	def googleCalendarService
	
	/**
	 * Called when we create a user
	 */
	def register = {	
		User user
		
		try {
			user = User.findByEmail(params.regEmail)
			
			if(!user) {
				user = new User()
				user.lastRenderedDate = new Date(0);
				user.name = params.name
				user.email = params.regEmail
				user.zipCode = params.zipCode
				user.localDeliveryTime = params.deliveryTime
				user.timeZone = params.timeZone
				user.password = params.regPassword
				
				user.deliveryTime = DateUtils.
					getNormalizedDeliveryTime(user.localDeliveryTime, DateUtils.getOffsetTimeZone(user.timeZone))
					
				if (user.validate() && "tufts".equals(params.inviteCode)) {
					user.id = KeyFactory.createKey(User.class.getSimpleName(), user.email)
					user.save()
					session.userEmail = user.email
					redirect(action:'personalize', model:[user:user])
				} 
			}
		} catch (Exception e) {
			log.error("Error processing registration", e)
		}
		render(view:'/index', model:[user:user])
		return
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
				
		if(params.saveInterests) {
			user.interests.clear()
			
			def interests = params.get("interests[]")
			if (interests) {
				//there could be only one interest
				//so we need to check if we should 
				//loop through or just take the one we have
				if (interests instanceof String) {
					user.interests.add(KeyFactory.stringToKey(interests))
				} else {
					for (String interest: interests)
						user.interests.add(KeyFactory.stringToKey(interest))
				}
			}
		} 

	 
		render(view:'personalize', model:[user:user])
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
