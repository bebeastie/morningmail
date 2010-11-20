package com.morningmail.controller

import java.util.ArrayList;

import com.morningmail.domain.*;
import javax.persistence.*

class AnotherController {

	def entityManager 
	
	def index = {  
		User user = new User(firstName:"Bob", last:"tester")
		
		
		OAuthToken token = new OAuthToken()
		user.tokens.add(token)
		token.user = user
		
		user.save()
		
		render(view:'monitor')
	}
	
	def listem = {

		Query q = entityManager.createQuery("select u from User u where u.firstName = :name")
		q.setParameter("name", "Bob")
		List results = q.getResultList(); 
		
		if(results) {
			System.out.println(results.size())
			User u = results.get(0)
			System.out.println(u.firstName)
			List <OAuthToken> tokens = u.tokens
			if (tokens) {
				System.out.println(tokens.size())	
			} else {
				System.out.println("nothing")
			}
			
			
			Query q2 = entityManager.createQuery("select t from OAuthToken t where t.user = :user")
			q2.setParameter("user", u)
			List results2 = q2.getResultList()
			
			if (results2) {
				System.out.println(results2.size())
			} else {
				System.out.println("nothing, again")
			}
			
		} else {
			System.out.println("nothig")
		}
			
		System.out.println("===trying GORM===")
		
		def u3 = User.findByFirstName("Bob")
		
		if (u3) {
			System.out.println(u3)	
		} else {
			System.out.println("GORM ain't working for shit (still)")
		}
			
		render(view:'boob')	 
	}
	
}
