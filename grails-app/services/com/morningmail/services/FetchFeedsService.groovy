package com.morningmail.services

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

class FetchFeedsService {
	public void fetchFeeds() {
		//pull yahoo news
		Queue queue = QueueFactory.getDefaultQueue()
		queue.add(url("/yahooNewsFeed/fetch"))
	}	
}
