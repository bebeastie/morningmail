package com.morningmail.controller

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.*;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import com.morningmail.domain.*

class DebugController {
	def entityManagerFactory
	EntityManager em
	
	def listEmails = {
		User u = User.findById(params.id)
		
		StringBuffer sb = new StringBuffer();
		for (Email e: u.emails) {
			sb.append(e.id).append("</br>")
		}
		render(view:'index', model:[returnValue:sb.toString()])
	}
	
	
	def deleteAllTwitter = {
		if (!session.userEmail) {
			redirect(uri:'/')
			return
		}
		
		em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory)
		Query q = em.createQuery("delete from TwitterLink t");
		int number = q.executeUpdate();
		render(view:'index', model:[returnValue:number])
	}
}
