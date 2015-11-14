// This class takes string input and creates a contact for it.
// Version 0.01
package com.brebalki.friendme;
import android.app.Activity;
import android.os.Bundle;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.provider.ContactsContract;

import android.widget.TextView;
import android.widget.Toast;

/**
 * This was created by Brandon on 11/14/2015.
 */
    public class ContactInfo extends Activity {
        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            try
            {
                ContentResolver cr = this.getContentResolver();
                ContentValues cv = new ContentValues();
                cv.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, "New Name");
                cv.put(ContactsContract.CommonDataKinds.Phone.NUMBER, "1234567890");
                cv.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                cr.insert(ContactsContract.RawContacts.CONTENT_URI, cv);

                Toast.makeText(this, "Contact added", Toast.LENGTH_LONG).show();
            }
            catch(Exception e)
            {
                TextView tv = new TextView(this);
                tv.setText(e.toString());
                setContentView(tv);
            }
        }
    }
