package com.morningmail.controller
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

class GlobalFeedController {

	def yahooNewsFeedService
	def dictionaryWotdService
	
	def index = {
		Queue queue = QueueFactory.getQueue("fetch-queue")
		queue.add(url("/globalFeed/yahooNews"))
		queue.add(url("/globalFeed/dictionaryWotd"))
		render(view:'index', model:[returnValue: "Fetch Complete"])
	}
	
	def yahooNews = {
		yahooNewsFeedService.fetch()
		render(view:'index', model:[returnValue: yahooNewsFeedService.getShortPlainText()])
	}
	
	def dictionaryWotd = {
		dictionaryWotdService.fetch()
		render(view:'index', model:[returnValue: dictionaryWotdService.getShortPlainText()])
	}
}
