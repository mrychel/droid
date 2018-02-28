package com.example.readcontacts;

import java.io.BufferedWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents;
import android.content.ContentProviderOperation;
import android.provider.ContactsContract.CommonDataKinds.Phone;
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
		setContacts();
		//getContacts();
		// Since reading contacts takes more time, let's run it on a separate thread.
		/*
		new Thread(new Runnable() {

			@Override
			public void run() {

				getContacts();

			}
		}).start();
*/
		// Set onclicklistener to the list item.
		/*
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//TODO Do whatever you want with the list data
				Toast.makeText(getApplicationContext(), "item clicked : \n"+contactList.get(position), Toast.LENGTH_SHORT).show();
			}
		});*/
	}

	public void setContacts() {
        boolean a;
 /*       a = insertContact("Mama", "505171199");
        a = insertContact("Tato", "518115548");
        a = insertContact("Agnieszka", "507160801");
        a = insertContact("Kajtek", "722188616");
        a = insertContact("Gabi", "661907177");
        a = insertContact("Mama Agnieszki", "507727781");
        a = insertContact("Babcia", "502587033");

        a = insertContact("Dorota", "507488224");
        a = insertContact("Michał", "518115548");
        a = insertContact("Stasiek", "785007034");
        a = insertContact("Asia", "515631963");
        a = insertContact("Atena", "502183126");
        a = insertContact("Łukasz", "881786888");
*/
        a = insertContact("Stefan", "601897446");
        a = insertContact("Ania Paszczuk", "511393479");

        a = insertContact("Marek", "604576933");
        a = insertContact("Rysiek", "604233036");
        a = insertContact("Piotrek", "665929266");
        a = insertContact("Paweł", "790404041");
        a = insertContact("Jacek", "693417730");
        a = insertContact("Jasiek", "500471123");

        a = insertContact("Roman", "667671116");
        a = insertContact("Bożena Tarczydło", "509393935");

        a = insertContact("Michał Lubaś", "668030646");
        a = insertContact("Piotr Pawlak", "691633163");
        a = insertContact("Grzegorz Sudolski", "607298056");
        a = insertContact("Krzysiek Glaesmann", "500054935");
        a = insertContact("Łukasz Truba", "698835825");
        a = insertContact("Krzysztof Juzaszek", "799609912");

/*

		for (int i = 0; i < 3; i++) {
			Intent intent = new Intent(Intents.Insert.ACTION);
			intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
			intent
					.putExtra(Intents.Insert.NAME, "Zdzichu"+i)
					.putExtra(Intents.Insert.EMAIL, "mrychel@wwww.com")
					.putExtra(Intents.Insert.PHONE, "507231112");
			startActivity(intent);
		}*/
	}

    public boolean insertContact(String firstName, String mobileNumber) {
        ContentResolver contentResolver = getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                        firstName).build());
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                        mobileNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        Phone.TYPE_MOBILE).build());

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            return false;
        }
        Toast.makeText(getApplicationContext(), firstName, Toast.LENGTH_SHORT).show();
        return true;
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

		StringBuffer output = new StringBuffer();
		ContentResolver contentResolver = getContentResolver();

		cursor = contentResolver.query(CONTENT_URI, null,null, null, null);	

		// Iterate every contact in the phone
		if (cursor.getCount() > 0) {

			counter = 0;

			while (cursor.moveToNext()) {

				// Update the progress message
				updateBarHandler.post(new Runnable() {
					public void run() {
						pDialog.setMessage("Reading contacts : "+ counter++ +"/"+cursor.getCount());
					}
				});

				String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
				String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
                //Toast.makeText(getApplicationContext(), cursor.getString(cursor.getColumnIndex( ContactsContract.RawContacts.ACCOUNT_TYPE )), Toast.LENGTH_LONG).show();

				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

				if (hasPhoneNumber > 0) {

					output.append("\n Numer:"+contact_id+" First Name:" + name);
					//This is to read multiple phone numbers associated with the same contact
					Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

					while (phoneCursor.moveToNext()) {
						phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
						output.append("\n Phone number:" + phoneNumber);
					}

					phoneCursor.close();

					// Read every email id associated with the contact
					Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);

					while (emailCursor.moveToNext()) {

						email = emailCursor.getString(emailCursor.getColumnIndex(DATA));

						output.append("\n Email:" + email);
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
		/*
		String[] mm = cursor.getColumnNames();

		for (int kk = 0; kk < mm.length; kk++) {
		//writeToFile(mm[kk].toString());}
            Toast.makeText(getApplicationContext(), mm[kk], Toast.LENGTH_LONG).show();}*/
	}

	public void writeToFile(String data)
	{
		// Get the directory for the user's public pictures directory.
		//pDialog.setMessage("Siemka Lamusie");


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
		//buf.append(file.getAbsolutePath());
		// Save your stream, don't forget to flush() it before closing it.

		try
		{

			file.createNewFile();
			FileOutputStream fOut = new FileOutputStream(file);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut, "utf-8");

			Toast.makeText(getApplicationContext(), "przed", Toast.LENGTH_LONG).show();
			myOutWriter.append(data);//append("mm".toCharArray());
			Toast.makeText(getApplicationContext(), "po", Toast.LENGTH_LONG).show();
			myOutWriter.flush();
			myOutWriter.close();

			fOut.flush();
			fOut.close();
		}
		catch (IOException e)
		{
			Log.e("Exception", "File write failed: " + e.toString());
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
		}
	}

}