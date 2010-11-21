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

class YahooNewsFeedService implements FeedService{

	private static final String FEED_URL = "http://rss.news.yahoo.com/rss/topstories"
	
	def entityManagerFactory
	EntityManager em
	
	public void fetch() {
		try {
			RssParser parser = RssParserFactory.createDefault();
			Rss rss = parser.parse(new URL(FEED_URL));
			
			Collection items = rss.getChannel().getItems();
			
			String html = ""
			String plainText = "NEWS\n\n"
			
			if(items != null && !items.isEmpty()) {
				//Iterate over our main elements. Should have one for each article		
				for (Item item : items) {
					//html
					html+="<h3>"+item.getTitle()+"</h3>";
					Document doc = Jsoup.parse(item.getDescription().getText());
					Element img = doc.select("img").first();
					if (img) {
						Integer newWidth = new Integer(img.attr("width"))/3
						Integer newHeight = new Integer(img.attr("height"))/3
						img.attr("width", newWidth.toString())
						img.attr("height", newHeight.toString())
						html+=img.outerHtml() 
					}
					html+=doc.text() + " "
					Element link = doc.select("a").first();
					if (link) {
						html+="<a href=\"" + link.attr("href") + "\">More</a>" 
					}
					html+="<br/>"
					
					//plainText
					plainText += item.getTitle() 
					plainText += "\n"
					plainText += doc.text() 
					plainText += "\n\n"
				}
			}
		
			Feed feed = Feed.findByType(Feed.TYPE_YAHOO_NEWS);
			
			if (!feed) { 
				feed = new Feed()
				feed.type = Feed.TYPE_YAHOO_NEWS
				feed.save()
			}
			
			feed.html = new Text(html)
			feed.plainText = new Text(plainText)
			
			feed.lastUpdated = new Date()
		} catch(Exception e) {
			log.info(e.toString())
		}
	}
	
	public String getHtml() {
		Feed feed = Feed.findByType(Feed.TYPE_YAHOO_NEWS)
		return feed.html.getValue()
	}
	
	public String getPlainText() {
		Feed feed = Feed.findByType(Feed.TYPE_YAHOO_NEWS)
		return feed.plainText.getValue()
	}
}
