package com.morningmail.utils;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;

import com.morningmail.utils.protocol.GAEConnectionManager;

public class HttpUtils {

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
}
