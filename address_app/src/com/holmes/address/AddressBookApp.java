package com.holmes.address;

import java.util.List;

import roboguice.application.RoboApplication;
import android.net.http.AndroidHttpClient;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class AddressBookApp extends RoboApplication {

	@Override
	protected void addApplicationModules( List<Module> modules ) {
		modules.add( new AbstractModuleExtension());
	}
	
	private final class AbstractModuleExtension extends AbstractModule {
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
}
