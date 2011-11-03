package com.holmes.address.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.holmes.address.dao.WebDao;
import com.holmes.address.model.Address;

public class DeleteTask extends AsyncTask<Address, Void, Address> {
	private final WebDao webDao;
	private final Context context;

	public DeleteTask( Context context, WebDao webDao ) {
		this.context = context;
		this.webDao = webDao;
	}

	@Override
	protected Address doInBackground( Address... address ) {
		webDao.deleteAddress( address[0].getId() );
		return address[0];
	}

	@Override
	protected void onPostExecute( Address deletedAddress ) {
		Toast.makeText( context, String.format( "Deleted %s", deletedAddress.getNickname() ), Toast.LENGTH_SHORT ).show();
	}
}
