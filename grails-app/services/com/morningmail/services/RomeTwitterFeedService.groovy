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
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.mortbay.log.Log;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date
import java.util.Map;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.SyndFeedInput;
import java.io.StringReader;

class RomeTwitterFeedService implements FeedService {
	
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
				Comparator feedCompartor = new Comparator() {
					public int compare(Object o1, Object o2) {
						SyndEntry entry1 = (SyndEntry) o1;
						SyndEntry entry2 = (SyndEntry)
						return o1.getPublishedDate().compareTo(o2.getPublishedDate())
					}
					
					public boolean equals(Object obj) {
						return false;
					}
				}
				
				//sort oldest to newest
				Collections.sort(entries, feedCompartor);
				
				//Iterate over our main elements. Should have one for each article
				Map<String, String> allLinks = new HashMap<String, String>()
				
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
						
						Document doc = Jsoup.parse(entry.getContents().get(0).getValue())
			
						Elements entryLinks = doc.getElementsByTag("a");
						
						if (!entryLinks || entryLinks.size() == 0 || 
								allLinks.containsKey(entryLinks.get(0).attr("href")))
							break
						
						String link = entryLinks.get(0).attr("href")
						//TODO - WAS HERE - HOW DO YOU WANT TO HANDLE THESE?

						String author = "@" + entry.getAuthor().split('(')[0].trim() + ":"

						String description = doc.text()
						description = description.trim()
												
						html.append(description)
						text.append(description)

						
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
