package com.morningmail.controller

import com.morningmail.domain.Newsletter;
import com.morningmail.domain.Interest;
import com.morningmail.domain.User;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import javax.persistence.EntityManager;

class NewsletterController {

	def interestService
	def entityManagerFactory
	
	def create = {
		if (!session.userEmail) {
			redirect(uri:'/')
			return
		}
		
		User user = User.findByEmail(session.userEmail)
		Newsletter nl = Newsletter.create(user, params.title, params.deliveryTime, 
			params.timeZone)
		
		if (nl.validate()) {
			Newsletter.withTransaction() {
				nl.subscribers.add(user.id)
				nl.save(flush:true)
			}
			def em = EntityManagerFactoryUtils.
				getTransactionalEntityManager(entityManagerFactory)
			user.subscriptions.add(nl.id)
			em.merge(user)
			
			redirect(action:'edit', params:[id:KeyFactory.keyToString(nl.id)])
		} else {
			log.error("ERROR trying to create!")
		}
	}
	
	def edit = {
		if (!session.userEmail) {
			redirect(uri:'/')
			return
		}
		
		User user = User.findByEmail(session.userEmail)
		
		Newsletter nl = Newsletter.findById(KeyFactory.stringToKey(params.id))
		
		def interestList = interestService.getAll(user)
		def interestMap = new HashMap<Key, Interest>()
		
		for(Interest i: interestList)
			interestMap.put(i.id, i)
		
		if(params.save) {
			def em = EntityManagerFactoryUtils.
				getTransactionalEntityManager(entityManagerFactory)
			nl.setInterests(params.get("interests[]"))
			em.merge(nl)
		}

		render(view:'edit', model:[
			newsletter: nl,
			user:user,
			interestList:interestList, 
			interestMap:interestMap])
	}
}
