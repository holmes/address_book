package com.holmes.address;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Singleton;

@Singleton
public class WebDao {

	public List<Address> getAddresses() {
		ArrayList<Address> addresses = new ArrayList<Address>();
		addresses.add( new Address( "123 A St", "Home" ) );
		addresses.add( new Address( "456 B St", "Work" ) );
		addresses.add( new Address( "789 C St", "Play" ) );
		addresses.add( new Address( "123 D St", "Bar" ) );
		
		return addresses;
	}

	public Address getAddress( long id ) {
		return null;
	}

	public Address create( Address addressBook ) {
		return addressBook;
	}
	
	public Address updateNickname( long id, String nickName ) {
		return null;
	}

	public void deleteAddress( long id ) {
		;
	}
}
