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
import java.util.Date

class GenericRssFeedService implements FeedService {
	
	public void fetch(Feed feed) {
		RssParser parser = RssParserFactory.createDefault();
		Rss rss = parser.parse(new URL(feed.url));
		
		Collection items = rss.getChannel().getItems();
		
		String html = feed.title + "<br/>"
		String plainText = "<b>" + feed.title.toUpperCase() + "</b>\n"
		
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
					
					String title = "<a href=\""+item.getLink()+"\">"+item.getTitle() + "</a>"
					
					Document doc = Jsoup.parse(item.getDescription().getText());
					String description = doc.text()
					description.trim()
					
					if (feed.maxWordsPerStory != -1) 
						description = TextUtils.getSummary(description, feed.maxWordsPerStory, true)
					
					String moreLink = "<a href=\""+item.getLink()+"\">More</a>"
					
					//plainText
					if (feed.includeItemTitle) {
						plainText += title
						plainText += "\n"
					}
					
					plainText += description
					
					if (feed.includeItemMoreLink) 
						plainText += moreLink
					
					plainText += "\n\n"
					
					//html @TODO
					
					storyCount++
				}
			}
		}
		
		if (storyCount == 0)
			plainText += "No new items"
			
		plainText = plainText.trim()
		
		feed.html = new Text(html)
		feed.plainText = new Text(plainText)
		
		feed.lastUpdated = new Date()
		
	}
	
	public String getHtml(Feed feed) {
		return feed.html.getValue()
	}
	
	public String getPlainText(Feed feed) {
		return feed.plainText.getValue()
	}
	
}
