package com.holmes.address.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.holmes.address.R;
import com.holmes.address.dao.WebDao;
import com.holmes.address.model.Address;

public class DeleteTask extends AsyncTask<Address, Void, Address> {
	private final WebDao webDao;
	private final Context context;
	private final DeleteFinishListener deleteFinishListener;

	public DeleteTask( Context context, WebDao webDao, DeleteFinishListener deleteFinishListener ) {
		this.context = context;
		this.webDao = webDao;
		this.deleteFinishListener = deleteFinishListener;
	}

	@Override
	protected Address doInBackground( Address... address ) {
		webDao.deleteAddress( address[0].getId() );
		return address[0];
	}

	@Override
	protected void onPostExecute( Address deletedAddress ) {
		Toast.makeText( context, String.format( "Deleted %s", deletedAddress.getNickname() ), Toast.LENGTH_SHORT ).show();
		if( deleteFinishListener != null ) {
			deleteFinishListener.onDeleteFinished( deletedAddress );
		}
	}
	
	public interface DeleteFinishListener {
		public void onDeleteFinished( Address address );
	}
	
	public static class DeleteDialogBuilder {
		private final Context context;
		private final Address address;
		private final WebDao webDao;
		private final DeleteFinishListener deleteFinishListener;

		public DeleteDialogBuilder( Context context, Address address, WebDao webDao, DeleteFinishListener deleteFinishListener ) {
			this.context = context;
			this.address = address;
			this.webDao = webDao;
			this.deleteFinishListener = deleteFinishListener;
		}
		
		public void show() {
			new AlertDialog.Builder( context )
			.setIcon( R.drawable.x )
			.setTitle( "Delete Address" )
			.setMessage( String.format( "Are you sure you want to delete %s", address.getNickname() ) )
			.setNegativeButton( android.R.string.cancel, null )
			.setPositiveButton( android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick( DialogInterface dialog, int which ) {
					new DeleteTask( context, webDao, deleteFinishListener ).execute( address );
				}
			} ).show();
		}
		
	}
	
}
