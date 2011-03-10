package com.morningmail.services

import com.morningmail.domain.Feed;
import com.morningmail.domain.Email;
import com.morningmail.domain.Interest 
import com.morningmail.utils.DateUtils;
import com.morningmail.utils.WebUtils;
import com.morningmail.utils.TextUtils;
import com.google.appengine.api.datastore.Text
import org.jsoup.nodes.Document
import org.jsoup.Jsoup;

import java.util.Calendar;
import java.util.Date
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.SyndFeedInput;
import java.io.StringReader;

class RomeFeedService implements FeedService {
	
	private static final TITLE_PLACEHOLDER = "^{title}^"
	
	public void fetch(Feed feed) {  
		feed.raw = new Text(WebUtils.fetchUrl(feed.url))
		feed.lastUpdated = new Date()
	}
		
	public FeedService.FeedServiceHelper process(Feed feed, Interest interest, String  emailId) {
		log.info("Starting to process feed $feed.id . Feed was last updated: $feed.lastUpdated")
		SyndFeedInput input = new SyndFeedInput()
		
		StringBuffer html = new StringBuffer()
		StringBuffer text = new StringBuffer()
		
		try {
			SyndFeed syndFeed = input.build(new StringReader(feed.raw.getValue()))
			List entries = syndFeed.getEntries()
			
			int storyCount = 0;
			
			html.append("<div>")
			html.append("<b>").append(interest.displayName.toUpperCase()).append("</b><br/>")
			
			text.append(interest.displayName.toUpperCase()).append("\n")
			
			if(entries != null && !entries.isEmpty()) {
				//Iterate over our main elements. Should have one for each article
				for (SyndEntry entry : entries) {
					if (storyCount >= interest.maxStories)
						break
					
					if (DateUtils.isWithin24Hours(entry.getPublishedDate())) {
						
						String htmlTitle = new StringBuffer("<a href=\"")
							.append(WebUtils.encodeLink(interest.id, feed.id,  emailId, entry.getLink()))
							.append("\">")
							.append(entry.getTitle())
							.append("</a><br/>")
							.toString()
							
						String textTitle = new StringBuffer()
							.append(entry.getTitle())
							.append("\n")
							.toString()
						
						//there are some weird characters out there. Copied from MS word docs?
						textTitle = textTitle.replaceAll("Ô", "'")
						textTitle = textTitle.replaceAll("«", "'")
						textTitle = textTitle.replaceAll("Õ", "'")
						textTitle = textTitle.replaceAll("Ð", "-")
						textTitle = textTitle.replaceAll("Ò", "\"")
						textTitle = textTitle.replaceAll("Ó", "\"")
						
						
						Document doc
						try {
							doc = Jsoup.parse(entry.getDescription().getValue());
						} catch (NullPointerException e) {
							doc = Jsoup.parse(entry.getContents().get(0).getValue())
						}
			
						String description = doc.text()
						description = description.trim()
						
						if (interest.maxWordsPerStory != Interest.NO_MAX)
							description = TextUtils.getSummary(description, interest.maxWordsPerStory, true)
						
						if (interest.includeItemTitle) {
							html.append(htmlTitle)
							text.append(textTitle)
						}
						
						html.append(description)
//						text.append(description)
						
						if (interest.includeItemMoreLink) {
							html.append("<a href=\""+WebUtils.encodeLink(interest.id, feed.id,  emailId, entry.getLink())+"\">More</a>")
							text.append(entry.getLink())
						}
						
						html.append("<br/>")
						text.append("\n\n")
						
						storyCount++
					} else {
						Calendar cal = Calendar.getInstance();
						Date now = cal.getTime();
						long diff = now.getTime() - entry.getPublishedDate().getTime();
						
						log.info("Not within 24 hours:" + entry.getPublishedDate().toString())
						log.info("Difference is: " + diff)
					}
				}
			}
			
			html.append("</div>")
			
			if (storyCount == 0) {
				html = new StringBuffer()
				text = new StringBuffer()
			}
		} catch (Exception e) {
			log.error("Error parsing feed: $feed ", e)
			html = new StringBuffer()
			text = new StringBuffer()
		} finally {
			log.info("Finishing processing of $feed.id")
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
	}
}
