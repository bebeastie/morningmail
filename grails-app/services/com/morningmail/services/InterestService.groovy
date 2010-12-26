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
	
	public void setSelected(User u, String[] keys) {
		em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory)
		u.interests.clear()
		for(String k: keys) 
			u.interests.add(KeyFactory.stringToKey(k))
		em.merge(u)
	}
	
	public void setSelected(User u, String key) {
		String[] arr = new String[1]
		arr[0] = key
		setSelected(u, arr)
	}
	
	public void add(User u, Interest interest) {
		em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory)
		u.interests.add(interest.id)
		em.merge(u)
	}
}
