package com.morningmail.controller

class YahooNewsFeedController {

	def yahooNewsFeedService
	
	def fetch = {
		yahooNewsFeedService.fetch()
		render(view:'index', model:[html: yahooNewsFeedService.getHtml()])
	}
}
