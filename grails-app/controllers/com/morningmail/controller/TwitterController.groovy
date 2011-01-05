package com.morningmail.controller

import com.morningmail.domain.User
import com.morningmail.domain.OAuthToken

class TwitterController {
	
	def twitterService
	
	def index = {
		render(view:'index', model:[returnValue:"OK"])
	}
	
	def generate = {
		if (!session.userEmail) {
			redirect(uri:'/')
			return
		}
		
		User u = User.findByEmail(session.userEmail)
		
		OAuthToken token = twitterService.generateRequestToken(u)
		
		render(view:'index', model:[returnValue:token.authorizationUrl])
	}
		
	def callback = {
		twitterService.upgradeToken(params.oauth_token, params.oauth_verifier)
		render(view:'index', model:[returnValue: "OK"])
	}
	
	def timeline = {
		if (!session.userEmail) {
			redirect(uri:'/')
			return
		}
		
		User u = User.findByEmail(session.userEmail)
		twitterService.getFeed(u)
		System.out.println("Back in the controller...")
		render(view:'index', model:[returnValue: "OK"])
	}
	

}
