package com.morningmail.services

import javax.persistence.EntityManager;
import com.morningmail.domain.Feed;
import com.morningmail.utils.DateUtils;
import com.morningmail.utils.TextUtils;
import com.google.appengine.api.datastore.Text
import org.jsoup.nodes.Document
import org.jsoup.Jsoup;
import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import java.net.URL;
import java.text.SimpleDateFormat
import java.text.DateFormat
import java.util.Calendar;
import java.util.Date
import java.util.TimeZone;
import java.io.IOException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.SyndFeedInput;
import java.io.StringReader;
import com.sun.syndication.io.XmlReader;

class RomeFeedService implements FeedService {
	private static final TITLE_PLACEHOLDER = "^{title}^"
	
	public void fetch(Feed feed) {  
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpGet httpget = new HttpGet(feed.url); 
//
//        // Create a response handler
//        ResponseHandler<String> responseHandler = new BasicResponseHandler();
//        String responseBody = httpclient.execute(httpget, responseHandler);
//
//        // When HttpClient instance is no longer needed, 
//        // shut down the connection manager to ensure
//        // immediate deallocation of all system resources
//        httpclient.getConnectionManager().shutdown(); 
//		
//		feed.raw = new Text(responseBody)
		parse(feed)
	}
	
	public String getHtml(Feed feed, String title) { 
		return feed.html.getValue().replaceFirst("\\Q"+TITLE_PLACEHOLDER+"\\E", title.toUpperCase())
	}
	
	public String getPlainText(Feed feed, String title) {
		return feed.plainText.getValue().replaceFirst("\\Q"+TITLE_PLACEHOLDER+"\\E", title.toUpperCase())
	}
	
	private parse(Feed feed) {
		SyndFeedInput input = new SyndFeedInput()
//		SyndFeed syndFeed = input.build(new StringReader(feed.raw.getValue()))
		SyndFeed syndFeed = input.build(new XmlReader(new URL(feed.url)))
		
		List entries = syndFeed.getEntries()
		
		StringBuffer html = new StringBuffer()
		StringBuffer text = new StringBuffer()
		
		html.append("<div>")
		html.append("<b>").append(TITLE_PLACEHOLDER).append("</b><br/>")
		
		text.append(TITLE_PLACEHOLDER).append("\n")
		
		int storyCount = 0;
		
		if(entries != null && !entries.isEmpty()) {
			//Iterate over our main elements. Should have one for each article
			for (SyndEntry entry : entries) {
				if (storyCount >= feed.maxStories)
					break
				
				if (DateUtils.isWithin24Hours(entry.getPublishedDate())) {
					
					String htmlTitle = new StringBuffer("<a href=\"")
						.append(entry.getLink())
						.append("\">")
						.append(entry.getTitle())
						.append("</a><br/>")
						.toString()
						
					String textTitle = new StringBuffer()
						.append(entry.getTitle())
						.append("\n")
						.toString()
					
					Document doc = Jsoup.parse(entry.getDescription().getValue());
					String description = doc.text()
					description = description.trim()
					
					if (feed.maxWordsPerStory != Feed.NO_MAX)
						description = TextUtils.getSummary(description, feed.maxWordsPerStory, true)
					
					if (feed.includeItemTitle) {
						html.append(htmlTitle)
						text.append(textTitle)
					}
					
					html.append(description)
					text.append(description)
					
					if (feed.includeItemMoreLink) {
						html.append("<a href=\""+entry.getLink()+"\">More</a>")
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
		
		
				
		feed.html = new Text(html.toString())
		feed.plainText = new Text(text.toString().trim())
		feed.title = syndFeed.getTitle()
		feed.description = syndFeed.getDescription()
		feed.lastUpdated = new Date()
	}
	
}
