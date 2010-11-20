package com.morningmail.controller

import com.morningmail.domain.*;
import com.morningmail.services.*;
class SignupController {
	static allowedMethods = [completeGoogAuth: ["POST", "GET"]]
	
	def googleCalendarService
	
	/**
	 * Called when we create a user
	 */
	def index = {
		User user = new User(params)
		
		if (user.validate()) {
			user.save()
			session.user = user
			redirect(action:'personalize', model:[user:user])
		} else {
			render(view:'index', model:[user:user])
		}
	}
	
	
	/**
	 * Called once a user is created and they would like
	 * to personalize the content they have
	 */
	def personalize = {
		if (!session.user) {
			redirect(action:'index')
		}
		
		User user = session.user
		String authUrl = "#"
		String calendars = "No access"
		
		if (googleCalendarService.getAccessToken(user)) {
			googleCalendarService.fetch(user)
			calendars = googleCalendarService.getHtml(user)
		} else {
			authUrl = googleCalendarService.generateRequestTokenAndUrl(user, GoogleCalendarService.CALLBACK)
		}	
		
		render(view:'personalize', model:[user:user, authUrl: authUrl, calendars: calendars, oauth_token: params.oauth_token, oauth_verifier: params.oauth_verifier])
	}

	/**
	 * Called by Google once the user grants or denies access
	 */
	def completeGoogleAuth = {
		googleCalendarService.upgradeRequestToken(params.oauth_token, params.oauth_verifier)
		redirect(action:'personalize', params: params)
	
	}
	


}
