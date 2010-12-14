package com.morningmail.controller

import com.morningmail.domain.*;
import com.morningmail.services.*;
import com.morningmail.utils.DateUtils;
import java.text.ParseException;
import com.google.appengine.api.datastore.KeyFactory;

class LoginController {
	static allowedMethods = [completeGoogAuth: ["POST", "GET"]]
	
	def googleCalendarService
	
	/**
	 * Called when we create a user
	 */
	def register = {	
		User user
		
		try {
			user = User.findByEmail(params.email)
			
			if(!user) {
				user = new User()
				user.lastRenderedDate = new Date(0);
				user.name = params.name
				user.email = params.email
				user.zipCode = params.zipCode
				user.localDeliveryTime = params.deliveryTime
				user.timeZone = params.timeZone
				user.password = params.password
				
				user.deliveryTime = DateUtils.
					getNormalizedDeliveryTime(user.localDeliveryTime, DateUtils.getOffsetTimeZone(user.timeZone))
					
				if (user.validate() && "tufts".equals(params.inviteCode)) {
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
		User user
		
		try {
			user = User.findByEmail(params.email2)
			
			//@TODO store encrypted passwords
			if (user && user.password.equals(params.password2)) {
				session.userEmail = user.email
				redirect(action:'personalize', model:[user:user])
			}
		} catch (Exception e) {
			log.error("Error processing login", e)
		}
		render(view:'/index')
	}
	
	
	/**
	 * Called once a user is created and they would like
	 * to personalize the content they have
	 */
	def personalize = {
		if (!session.userEmail) {
			redirect(uri:'/')
		}
				
		User user = User.findByEmail(session.userEmail)
				
		if(params.saveInterests) {
			user.interests.clear()
			
			def interests = params.get("interests[]")
			if (interests) {
				for (String interest: interests)
					user.interests.add(KeyFactory.stringToKey(interest))
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
			System.out.println("Interest: " + interest.type)
		}
		
		render(view:'index')
	}


}
