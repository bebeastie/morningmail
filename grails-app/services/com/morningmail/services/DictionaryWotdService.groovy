package com.morningmail.services

import javax.persistence.EntityManager;

import com.morningmail.services.FeedService;
import com.morningmail.domain.Feed;
import com.sun.cnpi.rss.parser.RssParser;
import com.sun.cnpi.rss.parser.RssParserFactory;
import com.sun.cnpi.rss.elements.Rss;
import com.sun.cnpi.rss.elements.Item;
import javax.persistence.*;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import com.google.appengine.api.datastore.Text
import org.jsoup.nodes.Document
import org.jsoup.Jsoup;
import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import org.springframework.beans.factory.InitializingBean

class DictionaryWotdService implements FeedService {
	
	public void fetch(Feed feed) {
		try {
			RssParser parser = RssParserFactory.createDefault();
			Rss rss = parser.parse(new URL(feed.url));
			
			Collection items = rss.getChannel().getItems();
			
			String html = "<h3>" + feed.title + "</h3></br>"
			String plainText = feed.title.toUpperCase() + "\n"
					
			if(items != null && !items.isEmpty()) {
				Item item = items.iterator().next();
				plainText+=item.getDescription();
				html+=item.getDescription();
			}
			plainText = plainText.trim()
						
			feed.html = new Text(html)
			feed.plainText = new Text(plainText)
			
			feed.lastUpdated = new Date()
		} catch(Exception e) {
			log.info("Problem getting dictionary.com word of the day",e)
		}
	}
	
	public String getHtml(Feed feed) {
		return feed.html.getValue()
	}
	
	public String getPlainText(Feed feed) {
		return feed.plainText.getValue()
	}
	
}
