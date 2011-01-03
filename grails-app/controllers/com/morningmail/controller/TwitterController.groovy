package com.morningmail.controller

import com.morningmail.domain.User
import com.morningmail.domain.OAuthToken

class TwitterController {
	
	def twitterUserTimelineService
	
	def index = {
		render(view:'index', model:[returnValue:"OK"])
	}
	
	def generate = {
		if (!session.userEmail) {
			redirect(uri:'/')
			return
		}
		
		User u = User.findByEmail(session.userEmail)
		
		OAuthToken token = twitterUserTimelineService.generateRequestToken(u)
		
		render(view:'index', model:[returnValue:token.authorizationUrl])
	}
		
	def callback = {
		twitterUserTimelineService.upgradeToken(params.oauth_token, params.oauth_verifier)
		render(view:'index', model:[returnValue: "OK"])
	}
	
	def timeline = {
		if (!session.userEmail) {
			redirect(uri:'/')
			return
		}
		
		User u = User.findByEmail(session.userEmail)
		twitterUserTimelineService.getFeed(u)
		render(view:'index', model:[returnValue: "OK"])
	}
	

}