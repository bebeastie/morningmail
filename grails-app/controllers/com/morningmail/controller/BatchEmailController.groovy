package com.morningmail.controller

import com.morningmail.domain.*;
import com.morningmail.domain.Email;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;


class BatchEmailController {

	def batchEmailService
	
	def prepare = {
		
		ArrayList<User> users = batchEmailService.getUsersToRender()
		
		String renderedUsers = "Users:<br/>"
		for (User u: users) {
			renderedUsers+=u+"<br/>"
		}
		
		render(view:'index', model:[returnValue:renderedUsers])
		
//		def users = User.list()
//		
//		if (users) {		
//			Queue queue = QueueFactory.getDefaultQueue()
//
//			for(User user: users) {
//				queue.add(url("/email/fetchAndRenderAsync/$user.id"))
//			}
//		}
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
