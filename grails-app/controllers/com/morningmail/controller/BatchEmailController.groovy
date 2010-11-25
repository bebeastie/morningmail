package com.morningmail.controller

import com.morningmail.domain.*;
import com.morningmail.domain.Email;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

class BatchEmailController {

	def prepare = {
		Date now = Calendar.getInstance()
		
		def users = User.list()
		
		if (users) {		
			Queue queue = QueueFactory.getDefaultQueue()

			for(User user: users) {
				queue.add(url("/email/fetch/$user.id"))
			}
		}
	}
	
	def render = {
		Date now = Calendar.getInstance()
		
		def users = User.list()
		
		if (users) {
			Queue queue = QueueFactory.getDefaultQueue()

			for(User user: users) {
				queue.add(url("/email/render/$user.id"))
			}
		}
	}
	
	def send = {
		def emails = Email.findByStatus(Email.STATUS_PENDING)
		
		if (emails) {
			Queue queue = QueueFactory.getDefaultQueue()
			
			for(Email email: emails) {
				queue.add(url("/email/send/$email.id"))
			}
		}
	}
}
