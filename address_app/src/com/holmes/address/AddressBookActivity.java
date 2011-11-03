package com.holmes.address;

import java.util.List;

import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.holmes.address.dao.BaseUrlProvider;
import com.holmes.address.dao.WebDao;
import com.holmes.address.model.Address;
import com.holmes.address.tasks.DeleteTask;
import com.holmes.address.tasks.DeleteTask.DeleteFinishListener;

public class AddressBookActivity extends RoboListActivity implements DeleteFinishListener, OnClickListener, OnItemClickListener, OnItemLongClickListener {
	private static final int REQUEST_CODE_VIEW = 0;

	@Inject
	private BaseUrlProvider urlProvider;
	
	@Inject
	private WebDao webDao;

	@InjectView( R.id.create_address )
	private Button createButton;

	private AddressBookAdapter adapter;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		setContentView( R.layout.address_list );
		createButton.setOnClickListener( this );
	}

	@Override
	protected void onStart() {
		super.onStart();
		refreshAddresses();
	}

	
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.address_main_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		if( item.getItemId() == R.id.refresh ) {
			refreshAddresses();
		} else {
			final EditText input = new EditText( this );
			input.setText( urlProvider.getBaseUrl() );

			new AlertDialog.Builder( this )
					.setTitle( "Update Nickname" )
					.setView( input )
					.setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick( DialogInterface dialog, int whichButton ) {
							urlProvider.setBaseUrl( input.getText().toString() );
							Toast.makeText( AddressBookActivity.this, "Updated the domain to: " + urlProvider.getBaseUrl(), Toast.LENGTH_LONG ).show();
						}
					} )
					.setNegativeButton( android.R.string.cancel, null )
					.show();
		}
		
		return true;
	}
	
	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		// check request and resultCode and possibly alter the adapter instead of being lazy and forcing the refresh via onResume
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
							new CreateTask(String.valueOf( addressField.getText() ), String.valueOf( nicknameField.getText() ) ).execute();
						}
					} ).show();
		}
	}

	@Override
	public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ) {
		// normally you'd show a menu here, in this case maybe with view/edit/delete
		Address address = (Address) parent.getItemAtPosition( position );
		Toast.makeText( AddressBookActivity.this, address.getNickname(), Toast.LENGTH_SHORT ).show();

		return true;
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		Intent intent = new Intent( this, AddressActivity.class );
		intent.putExtra( AddressActivity.INTENT_EXTRA_ADDRESS, adapter.getItem( position ) );
		startActivityForResult( intent, REQUEST_CODE_VIEW );
	}

	@Override
	public void onDeleteFinished( Address toRemove ) {
		adapter.remove( toRemove );
	}

	private void refreshAddresses() {
		new RetrieverTask().execute( (Void[]) null );
	}

	private void initializeListView( List<Address> result ) {
		adapter = new AddressBookAdapter( result );
		setListAdapter( adapter );

		getListView().setOnItemClickListener( this );
		getListView().setOnItemLongClickListener( this );
	}



	private class AddressBookAdapter extends ArrayAdapter<Address> {
		public AddressBookAdapter( List<Address> addresses ) {
			super( AddressBookActivity.this, 0, addresses );
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
					new DeleteTask.DeleteDialogBuilder( getContext(), address, webDao, AddressBookActivity.this ).show();
				}
			} );

			return view;
		}
	}



	/**
	 * order of params is address,nickname
	 * 
	 * @author holmesj
	 */
	private class CreateTask extends RoboAsyncTask<Address> {
		private ProgressDialog dialog;
		
		private final String name;
		private final String address;

		public CreateTask(String address, String name) {
			this.address = address;
			this.name = name;
		}
		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show( AddressBookActivity.this, "Saving new address", "Just a minute..." );
		}

		@Override
		public Address call() throws Exception {
			return webDao.create( address, name );
		}

		@Override
		protected void onSuccess( Address newAddress ) throws Exception {
			adapter.insert( newAddress, 0 );
		}
		
		@Override
		protected void onException( Exception e ) throws RuntimeException {
			Toast.makeText( AddressBookActivity.this, e.getMessage(), Toast.LENGTH_LONG ).show();
		}

		@Override
		protected void onFinally() throws RuntimeException {
			dialog.dismiss();
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
			AddressBookActivity.this.initializeListView( result );
		}
	}
}
