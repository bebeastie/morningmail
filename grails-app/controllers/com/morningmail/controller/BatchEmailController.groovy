package com.morningmail.controller

import com.morningmail.domain.*;
import com.morningmail.domain.Email;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

class BatchEmailController {

	def batchEmailService
	
	def index = {
		Queue queue = QueueFactory.getDefaultQueue()
		queue.add(url("/batchEmail/prepare/"))
		queue.add(url("/batchEmail/send/"))
		
		render(view:'index', model:[returnValue:"Batch prepare and send complete."])
	}
	
	def prepare = {
		List<String> userKeys = batchEmailService.getUsersToRender()
		
		for (String key: userKeys) {
			log.info("Prepare and render email for user: " + key)
			Queue queue = QueueFactory.getDefaultQueue()
			queue.add(url("/email/fetchAndRenderAsync/" + key))
		}
		
		String summary = "Queued up rendering of " + userKeys.size() + " emails"
		log.info(summary)
		
		render(view:'index', model:[returnValue:summary])
	}
	
	def send = {
		List<String> emailKeys = batchEmailService.getEmailsToSend()

		for(String key: emailKeys) {
			log.info("Send email with ID: " + key)
			Queue queue = QueueFactory.getQueue("mail-queue")
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
