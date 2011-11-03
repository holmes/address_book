package com.holmes.address.dao;

import com.google.inject.Singleton;

@Singleton
public class BaseUrlProvider {
	private static final String BASE_DOMAIN = "http://10.73.233.65:5000";

	private String baseUrl = BASE_DOMAIN;
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public void setBaseUrl( String baseUrl ) {
		this.baseUrl = baseUrl;
	}
}
