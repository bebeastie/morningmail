package com.morningmail.services

import javax.persistence.EntityManager
import org.springframework.orm.jpa.EntityManagerFactoryUtils
import com.morningmail.domain.*
import javax.persistence.*
import com.morningmail.utils.DateUtils
import com.google.appengine.api.datastore.Text

public class ReadLaterFeedService implements PersonalFeedService{
	def entityManagerFactory
	EntityManager em
	
	public void fetch(User u) {
		em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory)
		
		Query q = em.createQuery("select rl from ReadLaterItem rl where savedDate >= :lowerDate and"
			+ " user = :user")
		q.setParameter("lowerDate", DateUtils.get24HoursAgo());
		q.setParameter("user", u)
		q.setMaxResults(10)
		
		StringBuffer html = new StringBuffer()
		StringBuffer text = new StringBuffer()
		
		html.append("<div>")
		html.append("<b>READ LATER</b><br/>")
		
		text.append("READ LATER\n")
		

		List<ReadLaterItem> readLaterItems = q.getResultList()
		
		for (ReadLaterItem rlItem: readLaterItems) {
			html.append("<a href=\""+rlItem.getUrl()+"\">"+rlItem.getTitle()+"</a>")
				.append("<br/>")

			text.append(rlItem.getUrl()).append("\n")
			
			if (rlItem.getDescription()) {
				html.append(rlItem.getDescription())
					.append("<br/>")
				
				text.append(rlItem.getDescription())
					.append("\n")
			}
		}
			
		html.append("</div>")
		
		if (readLaterItems.size() == 0) {	
			html = new StringBuffer()
			text = new StringBuffer()
		}
		
		PersonalFeed feed = PersonalFeed.findByTypeAndUser(PersonalFeed.TYPE_READ_LATER, u);
		
		if (!feed) {
			feed = new PersonalFeed()
			feed.type = PersonalFeed.TYPE_READ_LATER
			feed.user = u
			u.pFeeds.add(feed)
			feed.save()
		}
		
		feed.html = new Text(html.toString())
		feed.plainText = new Text(text.toString().trim())
		feed.lastUpdated = new Date()
	}
	
	public String getHtml(User u) {
		try {
			PersonalFeed feed = PersonalFeed.findByTypeAndUser(PersonalFeed.TYPE_READ_LATER, u)
			return feed.html.getValue()
		}catch (Exception e) {
			   log.error("Couldn't find $PersonalFeed.TYPE_READ_LATER feed for $u")
			return ""
		}
	}
	
	public String getPlainText(User u) {
		try {
			PersonalFeed feed = PersonalFeed.findByTypeAndUser(PersonalFeed.TYPE_READ_LATER, u)
			return feed.plainText.getValue()
		}catch (Exception e) {
			   log.error("Couldn't find $PersonalFeed.TYPE_READ_LATER feed for $u")
			return ""
		}
	}

}
