package com.holmes.address.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Address implements Parcelable {

	public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
		@Override
		public Address createFromParcel( Parcel source ) {
			return new Address( source );
		}

		@Override
		public Address[] newArray( int size ) {
			return new Address[size];
		}
	};

	private long id;
	private String address;
	private String nickname;
	private double latitude;
	private double longitude;

	public Address() {
		;
	}

	public Address( String address, String nickname ) {
		super();
		this.address = address;
		this.nickname = nickname;
	}

	public Address( Parcel source ) {
		id = source.readLong();
		address = source.readString();
		nickname = source.readString();
		latitude = source.readDouble();
		longitude = source.readDouble();
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel dest, int flags ) {
		dest.writeLong( id );
		dest.writeString( address );
		dest.writeString( nickname );
		dest.writeDouble( latitude );
		dest.writeDouble( longitude );
	}
}
