package com.holmes.address;

import android.net.http.AndroidHttpClient;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

final class AddressBookModule extends AbstractModule {
	@Override
	protected void configure() {
		// i'm sure we'll need you at some point
	}
	
	@Provides
	@Singleton
	public AndroidHttpClient getHttpClient() {
		return AndroidHttpClient.newInstance( "Address Book App" );
	}
}