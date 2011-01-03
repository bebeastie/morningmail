package com.morningmail.controller

import com.morningmail.domain.User
import com.morningmail.domain.Newsletter
import com.morningmail.domain.Email
import com.morningmail.domain.LinkClick
import com.morningmail.domain.Feed
import com.morningmail.domain.Interest
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
	
	def link = {
		String decodedUrl
		try {
			decodedUrl = URLDecoder.decode(params.url, "UTF-8")
		} catch (Exception e) {
			log.error("Had trouble decoding the URL.",e)
		}
		
		try {
			LinkClick lc = new LinkClick()
			Email email = Email.findById(KeyFactory.stringToKey(params.email))
			lc.email = email
			lc.user = email.user
			lc.interest = Interest.findById(KeyFactory.stringToKey(params.interest))
			lc.feed = Feed.findById(KeyFactory.stringToKey(params.feed))
			lc.url = decodedUrl
			lc.timeClicked = new Date();
			if (lc.validate())
				lc.save()
			else
				log.error("Had trouble saving LinkClick" + lc.errors.allErrors);
		} catch (Exception e) {
			log.error("Had trouble creating LinkClick.",e)
		}
		
		if (decodedUrl) 
			redirect(uri:decodedUrl)
		else 
			render(view:"forwardError")
		
	}
}
