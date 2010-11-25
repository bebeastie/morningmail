package com.morningmail.controller

import com.morningmail.domain.*;
import com.morningmail.domain.Email;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

class BatchEmailController {

	def batchEmailService
	
	def prepare = {
		
		List<String> userKeys = batchEmailService.getUsersToRender()
		
		
		String prepareUsers = "Users:<br/>"
		for (String key : userKeys) {
			prepareUsers+= "User: " + key + "<br/>"
			Queue queue = QueueFactory.getDefaultQueue()
			queue.add(url("/email/fetchAndRenderAsync/" + key))
		}
		
		render(view:'index', model:[returnValue:prepareUsers])
	}
	
	def send = {
		def emails = Email.findByStatus(Email.STATUS_PENDING)
		
		if (emails) {
			Queue queue = QueueFactory.getQueue("mail-queue")
			
			for(Email email: emails) {
				queue.add(url("/email/send/$email.id"))
			}
		}
	}
}
