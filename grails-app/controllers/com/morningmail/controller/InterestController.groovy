package com.morningmail.controller

import com.morningmail.domain.*
import com.google.appengine.api.datastore.KeyFactory

class InterestController {

	def create = {
		if (!session.userEmail) {
			redirect(uri:'/')
			return
		}
		
		Interest interest
		
		if (params.save) {
			interest = new Interest()
			
			User u = User.findByEmail(session.userEmail)
			
			interest.displayName = params.displayName
			interest.feedStyle = Interest.FEED_STYLE_GLOBAL
			interest.owner = u.id
			interest.maxStories = new Integer(params.maxStories)
			interest.maxWordsPerStory = 40
			interest.includeItemMoreLink = false
			interest.includeItemTitle = true
			
			System.out.println("Max Stories: " + interest.maxStories)
			if (interest.validate()) {
				//now create or find feed
				Feed feed = Feed.findByUrl(params.url)
				if (!feed) {
					Feed.withTransaction() {
						feed = new Feed()
						feed.type = Feed.TYPE_GENERIC_RSS
						feed.url = params.url
						feed.save(flush:true)
					}

				}		
				interest.globalFeedId = feed.id
				interest.save()
				redirect(controller:'user', action:'personalize')
				return
			}
		} 
		render(view:"create", model:[interest:interest])
	}
}
