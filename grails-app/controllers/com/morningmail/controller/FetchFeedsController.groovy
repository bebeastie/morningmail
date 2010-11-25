package com.morningmail.controller

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

class FetchFeedsController {

	def index = {
		Queue queue = QueueFactory.getQueue("fetch-queue")
		queue.add(url("/yahooNewsFeed/fetch"))
		render(view:'index')
	}
}
