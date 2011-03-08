package com.morningmail.controller

import com.morningmail.domain.*;
import com.morningmail.domain.Email;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.KeyFactory

class BatchEmailController {

	def batchEmailService
	
	def index = {
		Queue queue = QueueFactory.getDefaultQueue()
		queue.add(url("/batchEmail/prepare/"))
		queue.add(url("/batchEmail/send/"))
		
		render(view:'index', model:[returnValue:"Batch prepare and send complete."])
	}
	
	def prepare = {
		List<Newsletter> newsletters = batchEmailService.getNewslettersToRender()
		
		int totalEmails = 0
		
		for (Newsletter nl: newsletters) {
			log.info("Prepare and render email for newsletter: " + nl.id)
			Queue queue = QueueFactory.getQueue("mail-prepare-queue")
			queue.add(url("/email/fetch/" + nl.id))
			totalEmails+=nl.subscribers.size()
			
			for(Key k: nl.subscribers) {
				queue.add(url("/email/render/" + nl.id + "/" 
					+ KeyFactory.keyToString(k)))
			}
		}
		
		StringBuffer summary = new StringBuffer()
			.append("Queued up rendering of " + newsletters.size() + " newletters ")
			.append("for a total of " + totalEmails + " emails.")
		log.info(summary.toString())
		
		render(view:'index', model:[returnValue:summary.toString()])
	}
	
	def send = {
		List<String> emailKeys = batchEmailService.getEmailsToSend()

		for(String key: emailKeys) {
			log.info("Send email with ID: " + key)
			Queue queue = QueueFactory.getQueue("mail-send-queue")
			queue.add(url("/email/send/" + key))
		}
		
		String summary = "Queued up delivery of " + emailKeys.size() + " emails"
		log.info(summary)
		
		render(view:'index', model:[returnValue:summary])
	}
	
	def keepAlive = {
		render(view:"index", model:[returnValue:"Keep Alive"])
	}
}
