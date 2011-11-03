package com.holmes.address;

import java.util.List;

import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.inject.Inject;
import com.holmes.address.dao.WebDao;
import com.holmes.address.model.Address;
import com.holmes.address.tasks.DeleteTask;
import com.holmes.address.tasks.DeleteTask.DeleteFinishListener;

public class AddressBookActivity extends RoboListActivity implements OnItemClickListener, DeleteFinishListener, OnClickListener {

	@Inject
	private WebDao webDao;

	@InjectView( R.id.create_address )
	private Button createButton;

	private AddressBookAdapter adapter;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		setContentView( R.layout.address_list );
		getListView().setOnItemClickListener( this );
		createButton.setOnClickListener( this );
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshAddressBook();
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		// launch another activity
	}

	@Override
	public void onClick( View v ) {
		if ( v == createButton ) {
			final View createView = getLayoutInflater().inflate( R.layout.address_create, null );
			new AlertDialog.Builder( AddressBookActivity.this )
					.setIcon( android.R.drawable.ic_input_add )
					.setTitle( "Create an address" )
					.setView( createView )
					.setNegativeButton( android.R.string.cancel, null )
					.setPositiveButton( android.R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick( DialogInterface dialog, int which ) {
							TextView addressField = (TextView) createView.findViewById( R.id.address );
							TextView nicknameField = (TextView) createView.findViewById( R.id.nickname );

							// no validation for now, just do it!
							new CreateTask().execute( String.valueOf( addressField.getText() ), String.valueOf( nicknameField.getText() ) );
						}
					} ).show();
		}
	}

	@Override
	public void onDeleteFinished() {
		refreshAddressBook();
	}

	private void refreshAddressBook() {
		new RetrieverTask().execute( (Void[]) null );
	}



	private class AddressBookAdapter extends ArrayAdapter<Address> {
		public AddressBookAdapter( List<Address> addresses ) {
			super( AddressBookActivity.this, android.R.layout.simple_list_item_1, addresses );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View view = convertView;
			if ( view == null ) {
				view = getLayoutInflater().inflate( R.layout.address_list_item, null );
			}

			final Address address = getItem( position );

			TextView nicknameField = (TextView) view.findViewById( R.id.nickname );
			nicknameField.setText( address.getNickname() );

			TextView addressField = (TextView) view.findViewById( R.id.address );
			addressField.setText( address.getAddress() );

			view.findViewById( R.id.delete ).setOnClickListener( new OnClickListener() {
				@Override
				public void onClick( View v ) {
					new AlertDialog.Builder( AddressBookActivity.this )
							.setIcon( R.drawable.x )
							.setTitle( "Delete Address" )
							.setMessage( String.format( "Are you sure you want to delete %s", address.getNickname() ) )
							.setNegativeButton( android.R.string.cancel, null )
							.setPositiveButton( android.R.string.yes, new DialogInterface.OnClickListener() {
								@Override
								public void onClick( DialogInterface dialog, int which ) {
									new DeleteTask( AddressBookActivity.this, webDao, AddressBookActivity.this ).execute( address );
								}
							} ).show();
				}
			} );

			return view;
		}
	}



	/**
	 * order of params is address/nickname
	 * 
	 * @author holmesj
	 */
	private class CreateTask extends AsyncTask<String, Void, Address> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show( AddressBookActivity.this, "Saving new address", "Just a minute..." );
		}

		@Override
		protected Address doInBackground( String... params ) {
			return webDao.create( params[0], params[1] );
		}

		@Override
		protected void onPostExecute( Address result ) {
			dialog.dismiss();
			adapter.insert( result, 0 );
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
