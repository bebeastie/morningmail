package com.morningmail.controller

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.*;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import com.morningmail.domain.*

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.BodyPart;

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
	
	def sendEmail = {
		if (!params.submit) {
			render(view:'sendEmail', model:[returnValue:""])
			return
		}
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
			
		String subject = params.subject
		
		try {
			Message msg = new MimeMessage(session);

			msg.setFrom(new InternetAddress(params.from, "MorningMail"));
			msg.addRecipient(Message.RecipientType.TO,
							new InternetAddress(params.to));
			msg.setSubject(subject);
		   
			MimeMultipart mp = new MimeMultipart();
			BodyPart tp = new MimeBodyPart();
			tp.setText(params.plainText, "UTF-8");
			mp.addBodyPart(tp);

			tp = new MimeBodyPart();
			tp.setContent(params.html
				, "text/html");
			mp.addBodyPart(tp);

			mp.setSubType("alternative");

			msg.setContent(mp);

			Transport.send(msg);
			
			render(view:'sendEmail', model:[returnValue:"Success"])
		} catch (AddressException e) {
			log.error("Problems sending email", e)
			render(view:'sendEmail', model:[returnValue:"Error: " + e])
		} catch (MessagingException e) {
			log.error("Problems sending email ", e)
			render(view:'sendEmail', model:[returnValue:"Error: " + e])
		}
		
	}
}
