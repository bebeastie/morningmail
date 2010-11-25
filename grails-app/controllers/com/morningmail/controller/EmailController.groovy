package com.morningmail.controller

import com.morningmail.domain.User
import com.morningmail.domain.Email
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

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
	
	def fetchAndRenderAsync = {
		//first do the fetch
		Queue fetchQueue = QueueFactory.getQueue("fetch-queue")
		fetchQueue.add(url("/email/fetch/" + params.id))
		
		//then do the render, we're assuming it will be ready in 45 sec
		Queue renderQueue = QueueFactory.getQueue("render-queue")
		renderQueue.add(url("/email/render/" + params.id).countdownMillis(45000))	
		
		render(view:'index', model:[returnValue:"fetchAndRenderAsync Complete"])
	}
}
