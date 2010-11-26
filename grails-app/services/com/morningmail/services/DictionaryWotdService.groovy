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

class DictionaryWotdService implements FeedService{

	private static final String FEED_URL = "http://dictionary.reference.com/wordoftheday/wotd.rss"
	
	def entityManagerFactory
	EntityManager em
	
	public void fetch() {
		try {
			RssParser parser = RssParserFactory.createDefault();
			Rss rss = parser.parse(new URL(FEED_URL));
			
			Collection items = rss.getChannel().getItems();
			
			String html = "<h3>WORD OF THE DAY</h3></br>"
			String plainText = "WORD OF THE DAY\n"
					
			if(items != null && !items.isEmpty()) {
				Item item = items.iterator().next();
				plainText+=item.getDescription();
				html+=item.getDescription();
			}
			plainText = plainText.trim()
			
			Feed feed = Feed.findByType(Feed.TYPE_DICTIONARY_DOT_COM_WOTD);
			
			if (!feed) { 
				feed = new Feed()
				feed.type = Feed.TYPE_DICTIONARY_DOT_COM_WOTD
				feed.save()
			}
			
			feed.html = new Text(html)
			feed.plainText = new Text(plainText)
			
			feed.lastUpdated = new Date()
		} catch(Exception e) {
			log.info("Problem getting dictionary.com word of the day",e)
		}
	}
	
	public String getHtml() {
		Feed feed = Feed.findByType(Feed.TYPE_DICTIONARY_DOT_COM_WOTD)
		return feed.html.getValue()
	}
	
	public String getPlainText() {
		Feed feed = Feed.findByType(Feed.TYPE_DICTIONARY_DOT_COM_WOTD)
		return feed.plainText.getValue()
	}
	
	public String getShortPlainText() {
		Feed feed = Feed.findByType(Feed.TYPE_DICTIONARY_DOT_COM_WOTD)
		return feed.plainText.toString();
	}
}
