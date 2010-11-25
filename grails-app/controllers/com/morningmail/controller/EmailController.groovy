package com.morningmail.controller

import com.morningmail.domain.User
import com.morningmail.domain.Email

class EmailController {

	def emailService
	
	def fetch = {
		User u = User.findById(params.id)
		emailService.fetchPersonalFeeds(u)
		render(view:'index', model:[returnValue:"Fetch Complete"])
	}
	
	def render = {
		User u = User.findById(params.id)
		Email email = emailService.render(u)
		render(view:'index', model:[returnValue:email.contents.getValue()])
	}
	
	def send = {
		Email e = Email.findById(params.id)
		emailService.send(e)
		render(view:'index', model:[returnValue:"Send Complete"])
	}
}
