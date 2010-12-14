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

class YahooNewsFeedService implements FeedService {
	
	public void fetch(Feed feed) {
		try {
		
			RssParser parser = RssParserFactory.createDefault();
			Rss rss = parser.parse(new URL(feed.url));
			
			Collection items = rss.getChannel().getItems();
			
			String html = feed.title + "<br/>"
			String plainText = "<b>" + feed.title.toUpperCase() + "</b>\n"
			
			int storyCount = 1;
			
			if(items != null && !items.isEmpty()) {
				//Iterate over our main elements. Should have one for each article		
				for (Item item : items) {
					if (storyCount > feed.maxStories) 
						break
						
					//html
					String title = item.getTitle()
					title = title.replaceAll("\\n","");
					title = title.replaceAll("\\t","");
					title = title.replaceAll("\\(AP\\)","");
					title = title.replaceAll("\\(Reuters\\)","");
					title = title.trim()
					title = "<a href=\""+item.getLink()+"\">"+title+ "</a>"
					
					html+="<h3>"+title+"</h3>";
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
					plainText += title 
					plainText += "\n"
					plainText += doc.text() 
//					plainText +=  "<a href=\""+item.getLink()+"\">More</a>"
					plainText += "\n\n"
					storyCount++
				}
			}
			plainText = plainText.trim()
			
		
			feed.html = new Text(html)
			feed.plainText = new Text(plainText)
			
			feed.lastUpdated = new Date()
		} catch(Exception e) {
			log.info(e.toString())
		}
	}
	
	public String getHtml(Feed feed) {
		return feed.html.getValue()
	}
	
	public String getPlainText(Feed feed) {
		return feed.plainText.getValue()
	}
}
