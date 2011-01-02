package com.morningmail.controller

import com.morningmail.domain.User
import com.morningmail.domain.Newsletter
import com.morningmail.domain.Email
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

class EmailController {

	def emailService
	
	def fetch = {
		Newsletter nl = Newsletter.findById(KeyFactory.stringToKey(params.id))
		emailService.fetchPersonalFeeds(nl)
		render(view:'index', model:[returnValue:"Fetch Complete"])
	}
	
	def render = {
		Newsletter nl = Newsletter.findById(KeyFactory.stringToKey(params.id))
		Email email = emailService.render(nl)
		render(view:'index', model:[returnValue:email.html.getValue()])
	}
	
	def send = {
		Email e = Email.findById(KeyFactory.stringToKey(params.id))
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
	
	def keepAlive = {
		render(view:"index", model:[returnValue:"Keep Alive"])
	}
}
