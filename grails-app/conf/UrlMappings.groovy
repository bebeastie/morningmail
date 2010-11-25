class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/email/fetch/$id" {
			controller = "email"
			action = "fetch"
		 }
		
		"/email/render/$id" {
			controller = "email"
			action = "render"
		 }
		
		"/email/send/$id" {
			controller = "email"
			action = "send"
		 }
		
		"/email/fetchAndRenderAsync/$id" {
			controller = "email"
			action = "fetchAndRenderAsync"
		 }
		
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
