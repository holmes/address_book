package com.holmes.address;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.inject.Inject;
import com.holmes.address.dao.WebDao;
import com.holmes.address.model.Address;

public class AddressActivity extends RoboActivity implements OnClickListener {
	public static final String INTENT_EXTRA_ADDRESS = "address";

	@Inject
	private WebDao webDao;

	@InjectView( R.id.address )
	private TextView address;

	@InjectView( R.id.nickname )
	private TextView nickname;

	@InjectView( R.id.edit_nickname )
	private ImageButton editNickname;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		setContentView( R.layout.address_view );
		editNickname.setOnClickListener( this );
	}

	@Override
	protected void onResume() {
		super.onResume();

		Address addressToUse = getIntent().getParcelableExtra( INTENT_EXTRA_ADDRESS );
		address.setText( addressToUse.getAddress() );
		nickname.setText( addressToUse.getNickname() );
	}

	@Override
	public void onClick( View v ) {
		final EditText input = new EditText( this );
		input.setText( nickname.getText() );

		new AlertDialog.Builder( this )
				.setTitle( "Update Nickname" )
				.setView( input )
				.setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick( DialogInterface dialog, int whichButton ) {
						nickname.setText( input.getText().toString() );
					}
				} )
				.setNegativeButton( android.R.string.cancel, null )
				.show();
	}
}
