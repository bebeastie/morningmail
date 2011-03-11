package com.morningmail.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import com.google.appengine.api.datastore.Key;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import com.morningmail.utils.protocol.GAEConnectionManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import grails.util.GrailsUtil;
import groovy.util.ConfigObject;

import org.codehaus.groovy.grails.commons.*

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.SyndFeedInput;
import java.io.StringReader;

public class WebUtils {
	public static final String EMAIL_CONTROLLER = "/email/link";
	
	private static String fetchUrlBasic(String fetchUrl) throws ClientProtocolException, IOException {
		URL url = new URL(fetchUrl);
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line;
		
		StringBuffer buffer = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		reader.close();
		return buffer.toString();
	}
	
	public static boolean isValidFeed(String url) {
		SyndFeedInput input = new SyndFeedInput()
		try {
			SyndFeed syndFeed = input.build(new StringReader(fetchUrl(url)))
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static String fetchUrl(String url) throws ClientProtocolException, IOException {
		BasicHttpParams params = new BasicHttpParams();

		HttpClient httpclient = new DefaultHttpClient(new GAEConnectionManager(), params);
	    HttpGet httpget = new HttpGet(url); 

	    // Create a response handler
	    ResponseHandler<String> responseHandler = new BasicResponseHandler();
	    String responseBody = httpclient.execute(httpget, responseHandler);

	    // When HttpClient instance is no longer needed, 
	    // shut down the connection manager to ensure
	    // immediate deallocation of all system resources
	    httpclient.getConnectionManager().shutdown(); 
			
	    return responseBody;
	}
	
	public static boolean isValidEmailAddress(String emailAddress){
		String  expression="^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}\$";
		CharSequence inputStr = emailAddress;
		Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}
	
	public static String encodeLink(Key interestKey, Key feedKey, String emailId, String url) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		sb.append(ConfigurationHolder.config.grails.serverURL).append(EMAIL_CONTROLLER)
		sb.append("?interest=").append(KeyFactory.keyToString(interestKey));
		sb.append("&feed=").append(KeyFactory.keyToString(feedKey));
		sb.append("&email=").append(emailId);
		sb.append("&url=").append(URLEncoder.encode(url, "UTF-8"));
		return sb.toString();
	}
	
	public static String getUrl(String controller, String action, Map params) {
		def g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()
		return g.createLink(controller: controller, action: action, params: params)
	}
	
	public static String createLinkElement(String controller, String action, Map params, String text) {
		StringBuffer sb = new StringBuffer()
			.append("<a href=\"")
			.append(WebUtils.getUrl(controller, action, params))
			.append("\">$text</a>")
		return sb.toString()
	}
	
	public static getAbsoluteUrl(String controller, String action, Map params) {
		return ConfigurationHolder.config.grails.serverURL + getUrl(controller, action, params)
	}
	
	public static getAbsoluteUrl(String path) {
		return ConfigurationHolder.config.grails.serverURL + path;
	}
}
