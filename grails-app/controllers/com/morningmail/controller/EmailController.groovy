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
		
		String display
		
		if (nl.subscribers.contains(u.id)) {
			Email email = emailService.render(nl,u)
			display = email.html.getValue()
		} else {
			display = "Attempted to send a newsletter to a user that is not subscribed!";
			log.error(display)
		}
		render(view:'render', model:[returnValue:display])
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
		String decodedUrl
		try {
			decodedUrl = URLDecoder.decode(params.url, "UTF-8")
		} catch (Exception e) {
			log.error("Had trouble decoding the URL.",e)
		}
		
		try {
			LinkClick lc = new LinkClick()
			Email email = Email.findById(KeyFactory.stringToKey(params.email))
			lc.email = email.id
			lc.user = email.user.id
			lc.interest = KeyFactory.stringToKey(params.interest)
			lc.feed = KeyFactory.stringToKey(params.feed)
			lc.url = decodedUrl
			lc.timeClicked = new Date();
			if (lc.validate()) {
				lc.save()
			} else {
				log.error("Had trouble saving LinkClick" + lc.errors.allErrors);
			}
		} catch (Exception e) {
			log.error("Had trouble creating LinkClick." +e)
		}
		
		if (decodedUrl) 
			redirect(uri:decodedUrl)
		else 
			render(view:"forwardError")
		
	}
	
	def pixel = {
		String id = params.id
		
		System.out.println("Id: " + id)
		BufferedImage pixel;
		pixel = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		pixel.setRGB(0, 0, (0xFF));

		response.setContentType("image/png");
		OutputStream os = response.getOutputStream();
		ImageIO.write(pixel, "png", os);
	}
}
