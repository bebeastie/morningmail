package com.morningmail.controller

import com.morningmail.domain.*;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;
import com.morningmail.services.FeedService;
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.KeyFactory;

class GlobalFeedController {

	FeedService globalFeedService

	def index = {	
		def feeds = Feed.list()
		
		Queue queue = QueueFactory.getQueue("fetch-queue")
		
		for(Feed feed in feeds) {
			queue.add(url("/globalFeed/fetch/"+KeyFactory.keyToString(feed.id)))
		}

		render(view:'index', model:[returnValue: "Fetch Complete"])
	}
	
	def fetch = {
		def feed
		
		if (params.id) {
			try {
				feed = Feed.findById(KeyFactory.stringToKey(params.id))
			} catch (Exception e) {
				log.error("Error looking up feed ", e)
			}
		}
		
		if (feed) {
			globalFeedService.fetch(feed)
			log.info("Just fetched for: $feed.id . Last updated date is now: $feed.lastUpdated")
			render(view:'index', model:[returnValue: "Fetch Complete"])
		} else {
			log.error("Couldn't find feed with id: $params.id")
			render(view:'index', model:[returnValue: "ERROR: Couldn't find feed with id: $params.id"])
		}
	}
}
