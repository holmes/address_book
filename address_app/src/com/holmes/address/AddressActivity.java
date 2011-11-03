package com.holmes.address;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.inject.Inject;
import com.holmes.address.dao.WebDao;
import com.holmes.address.model.Address;
import com.holmes.address.tasks.DeleteTask;
import com.holmes.address.tasks.DeleteTask.DeleteFinishListener;

public class AddressActivity extends RoboActivity implements OnClickListener, DeleteFinishListener {
	public static final String INTENT_EXTRA_ADDRESS = "address";

	@Inject
	private WebDao webDao;

	@InjectView( R.id.address )
	private TextView address;

	@InjectView( R.id.nickname )
	private TextView nickname;

	@InjectView( R.id.edit_nickname )
	private ImageButton editNickname;

	private Address addressToUse;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		setContentView( R.layout.address_view );
		editNickname.setOnClickListener( this );
	}

	@Override
	protected void onResume() {
		super.onResume();

		addressToUse = getIntent().getParcelableExtra( INTENT_EXTRA_ADDRESS );
		updateAddress();
	}
	
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.address_view_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		if( item.getItemId() == R.id.edit_nickname ) {
			editNickname();
		} else {
			new DeleteTask.DeleteDialogBuilder( this, addressToUse, webDao, this ).show();
		}
		
		return true;
	}
	
	@Override
	public void onClick( View v ) {
		editNickname();
	}
	
	@Override
	public void onDeleteFinished( Address deletedAddress ) {
		finish();
	}

	private void editNickname() {
		final EditText input = new EditText( this );
		input.setText( nickname.getText() );

		new AlertDialog.Builder( this )
				.setTitle( "Update Nickname" )
				.setView( input )
				.setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick( DialogInterface dialog, int whichButton ) {
						nickname.setText( input.getText().toString() );
						new UpdateNicknameTask().execute( (Void[]) null );
					}
				} )
				.setNegativeButton( android.R.string.cancel, null )
				.show();
	}

	private void updateAddress() {
		address.setText( addressToUse.getAddress() );
		nickname.setText( addressToUse.getNickname() );
	}



	private class UpdateNicknameTask extends AsyncTask<Void, Void, Address> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show( AddressActivity.this, "Updating Nickname", "Just a sec..." );
		}

		@Override
		protected Address doInBackground( Void... params ) {
			return webDao.updateNickname( addressToUse.getId(), String.valueOf( nickname.getText() ) );
		}

		@Override
		protected void onPostExecute( Address result ) {
			dialog.dismiss();
		}
	}

}
