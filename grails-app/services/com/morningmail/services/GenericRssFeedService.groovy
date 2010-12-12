package com.morningmail.services

import javax.persistence.EntityManager;
import com.morningmail.domain.Feed;
import com.sun.cnpi.rss.parser.RssParser;
import com.sun.cnpi.rss.parser.RssParserFactory;
import com.sun.cnpi.rss.elements.Rss;
import com.sun.cnpi.rss.elements.Item;
import com.morningmail.utils.TextUtils;
import com.google.appengine.api.datastore.Text
import org.jsoup.nodes.Document
import org.jsoup.Jsoup;
import org.jsoup.select.Elements
import org.jsoup.nodes.Element

class GenericRssFeedService implements FeedService {
	
	public void fetch(Feed feed) {
		RssParser parser = RssParserFactory.createDefault();
		Rss rss = parser.parse(new URL(feed.url));
		
		Collection items = rss.getChannel().getItems();
		
		String html = feed.title + "<br/>"
		String plainText = feed.title.toUpperCase() + "\n"
		
		int storyCount = 1;
		
		if(items != null && !items.isEmpty()) {
			//Iterate over our main elements. Should have one for each article
			for (Item item : items) {
				if (storyCount > feed.maxStories)
					break
				
				String title = item.getTitle()
				title = title.trim()
				
				Document doc = Jsoup.parse(item.getDescription().getText());
				String description = doc.text()
				description.trim()
				
				if (feed.maxWordsPerStory != -1) 
					description = TextUtils.getSummary(description, feed.maxWordsPerStory, true)
				
				//plainText
				plainText += title
				plainText += "\n"
				plainText += description
				plainText += "\n\n"
				
				//html @TODO
				
				storyCount++
			}
		}
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
