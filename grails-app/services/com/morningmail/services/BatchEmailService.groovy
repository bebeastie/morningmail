package com.morningmail.services

import javax.persistence.*
import org.springframework.orm.jpa.EntityManagerFactoryUtils
import java.util.ArrayList;
import java.util.Date
import java.util.Calendar
import com.morningmail.utils.DateUtils
import com.morningmail.domain.User;
import com.morningmail.domain.Newsletter;
import com.morningmail.domain.Email;
import com.google.appengine.api.datastore.KeyFactory

class BatchEmailService {
	private static final Long LOWER_DATE_BOUND = 600000 //10 min
	private static final Long UPPER_DATE_BOUND = 300000 //5 min
	private static final Long PREVIOUS_PERIOD = 18000000  //5 hours
	
	static transactional = false
	
	def entityManagerFactory
	EntityManager em
	
	/**
	 * Determines the list of users whose emails should be delivered
	 * within 5 minutes before or after the present moment and who have not had an
	 * email prepared within the past 5 hours.
	 * @return
	 */
	public List<Newsletter> getNewslettersToRender() {
		em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory)
		
		Date now = Calendar.getInstance().getTime();
		
		Date lowerDate = DateUtils.getNormalizedTime(now, -LOWER_DATE_BOUND)
		Date upperDate = DateUtils.getNormalizedTime(now, UPPER_DATE_BOUND)
		
		log.info("Searching for newsletters between " + lowerDate + " and " + upperDate);
	
		Date lastRenderedDate = new Date(now.getTime() - PREVIOUS_PERIOD)

		Query q = em.createQuery("select n from Newsletter n where n.deliveryTime >= :lowerDate" +
			 " and n.deliveryTime <= :upperDate")
		q.setParameter("lowerDate", lowerDate);
		q.setParameter("upperDate", upperDate);
		q.setMaxResults(75)
	
		//GAE datastore doesn't support comparisons against two operators
		//so we need to programmatically remove users that 
		//we've rendered recently
		List<Newsletter> newsletters = new ArrayList<Newsletter>()

		List<User> dbResults = q.getResultList()
		
		for (Iterator<Newsletter> it = dbResults.iterator(); it.hasNext(); ) {
			Newsletter n = it.next()
			
			if (n.lastRenderedDate < lastRenderedDate) {
				newsletters.add(n)
				n.lastRenderedDate = Calendar.getInstance().getTime()
				em.merge(n)
			}
		}
		
		log.info("Found " + newsletters.size() + " to render")
		return newsletters
	}
	
	/**
	 * Returns a list of keys for all pending emails
	 * @return
	 */
	public List<String> getEmailsToSend() {
		em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory)
		
		Query q = em.createQuery("select e from Email e where e.status = :status")
		
		q.setParameter("status", Email.STATUS_PENDING)
		q.setMaxResults(75)
		
		List emailKeys = new ArrayList<String>()
		
		try {
			List<Email> emails = q.getResultList()
			
			for (Email e: emails) {
				emailKeys.add(KeyFactory.keyToString(e.id))
				e.status = Email.STATUS_QUEUED
			}
		} catch (NoResultException e) {
			return emailKeys
		}
		return emailKeys
	}
}
