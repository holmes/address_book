package com.holmes.address.dao;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import roboguice.util.Ln;
import android.net.http.AndroidHttpClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.holmes.address.model.Address;

@Singleton
public class WebDao {
	private static final String BASE_DOMAIN = "http://10.73.233.65:5000";
	private static final String BASE_ADDRESS_PATH = BASE_DOMAIN + "/address/";
	private static final String SPECIFIC_ADDRESS_PATH = BASE_DOMAIN + BASE_ADDRESS_PATH + "%d";
	private static final Type ADDRESSES_TYPE = new TypeToken<List<Address>>() {}.getType();

	@Inject
	private AndroidHttpClient httpClient;

	@Inject
	private Gson gson;

	public List<Address> getAddresses() {
		ArrayList<Address> addresses;

		try {
			HttpGet request = new HttpGet( BASE_DOMAIN );
			HttpResponse response = httpClient.execute( request );
			String content = EntityUtils.toString( response.getEntity() );

			// ugly hack to just get Gson to just work
			JSONObject json = new JSONObject( content );
			JSONArray addressesArray = json.getJSONArray( "items" );

			addresses = gson.fromJson( addressesArray.toString(), ADDRESSES_TYPE );
		} catch ( Exception e ) {
			Ln.e( e, "Unable to retrieve all addresses" );
			addresses = new ArrayList<Address>();
		}

		return addresses;
	}

	public Address create( String address, String nickname ) {
		try {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add( new BasicNameValuePair( "address", address ) );
			parameters.add( new BasicNameValuePair( "nickname", nickname ) );
			String body = URLEncodedUtils.format( parameters, "UTF-8" );

			HttpPost request = new HttpPost( BASE_ADDRESS_PATH );
			request.setEntity( new StringEntity( body ) );
			
			return executeForAddress( request );
		} catch ( Exception e ) {
			Ln.e( e, "Unable to retrieve all addresses" );
			return new Address( "Unable to retrieve", "Unknown" );
		}
	}

	public Address updateNickname( long id, String nickname ) {
		try {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add( new BasicNameValuePair( "nickname", nickname ) );
			String body = URLEncodedUtils.format( parameters, "UTF-8" );

			HttpPut request = new HttpPut( String.format( SPECIFIC_ADDRESS_PATH, id ) );
			request.setEntity( new StringEntity( body ) );

			return executeForAddress( request );
		} catch ( Exception e ) {
			Ln.e( e, "Unable to retrieve all addresses" );
			return new Address( "Unable to retrieve", "Unknown" );
		}
	}

	public boolean deleteAddress( long id ) {
		try {
			HttpDelete request = new HttpDelete( String.format( SPECIFIC_ADDRESS_PATH, id ) );
			HttpResponse response = httpClient.execute( request );
			return response.getStatusLine().getStatusCode() == 200;
		} catch ( Exception e ) {
			Ln.e( e, "Unable to delete your address" );
			return false;
		}
	}
	
	private Address executeForAddress( HttpUriRequest request ) throws IOException {
		HttpResponse response = httpClient.execute( request );
		String content = EntityUtils.toString( response.getEntity() );
		
		return gson.fromJson( content.toString(), Address.class );
	}
}
