package com.morningmail.controller
import com.morningmail.domain.*;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;
import com.morningmail.services.FeedService;

class GlobalFeedController {

	FeedService globalFeedService

	def index = {	
		def feeds = Feed.list()
		
		Queue queue = QueueFactory.getQueue("fetch-queue")
		
		for(Feed feed in feeds) {
			queue.add(url("/globalFeed/fetch/$feed.id"))
		}

		render(view:'index', model:[returnValue: "Fetch Complete"])
	}
	
	def fetch = {
		def feed
		
		if (params.id)
			feed = Feed.findById(params.id)
		
		if (feed) {
			globalFeedService.fetch(feed)
			render(view:'index', model:[returnValue: "Fetch Complete"])
		} else {
			log.error("Couldn't find feed with id: $params.id")
			render(view:'index', model:[returnValue: "ERROR: Couldn't find feed with id: $params.id"])
		}
	}
}
