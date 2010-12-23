package com.morningmail.utils;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpUtils {

	public static String fetchUrl(String url) throws ClientProtocolException, IOException {
	        HttpClient httpclient = new DefaultHttpClient();
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
