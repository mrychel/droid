package com.example.readcontacts;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import android.os.Environment;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

public class MainActivity extends Activity {
	private ListView mListView;
	private ProgressDialog pDialog;
	private Handler updateBarHandler;

	ArrayList<String> contactList;
	Cursor cursor;
	int counter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Reading contacts...");
		pDialog.setCancelable(false);
		pDialog.show();

		mListView = (ListView) findViewById(R.id.list);
		updateBarHandler =new Handler();
		
		// Since reading contacts takes more time, let's run it on a separate thread.
		new Thread(new Runnable() {

			@Override
			public void run() {
				getContacts();
			}
		}).start();

		// Set onclicklistener to the list item.
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//TODO Do whatever you want with the list data
				Toast.makeText(getApplicationContext(), "item clicked : \n"+contactList.get(position), Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void getContacts() {


		contactList = new ArrayList<String>();

		String phoneNumber = null;
		String email = null;

		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

		Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;

		StringBuffer output;
        String s = new String("\n");
		ContentResolver contentResolver = getContentResolver();

		cursor = contentResolver.query(CONTENT_URI, null,null, null, null);	

		// Iterate every contact in the phone
		if (cursor.getCount() > 0) {

			counter = 0;

			while (cursor.moveToNext()) {
				output = new StringBuffer();

				// Update the progress message
				updateBarHandler.post(new Runnable() {
					public void run() {
						pDialog.setMessage("Reading contacts : "+ counter++ +"/"+cursor.getCount());
					}
				});

				String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
				String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

				if (hasPhoneNumber > 0) {

					output.append("\n Numer:"+contact_id+" First Name:" + name);
					s.concat("\n Numer:"+contact_id+" First Name:" + name);
					//This is to read multiple phone numbers associated with the same contact
					Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

					while (phoneCursor.moveToNext()) {
						phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
						output.append("\n Phone number:" + phoneNumber);
						s.concat("\n Phone number:" + phoneNumber);
					}

					phoneCursor.close();

					// Read every email id associated with the contact
					Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);

					while (emailCursor.moveToNext()) {

						email = emailCursor.getString(emailCursor.getColumnIndex(DATA));

						output.append("\n Email:" + email);
						s.concat("\n Email:" + email);
					}

					emailCursor.close();
				}

				// Add the contact to the ArrayList
				contactList.add(output.toString());
			}

			// ListView has to be updated using a ui thread
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, R.id.text1, contactList);
					mListView.setAdapter(adapter);			
				}
			});

			// Dismiss the progressbar after 500 millisecondds
			updateBarHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					pDialog.cancel();				
				}
			}, 500);
		}
        writeToFile(s);
	}

	public void writeToFile(String data)
	{
		StringBuffer buf = new StringBuffer();
        // Get the directory for the user's public pictures directory.
		final File path =
				Environment.getExternalStoragePublicDirectory
						(
								//Environment.DIRECTORY_PICTURES
								Environment.DIRECTORY_DCIM + "/YourFolder/"
						);

		// Make sure the path directory exists.
		if(!path.exists())
		{
			// Make it, if it doesn't exit
			path.mkdirs();
		}

		final File file = new File(path, "mrychel.txt");
buf.append(file.getAbsolutePath());
		// Save your stream, don't forget to flush() it before closing it.

		try
		{

			file.createNewFile();
			FileOutputStream fOut = new FileOutputStream(file);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(data);

			myOutWriter.close();

			fOut.flush();
			fOut.close();
		}
		catch (IOException e)
		{
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

}