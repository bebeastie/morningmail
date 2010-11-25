package com.morningmail.controller

import com.morningmail.domain.*;
import com.morningmail.services.*;
import com.morningmail.utils.DateUtils;
import java.text.ParseException;

class SignupController {
	static allowedMethods = [completeGoogAuth: ["POST", "GET"]]
	
	static List<String> deliveryTimes = new ArrayList<String>();
	static {
		deliveryTimes.add("5:30 AM")
		deliveryTimes.add("6:00 AM")
		deliveryTimes.add("6:30 AM")
		deliveryTimes.add("7:00 AM")
		deliveryTimes.add("7:30 AM")
		deliveryTimes.add("8:00 AM")
		deliveryTimes.add("8:30 AM")
		deliveryTimes.add("9:00 AM")
		deliveryTimes.add("9:30 AM")
		deliveryTimes.add("10:00 AM")
		deliveryTimes.add("10:30 AM")
	}
	
	
	def googleCalendarService
	
	/**
	 * Called when we create a user
	 */
	def index = {
		User user
		
		if (params.email)
			user = User.findByEmail(params.email)
	
		if (!user) {
			user = new User()
			user.lastRenderedDate = new Date(0);
		}
		
		user.name = params.name
		user.email = params.email
		user.zipCode = params.zipCode
		user.localDeliveryTime = params.deliveryTime
		user.timeZone = params.timeZone
		
		//@TODO this is just to set a default
		if (!params.deliveryTime) {
			user.localDeliveryTime = "8:00 AM"
			user.timeZone = "Eastern"
		}
		
		try {
			user.deliveryTime = DateUtils.
				getNormalizedDeliveryTime(user.localDeliveryTime, DateUtils.getOffsetTimeZone(user.timeZone))
		} catch (ParseException e) {
			log.error("Invalid deliveryTime", e);
		}
		
		if (user.validate() && "tufts".equals(params.inviteCode)) {
			if (!user.id) 
				user.save()
			session.userEmail = user.email
			redirect(action:'personalize', model:[user:user])
		} else {
			render(view:'index', model:[user:user, deliveryTimes:deliveryTimes, timeZones:DateUtils.TIME_ZONES])
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
