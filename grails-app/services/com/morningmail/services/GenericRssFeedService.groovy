package com.morningmail.services

import javax.persistence.EntityManager;
import com.morningmail.domain.Feed;
import com.sun.cnpi.rss.parser.RssParser;
import com.sun.cnpi.rss.parser.RssParserFactory;
import com.sun.cnpi.rss.elements.Rss;
import com.sun.cnpi.rss.elements.Item;
import com.sun.cnpi.rss.elements.PubDate;
import com.morningmail.utils.DateUtils;
import com.morningmail.utils.TextUtils;
import com.google.appengine.api.datastore.Text
import org.jsoup.nodes.Document
import org.jsoup.Jsoup;
import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat
import java.text.DateFormat
import java.util.Calendar;
import java.util.Date
import java.util.TimeZone;

class GenericRssFeedService implements FeedService {
	
	public void fetch(Feed feed) {
		RssParser parser = RssParserFactory.createDefault();
		Rss rss = parser.parse(new URL(feed.url));
		
		Collection items = rss.getChannel().getItems();
		
		StringBuffer html = new StringBuffer()
		StringBuffer text = new StringBuffer()
		
		html.append("<div>")
		html.append("<b>").append(feed.title.toUpperCase()).append("</b><br/>")
		
		text.append(feed.title.toUpperCase()).append("\n")
		
		int storyCount = 0;
		
		if(items != null && !items.isEmpty()) {
			//Iterate over our main elements. Should have one for each article
			for (Item item : items) {
				if (storyCount >= feed.maxStories)
					break

				//e.g.: Sun, 12 Dec 2010 07:41:11 PST
				DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
				Date date = formatter.parse(item.getPubDate().getText());
				
				if (DateUtils.isWithin24Hours(date)) {
					
					String htmlTitle = new StringBuffer("<a href=\"")
						.append(item.getLink())
						.append("\">")
						.append(item.getTitle())
						.append("</a><br/>")
						.toString()
						
					String textTitle = new StringBuffer()
						.append(item.getTitle())
						.append("\n")
						.toString()
					
					Document doc = Jsoup.parse(item.getDescription().getText());
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
						html.append("<a href=\""+item.getLink()+"\">More</a>")
						text.append(item.getLink())
					}
					
					html.append("<br/>")
					text.append("\n\n")
					
					storyCount++
				} else {
					Calendar cal = Calendar.getInstance();
					Date now = cal.getTime();
					long diff = now.getTime() - date.getTime();
					
					log.info("Not within 24 hours:" + date.toString())
					log.info("Difference is: " + diff)
				}
			}
		}
		
		if (storyCount == 0) {
			html.append("No new items").append("<br/>")
			text.append("No new items").append("\n")
		}
		
		html.append("</div>")
				
		feed.html = new Text(html.toString())
		feed.plainText = new Text(text.toString().trim())
		
		feed.lastUpdated = new Date()
	}
	
	public String getHtml(Feed feed) {
		return feed.html.getValue()
	}
	
	public String getPlainText(Feed feed) {
		return feed.plainText.getValue()
	}
	
}
