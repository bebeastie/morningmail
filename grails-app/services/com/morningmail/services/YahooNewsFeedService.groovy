package com.morningmail.services

import com.morningmail.services.FeedService;
import com.morningmail.domain.Feed;
import com.morningmail.domain.Email;
import com.morningmail.domain.Interest;
import com.morningmail.utils.WebUtils;
import javax.persistence.*;
import com.google.appengine.api.datastore.Text
import org.jsoup.nodes.Document
import org.jsoup.Jsoup;
import com.morningmail.utils.TextUtils
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.SyndFeedInput;
import java.io.StringReader;
import com.morningmail.utils.WebUtils;

class YahooNewsFeedService implements FeedService {

	public void fetch(Feed feed) {
		feed.raw = new Text(WebUtils.fetchUrl(feed.url))
		feed.lastUpdated = new Date()
	}
	
	public FeedService.FeedServiceHelper process(Feed feed, Interest interest, String emailId) {
		try {
			SyndFeedInput input = new SyndFeedInput()
			SyndFeed syndFeed = input.build(new StringReader(feed.raw.getValue()))
		
			List entries = syndFeed.getEntries()
			
			StringBuffer html = new StringBuffer()
			StringBuffer text = new StringBuffer()
			
			html.append("<div>")
			html.append("<b>").append(interest.displayName.toUpperCase()).append("</b><br/>")
			
			text.append(interest.displayName.toUpperCase()).append("\n")
			
			int storyCount = 0;
			
			if(entries != null && !entries.isEmpty()) {
				//Iterate over our main elements. Should have one for each article		
				for (SyndEntry entry : entries) {
					if (storyCount >= interest.maxStories) 
						break
						
					//html
					String iTitle = entry.getTitle()
					iTitle = iTitle.replaceAll("\\n","");
					iTitle = iTitle.replaceAll("\\t","");
					iTitle = iTitle.replaceAll("\\(AP\\)","");
					iTitle = iTitle.replaceAll("\\(Reuters\\)","");
					iTitle = iTitle.trim()
					
					String htmlTitle = new StringBuffer("<a href=\"")
						.append(WebUtils.encodeLink(interest.id, feed.id,  emailId, entry.getLink()))
						.append("\">")
						.append(iTitle)
						.append("</a><br/>")
						.toString()
						
					String textTitle = new StringBuffer()
						.append(iTitle)
						.append("\n")
						.toString()
					
	
					Document doc = Jsoup.parse(entry.getDescription().getValue());
					
					String description = doc.text()
					
					if (interest.maxWordsPerStory != Interest.NO_MAX)
						description = TextUtils.getSummary(description, interest.maxWordsPerStory, true)
					
					description = description.trim()
										
					if (interest.includeItemTitle) {
						html.append(htmlTitle)
						text.append(textTitle)
					}
					
					html.append(description)
					text.append(description)
					
					if (interest.includeItemMoreLink) { 
						html.append("<a href=\""+WebUtils.encodeLink(interest.id, feed.id,  emailId, entry.getLink())+"\">More</a>")
						text.append(entry.getLink())
					}
					
					html.append("<br/>")
					text.append("\n\n")
					
					storyCount++
				}
			}
			
			html.append("</div>")
			
			if (storyCount == 0) {
				html = new StringBuffer()
				text = new StringBuffer()
			}
			
			FeedService.FeedServiceHelper fsHelper = new FeedService.FeedServiceHelper() {
				@Override
				public String getPlainText() {
					return text.toString()
				}
				
				@Override
				public String getHtml() {
					return html.toString()
				}
			};
			
		} catch(Exception e) {
			log.error("Problem parsing feed", e)
		}
	}
}
