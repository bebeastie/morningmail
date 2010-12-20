package com.morningmail.controller

import com.morningmail.domain.*
import java.net.URLEncoder

class ReadLaterController {

	def add = {
		if (!session.userEmail) {
			def jump = request.forwardURI + "?" + request.queryString
			
			//force the user to login first
			redirect(controller:"user", action:"login", 
				params:["jump":URLEncoder.encode(jump, "UTF-8"),
					 "display":UserController.LOGIN_DISPLAY_MINIMAL])
			return
		}
		
		User user = User.findByEmail(session.userEmail)
		
		ReadLaterItem readLaterItem = new ReadLaterItem()
		readLaterItem.user = user
		readLaterItem.title = params.title
		readLaterItem.url = params.url
		readLaterItem.description = params.description
		readLaterItem.savedDate = Calendar.getInstance().getTime()
		
		if (readLaterItem.validate()) {
			readLaterItem.save()
			render(view:"save", model:[readLaterItem:readLaterItem])
		} else {
			render(view:"error")
		}
	}
	
}
