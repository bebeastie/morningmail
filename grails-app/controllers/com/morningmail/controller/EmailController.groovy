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
	
	/**
	 * Fetches feeds specific to this newsletter
	 */
	def fetch = {
		Newsletter nl = Newsletter.findById(KeyFactory.stringToKey(params.newsletterId))
		emailService.fetchPersonalFeeds(nl)
		render(view:'index', model:[returnValue:"Fetch Complete"])
	}
	
	/**
	 * Queues up rendering of a newsletter for a specific user
	 */
	def render = {
		Newsletter nl = Newsletter.findById(KeyFactory.stringToKey(params.newsletterId))
		User u = params.userId ? User.findById(KeyFactory.stringToKey(params.userId)) : nl.owner
		String returnValue
		
		if (nl.subscribers.contains(u.id)) {
				Email email = emailService.render(nl,u)
				email.save();
				returnValue = email.html.getValue()
		} else {
			returnValue = "Attempted to send a newsletter to a user that is not subscribed!";
			log.error(returnValue)
		}
		render(view:'render', model:[returnValue:returnValue])
	}
	
	/**
	 * Sends an email
	 */
	def send = {
		Email e = Email.findById(KeyFactory.stringToKey(params.emailId))
		emailService.send(e)
		render(view:'index', model:[returnValue:"Send Complete"])
	}
	
	def keepAlive = {
		render(view:"index", model:[returnValue:"Keep Alive"])
	}
	
	
	/**
	 * Used to display emails in a web browser
	 */
	def view = {
		String emailId = params.emailId
		String emailHtml = new String("")
		if (emailId) {
			try {
				Email email = Email.findById(KeyFactory.stringToKey(emailId))
				emailHtml = email.html.getValue()
			} catch (Exception e) {
				log.error("Error viewing email with ID $emailId ", e)
				emailHtml = ""
			}
		}
		render(view:'view', model:[emailHtml:emailHtml])
	}
	
	
	/**
	 * Records a link click and forwards to the appropriate site. This is called
	 * when a user clicks a link in an email.
	 */
	def link = {
		
		try {
			LinkClick lc = new LinkClick()
			Email email = Email.findById(KeyFactory.stringToKey(params.email))
			lc.email = email.id
			lc.user = email.user.id
			lc.interest = KeyFactory.stringToKey(params.interest)
			lc.feed = KeyFactory.stringToKey(params.feed)
			lc.url = params.url
			lc.timeClicked = new Date();
			if (lc.validate()) {
				lc.save()
			} else {
				log.error("Had trouble saving LinkClick" + lc.errors.allErrors);
			}
		} catch (Exception e) {
			log.error("Had trouble creating LinkClick." +e)
		}
		
		if (params.url) {
			log.info("Redirecting to: $params.url")
			redirect(uri:params.url)
		} else { 
			log.error("url is null, cannot forward");
			render(view:"forwardError")
		}
		
	}
}
