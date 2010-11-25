package com.morningmail.controller
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

class GlobalFeedController {

	def yahooNewsFeedService
	
	def index = {
		Queue queue = QueueFactory.getQueue("fetch-queue")
		queue.add(url("/globalFeed/yahooNews"))
		render(view:'index', model:[returnValue: "Fetch Complete"])
	}
	
	def yahooNews = {
		yahooNewsFeedService.fetch()
		render(view:'index', model:[returnValue: yahooNewsFeedService.getShortPlainText()])
	}
}
