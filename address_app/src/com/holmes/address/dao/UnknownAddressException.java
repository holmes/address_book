package com.holmes.address.dao;

@SuppressWarnings( "serial" )
public class UnknownAddressException extends RuntimeException {
	private final String addressGiven;

	public UnknownAddressException( String addressGiven ) {
		super( String.format("Unable to geocode address: %s", addressGiven ) );
		this.addressGiven = addressGiven;
	}
	
	public String getAddressGiven() {
		return addressGiven;
	}
}
