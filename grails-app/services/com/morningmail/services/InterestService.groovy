package com.morningmail.services

import javax.persistence.EntityManager;

import com.morningmail.domain.Interest;
import com.morningmail.domain.User;
import org.springframework.orm.jpa.EntityManagerFactoryUtils
import javax.persistence.*
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.springframework.beans.factory.InitializingBean;

class InterestService {

	def entityManagerFactory
	EntityManager em
	
	public List<Interest> getAll(User u) {
		em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory)
		Query q = em.createQuery("select i from Interest i where i.owner = :owner or " +
			"i.owner = null order by i.displayName")
		q.setParameter("owner", u.id);
		
		return q.getResultList()
	}
	
}
