package com.morningmail.controller

import com.morningmail.domain.*;
import com.morningmail.services.*;
class SignupController {
	static allowedMethods = [completeGoogAuth: ["POST", "GET"]]
//	static final List<String> deliveryTimes = new ArrayList<String>()[
//		"8:30 AM","8:45 AM","9:00 AM","9:15 AM"]
	
	def googleCalendarService
	
	/**
	 * Called when we create a user
	 */
	def index = {
		//if we don't have an email param send it back
		if (!params.email) {
			render(view:'index', model:[user:null])
			return
		}
		
		//see if we already have a user
		User user = User.findByEmail(params.email)
		
		if (!user) 
			user = new User()
		
		
		user.email = params.email
		user.zipCode = params.zipCode
		
		if (user.validate()) {
			if (!user.id) 
				user.save()
			session.userEmail = user.email
			redirect(action:'personalize', model:[user:user])
		} else {
			render(view:'index', model:[user:user])
			return
		}	
	}
	
	
	/**
	 * Called once a user is created and they would like
	 * to personalize the content they have
	 */
	def personalize = {
		if (!session.userEmail) {
			redirect(action:'index')
		}
				
		User user = User.findByEmail(session.userEmail)
				
		if(params.saveInterests) {
			user.interests.clear()
			if (params.get(Interest.TYPE_WEATHER))
				user.interests.add(Interest.findByType(Interest.TYPE_WEATHER).id)
			
			if (params.get(Interest.TYPE_TOP_NEWS)) 
				user.interests.add(Interest.findByType(Interest.TYPE_TOP_NEWS).id)
			
			if (params.get(Interest.TYPE_GOOGLE_CAL))
				user.interests.add(Interest.findByType(Interest.TYPE_GOOGLE_CAL).id)
	
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
