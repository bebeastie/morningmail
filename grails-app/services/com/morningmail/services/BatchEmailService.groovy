package com.morningmail.services

import javax.persistence.*
import org.springframework.orm.jpa.EntityManagerFactoryUtils
import java.util.ArrayList;
import java.util.Date
import java.util.Calendar
import com.morningmail.utils.DateUtils
import com.morningmail.domain.User;

class BatchEmailService {
	private static final Long LOWER_DATE_BOUND = 300000 //5 min
	private static final Long UPPER_DATE_BOUND = 300000 //5 min
	private static final Long PREVIOUS_PERIOD = 18000000  //5 hours
	
	def entityManagerFactory
	EntityManager em
	
	/**
	 * Determines the list of users whose emails should be delivered
	 * within 5 minutes before or after the present moment and who have not had an
	 * email prepared within the past 5 hours.
	 * @return
	 */
	public List<User> getUsersToRender() {
		em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory)
		
		Date now = Calendar.getInstance().getTime();
		
		Date lowerDate = DateUtils.getNormalizedTime(now, -LOWER_DATE_BOUND)
		Date upperDate = DateUtils.getNormalizedTime(now, UPPER_DATE_BOUND)
				
		Date lastRenderedDate = new Date(now.getTime() - PREVIOUS_PERIOD)
		
		Query q = em.createQuery("select u from User u where u.deliveryTime >= :lowerDate" +
			 " and u.deliveryTime <= :upperDate")
		q.setParameter("lowerDate", lowerDate);
		q.setParameter("upperDate", upperDate);
		
		
		//GAE datastore doesn't support comparisons against two operators
		//so we need to programmatically remove users that 
		//we've rendered recently
		try {
			List<User> results = q.getResultList()
			for (Iterator<User> it = results.iterator(); it.hasNext(); ) {
				User u = it.next()
				
				if (u.lastRenderedDate >= lastRenderedDate)
					it.remove()
			}
			return results
		} catch (NoResultException e) {
			return new ArrayList<User>();
		}
	}
}
