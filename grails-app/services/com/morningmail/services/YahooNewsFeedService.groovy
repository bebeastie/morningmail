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
import com.morningmail.utils.TextUtils

class YahooNewsFeedService implements FeedService {
	
	public void fetch(Feed feed) {
		try {
		
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
						
					//html
					String title = item.getTitle()
					title = title.replaceAll("\\n","");
					title = title.replaceAll("\\t","");
					title = title.replaceAll("\\(AP\\)","");
					title = title.replaceAll("\\(Reuters\\)","");
					title = title.trim()
					
					String htmlTitle = new StringBuffer("<a href=\"")
						.append(item.getLink())
						.append("\">")
						.append(title)
						.append("</a><br/>")
						.toString()
						
					String textTitle = new StringBuffer()
						.append(title)
						.append("\n")
						.toString()
					
	
					Document doc = Jsoup.parse(item.getDescription().getText());
					
					String description = doc.text()
					
					if (feed.maxWordsPerStory != Feed.NO_MAX)
						description = TextUtils.getSummary(description, feed.maxWordsPerStory, true)
					
					description = description.trim()
					
//						Element img = doc.select("img").first();
//					if (img) {
//						Integer newWidth = new Integer(img.attr("width"))/3
//						Integer newHeight = new Integer(img.attr("height"))/3
//						img.attr("width", newWidth.toString())
//						img.attr("height", newHeight.toString())
//						html+=img.outerHtml() 
//					}
//
//					Element link = doc.select("a").first();
//					if (link) {
//						html+="<a href=\"" + link.attr("href") + "\">More</a>" 
//					}
//					html+="<br/>"
					
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
				}
			}
			
			html.append("</div>")
			
			if (storyCount == 0) {
				html = new StringBuffer()
				text = new StringBuffer()
			}
					
			feed.html = new Text(html.toString())
			feed.plainText = new Text(text.toString().trim())
			
			feed.lastUpdated = new Date()
		} catch(Exception e) {
			log.error("Problem parsing feed", e)
		}
	}
	
	public String getHtml(Feed feed) {
		return feed.html.getValue()
	}
	
	public String getPlainText(Feed feed) {
		return feed.plainText.getValue()
	}
}
