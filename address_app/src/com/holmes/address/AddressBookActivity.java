package com.holmes.address;

import java.util.List;

import roboguice.activity.RoboListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.inject.Inject;
import com.holmes.address.dao.WebDao;
import com.holmes.address.model.Address;

public class AddressBookActivity extends RoboListActivity implements OnItemClickListener {

	@Inject
	private WebDao webDao;
	
	private AddressBookAdapter adapter;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		getListView().setOnItemClickListener( this );
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		new RetrieverTask().execute( (Void[]) null );
	}
	
	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		// launch another activity
	}
	
	private class AddressBookAdapter extends ArrayAdapter<Address> {
		public AddressBookAdapter( List<Address> addresses ) {
			super( AddressBookActivity.this, android.R.layout.simple_list_item_1, addresses );
		}
		
		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			TextView title = (TextView) super.getView( position, convertView, parent );
			title.setText( getItem( position ).getNickname() );
			return title;
		}
	}
	
	private class RetrieverTask extends AsyncTask<Void, Void, List<Address>> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show( AddressBookActivity.this, "Loading Addresses", "Just a minute..." );
		}
		
		@Override
		protected List<Address> doInBackground( Void... params ) {
			return webDao.getAddresses();
		}
		
		@Override
		protected void onPostExecute( List<Address> result ) {
			dialog.dismiss();
			
			adapter = new AddressBookAdapter( result );
			AddressBookActivity.this.setListAdapter( adapter );
		}
	}
}