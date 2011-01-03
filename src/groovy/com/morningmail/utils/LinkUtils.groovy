package com.morningmail.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import grails.util.GrailsUtil;
import groovy.util.ConfigObject;
import org.codehaus.groovy.grails.commons.*

public class LinkUtils {
	public static final String CONTROLLER = "email/link";
	
	public static String encode(Key interestKey, Key feedKey, String emailId, String url) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		sb.append(ConfigurationHolder.config.grails.serverURL).append(CONTROLLER)
		sb.append("?interest=").append(KeyFactory.keyToString(interestKey));
		sb.append("&feed=").append(KeyFactory.keyToString(feedKey));
		sb.append("&email=").append(emailId);
		sb.append("&url=").append(URLEncoder.encode(url, "UTF-8"));
		return sb.toString();
	}
}
