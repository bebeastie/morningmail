package com.morningmail.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import com.morningmail.utils.protocol.GAEConnectionManager;

public class WebUtils {

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
	
	  /**
	  * Validate the form of an email address.
	  *
	  * <P>Return <tt>true</tt> only if 
	  *<ul> 
	  * <li> <tt>aEmailAddress</tt> can successfully construct an 
	  * {@link javax.mail.internet.InternetAddress} 
	  * <li> when parsed with "@" as delimiter, <tt>aEmailAddress</tt> contains 
	  * two tokens which satisfy {@link hirondelle.web4j.util.Util#textHasContent}.
	  *</ul>
	  *
	  *<P> The second condition arises since local email addresses, simply of the form
	  * "<tt>albert</tt>", for example, are valid for 
	  * {@link javax.mail.internet.InternetAddress}, but almost always undesired.
	  */
	  public static boolean isValidEmailAddress(String aEmailAddress){
	    if (aEmailAddress == null) return false;
	    boolean result = true;
	    try {
	      InternetAddress emailAddr = new InternetAddress(aEmailAddress);
	      if ( ! hasNameAndDomain(aEmailAddress) ) {
	        result = false;
	      }
	    }
	    catch (AddressException ex){
	      result = false;
	    }
	    return result;
	  }

	  private static boolean hasNameAndDomain(String aEmailAddress){
	    String[] tokens = aEmailAddress.split("@");
	    return 
	     tokens.length == 2 &&
	     textHasContent( tokens[0] ) && 
	     textHasContent( tokens[1] ) ;
	  }
	  
	  /**
	   * Returns true if aText is non-null and has visible content.
	   *
	   * This is a test which is often performed, and should probably
	   * be placed in a general utility class.
	   */
	   private static boolean textHasContent( String aText ){
	     String EMPTY_STRING = "";
	     return (aText != null) && (!aText.trim().equals(EMPTY_STRING));
	   }
}
