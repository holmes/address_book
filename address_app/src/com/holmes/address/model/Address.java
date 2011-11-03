package com.holmes.address.model;

public class Address {

	private long id;
	private String address;
	private String nickname;
	private double latitude;
	private double longitude;

	public Address( String address, String nickname ) {
		super();
		this.address = address;
		this.nickname = nickname;
	}

	public long getId() {
		return id;
	}

	public void setId( long id ) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress( String address ) {
		this.address = address;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname( String nickname ) {
		this.nickname = nickname;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude( double latitude ) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude( double longitude ) {
		this.longitude = longitude;
	}
}
