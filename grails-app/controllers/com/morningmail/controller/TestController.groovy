package com.morningmail.controller

import com.morningmail.domain.*;

class TestController {
	static allowedMethods = [index : ["POST","GET"]]
	
	def index = {
		User user = new User(firstName:"Bob") 
		user.save()
		 
		render(view:'test')  
	}
	
}
 