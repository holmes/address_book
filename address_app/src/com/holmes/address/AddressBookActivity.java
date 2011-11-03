package com.holmes.address;

import roboguice.activity.RoboListActivity;

import com.google.inject.Inject;

public class AddressBookActivity extends RoboListActivity {

	@Inject
	private WebDao webDao;
	
}