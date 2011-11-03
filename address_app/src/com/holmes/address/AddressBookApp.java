package com.holmes.address;

import java.util.List;

import roboguice.application.RoboApplication;

import com.google.inject.Module;

public class AddressBookApp extends RoboApplication {

	@Override
	protected void addApplicationModules( List<Module> modules ) {
		modules.add( new AddressBookModule() );
	}
}
