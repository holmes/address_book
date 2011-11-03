package com.holmes.address;

import java.util.List;

import roboguice.activity.RoboListActivity;
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
import android.widget.TextView;

import com.google.inject.Inject;
import com.holmes.address.dao.WebDao;
import com.holmes.address.model.Address;
import com.holmes.address.tasks.DeleteTask;
import com.holmes.address.tasks.DeleteTask.DeleteFinishListener;

public class AddressBookActivity extends RoboListActivity implements OnItemClickListener, DeleteFinishListener {

	@Inject
	private WebDao webDao;
	
	AddressBookAdapter adapter;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.address_list );
		getListView().setOnItemClickListener( this );
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
			if( view == null ) {
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
					new AlertDialog.Builder(AddressBookActivity.this)
			        .setIcon(android.R.drawable.ic_dialog_alert)
			        .setTitle("Delete Address")
			        .setMessage(String.format("Are you sure you want to delete %s", address.getNickname() ))
			        .setNegativeButton(android.R.string.cancel, null)
			        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int which) {
			            	new DeleteTask( AddressBookActivity.this, webDao, AddressBookActivity.this ).execute( address );
			            }
			        }).show();
				}
			});
			
			return view;
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