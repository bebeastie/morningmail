package com.morningmail.controller

import com.google.appengine.api.datastore.KeyFactory;
import com.morningmail.domain.User;

class DashboardController {
	
	def index = {
		if (!session.userEmail) {
			redirect(uri:'/')
			return
		}
		
		User user = User.findByEmail(session.userEmail)
				
		if (user.subscriptions.size() == 1 && 
				user.newsletters.size() == 1) {
			redirect(controller:'newsletter', action:'edit', 
				params:[id:KeyFactory.keyToString(user.newsletters.get(0).id)])
		} else {
			
		}
	}	
}
