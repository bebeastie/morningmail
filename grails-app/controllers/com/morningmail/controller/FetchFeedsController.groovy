package com.morningmail.controller

class FetchFeedsController {

	def fetchFeedsService
	
	def index = {
		fetchFeedsService.fetchFeeds()
		render(view:'index')
	}
}
