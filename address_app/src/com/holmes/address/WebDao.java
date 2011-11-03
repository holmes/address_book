package com.holmes.address;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Singleton;

@Singleton
public class WebDao {

	public List<AddressBook> getAddresses() {
		return new ArrayList<AddressBook>();
	}

	public AddressBook getAddress( long id ) {
		return null;
	}

	public AddressBook create( AddressBook addressBook ) {
		return addressBook;
	}
	
	public AddressBook updateNickname( long id, String nickName ) {
		return null;
	}

	public void deleteAddress( long id ) {
		;
	}
}
