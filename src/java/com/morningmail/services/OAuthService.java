package com.morningmail.services;

import com.morningmail.domain.User;
import com.morningmail.domain.OAuthToken;

public interface OAuthService {
	
	/**
	 * Used to generate a request token and
	 * authorization URL
	 * @param u
	 * @return
	 */
	public OAuthToken generateRequestToken(User u);
	
	public OAuthToken upgradeToken(String token, String verifier);
	
	public OAuthToken getToken(User u);
}
