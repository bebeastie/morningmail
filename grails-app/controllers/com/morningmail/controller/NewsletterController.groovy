package com.morningmail.controller

import com.morningmail.domain.Newsletter;
import com.morningmail.domain.Interest;
import com.morningmail.domain.User;
import com.morningmail.utils.WebUtils;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import javax.persistence.EntityManager;

class NewsletterController {

	def interestService
	def entityManagerFactory
	
	def view = {
		Newsletter nl
		
		try {
			if (params.id) {
				nl = Newsletter.findById(KeyFactory.stringToKey(params.id))	
			} else if (params.name) {
				nl = Newsletter.findByNameUppercase(params.name.replace('-',' ').toUpperCase())
			}
			//@TODO check to make sure it is public
		} catch (Exception e) {
			log.info("Had trouble finding newsletter", e)
		}
		
		nl ? render(view:'view', model:[newsletter:nl]) :
			render(view:'unknown')
	}
	
	def subscribe = {
		Newsletter nl
		
		try {
			nl = Newsletter.findById(KeyFactory.stringToKey(params.newsletter))	
		} catch (Exception e) {
			log.error("Had trouble finding the newsletter", e)
			//show newsletter not found error
			response.status = 404;
			return
		}
		
		if (WebUtils.isValidEmailAddress(params.email)) {
			User u = User.findByEmail(params.email)
			
			if(!u) {
				//create an account
				u = new User()
				u.email = params.email
				u.type = User.TYPE.SUBSCRIBE_ONLY
			} else if (u.type == User.TYPE.STANDARD
				&& !params.email.equals(session.email)) {
				//that email belongs to a standard user and they are not logged in
				//@TODO get the user back to the subscribe page!
				redirect(controller:'user', action:'login')
				return
			} 
				
			u.newsletters.add(nl)
			
			User.withTransaction() {
				u.save(flush:true)
			}
				
			nl.subscribers.add(u)
			
			Newsletter.withTransaction() {
				nl.merge()
			}
			render(view:"completeSubscription", model:[newsletter:nl])
			return
		} else {
			flash.message = "Please enter a valid email address"
			flash.email = params.email
			render(view:"view")
		}
	}
		
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
