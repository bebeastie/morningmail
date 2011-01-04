class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/email/fetch/$newsletterId" {
			controller = "email"
			action = "fetch"
		 }
		
		"/email/render/$newsletterId/$userId?" {
			controller = "email"
			action = "render"
		 }
		
		"/email/send/$emailId" {
			controller = "email"
			action = "send"
		 }
			
		"/globalFeed/fetch/$id" {
			controller = "globalFeed"
			action = "fetch"
		}
		
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
