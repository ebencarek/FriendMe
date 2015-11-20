package com.brebalki.friendme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements CreateNdefMessageCallback{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "ojRm3zlTFPsiyzaE4gEyEChPE";
    private static final String TWITTER_SECRET = "XMKzaN0iHf0d5XMiwxyCUPymTASfh0BBcHkT8nwSaLWQmlzFSx";
    public NdefMessage msg;
    public TextView testing;
    public ContactInfo myContacts;
    public Context contxt;
    public SelectDataDialogFragment getData;
    NfcAdapter adapter;
    private CallbackManager callbackManager;
    private Facebook fb = new Facebook();
    private TwitterHandler tw = new TwitterHandler();
    private LoginButton facebookLoginButton;
    private TwitterLoginButton twitterLoginButton;
    private TextView textView;
    private AccessTokenTracker accessTokenTracker;

    public String constructPayload(){
        Context c = MainActivity.this;
        SharedPreferences sharedPref = c.getSharedPreferences("brebalki", Context.MODE_PRIVATE);
        boolean[] share = new boolean[5];
        String name, phone, email, facebook, twitter;

        share[0] = (sharedPref.getBoolean("nameShare", true));
        share[1] = (sharedPref.getBoolean("numberShare", true));
        share[2] = (sharedPref.getBoolean("emailShare", true));
        share[3] = (sharedPref.getBoolean("facebookShare", true));
        share[4] = (sharedPref.getBoolean("twitterShare", true));

        name = share[0] ? sharedPref.getString("Name", "") : "";
        phone = share[1] ? sharedPref.getString("Phone", "") : "";
        email = share[2] ? sharedPref.getString("Email", "") : "";

        if (share[3] && fb.getAccessToken() != null) {
            facebook = fb.getProfile().getLinkUri().toString();
        }
        else {
            facebook = "";
        }

        if (share[4] && tw.getTwitterSession() != null) {
            twitter = tw.getUserId() + "";
        }
        else {
            twitter = "";
        }

        String payload;

        payload = "NM" + name + "~PH" + phone + "~EM" + email + "~FB" + facebook + "~TW" + twitter;

        return payload;
    }

    public void setBroadcastMessage(String payload){
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, null, payload.getBytes());
        NdefMessage message = new NdefMessage(record);
        adapter = NfcAdapter.getDefaultAdapter(this);
        adapter.setNdefPushMessage(message, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Activate facebook sdk
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        FacebookSdk.sdkInitialize(getApplicationContext());

        // register login callback
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);

        configureFacebook();
        configureTwitter();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = NfcAdapter.getDefaultAdapter(this);

        //I don't know if this needs to be here, but I think we should leave it for now
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }

        setBroadcastMessage(constructPayload());

        myContacts = new ContactInfo();
        contxt = getApplicationContext();
    }

    private void configureTwitter() {
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        tw.setTwitterSession(session);

        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                tw.loginSuccess(result);
                setBroadcastMessage(constructPayload());
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TW", exception.getLocalizedMessage());
            }
        });
    }
    private void configureFacebook() {
        AccessToken ac = AccessToken.getCurrentAccessToken();
        fb.setAccessToken(ac);

        facebookLoginButton = (LoginButton) this.findViewById(R.id.facebook_login_button);
        facebookLoginButton.setReadPermissions("user_friends");

        facebookLoginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        fb.loginSuccess(loginResult);
                        setBroadcastMessage(constructPayload());
                    }

                    @Override
                    public void onCancel() {
                        fb.printCancel();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                fb.setAccessToken(currentAccessToken);
            }
        };
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        NdefRecord n = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, null, "PAYLOAD".getBytes());
        //Need acc thingy
        //first record in a message is the mime or uri (?)
        return new NdefMessage(n);
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }
    /**
     * Parses the NDEF Message from the intent and prints to recievedData
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        String output = new String(msg.getRecords()[0].getPayload());
        String[] recievedData = output.split("[~]");


        String email, name, phone, facebook, twitter;
        email = "";
        name = "";
        phone = "";
        facebook = "";
        twitter = "";
        //Parse out the information
        for(String h : recievedData){
            if(h.substring(0,2).equals("NM") && !h.substring(2,h.length()).equals("")){
                name = h.substring(2,h.length());
            }else if(h.substring(0,2).equals("PH") && !h.substring(2,h.length()).equals("")){
                phone = h.substring(2,h.length());
            }else if(h.substring(0,2).equals("EM") && !h.substring(2,h.length()).equals("")){
                email = h.substring(2,h.length());
            }else if(h.substring(0,2).equals("FB") && !h.substring(2,h.length()).equals("")){
                facebook = h.substring(2,h.length());
            }else if(h.substring(0,2).equals("TW") && !h.substring(2,h.length()).equals("")){
                twitter = h.substring(2,h.length());
            }
        }

        //This segment doesn't follow proper encapsulation rules or whatever, but it works well enough
        //Will be moved to its own function "soon"
        getData = new SelectDataDialogFragment();
        /*
        items[0] = "Name";
        items[1] = "Phone Number";
        items[2] = "Email Address";
        items[3] = "Twitter";
        items[4] = "Facebook";
        */

        //Remove stuff from the items array based on if you received the information
        int r = 5;
        if (name.equals("")) {
            getData.items[0] = "";
            r--;
        }
        if (phone.equals("")) {
            getData.items[1] = "";
            r--;
        }
        if (email.equals("")) {
            getData.items[2] = "";
            r--;
        }
        if (twitter.equals("")) {
            getData.items[3] = "";
            r--;
        }
        if (facebook.equals("")) {
            getData.items[4] = "";
            r--;
        }

        //Only allow the user to select from items which they have recieved
        getData.endItems = new String[r];
        r = 0;
        for (int q = 0; q < getData.items.length; q++, r++) {
            if (getData.items[q].equals("")) {
                r--;
            } else {
                getData.endItems[r] = getData.items[q];
            }
        }

        // Specify the list array, the items to be selected by default (null for none),
        // and the listener through which to receive callbacks when items are selected
        //Normally this would belong in the SelectDataDialogFragment object
        //This might behave strangely...
        getData.builder.setMultiChoiceItems(getData.endItems, null, //Non - null for different defaults
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            getData.mSelectedItems.add(getData.endItems[which]);
                        } else if (getData.mSelectedItems.contains(getData.endItems[which])) {
                            // Else, if the item is already in the array, remove it
                            getData.mSelectedItems.remove(getData.endItems[which]);
                        }
                    }
                });
        getData.show(this.getFragmentManager(), "tag");

        //With the getData Alert Dialog object, !THING.equals("") is probably redundant (included in the mSelectedItems creation)
        if (fb.getAccessToken() != null && !facebook.equals("") && getData.mSelectedItems.contains("Facebook")) {
            startActivity(fb.openFacebookProfile(facebook, getPackageManager()));
        }
        if (tw.getTwitterSession() != null && !twitter.equals("") && getData.mSelectedItems.contains("Twitter")) {
            tw.sendFollow(Long.parseLong(twitter));
        }

        myContacts = new ContactInfo();
        contxt = getApplicationContext();
        //Add the relevant contact information
        if (getData.mSelectedItems.contains("Name")) {
            if (getData.mSelectedItems.contains("Phone Number")) {
                if (getData.mSelectedItems.contains("Email Address")) {
                    myContacts.WritePhoneContact(name, phone, email, contxt);
                } else {
                    myContacts.WritePhoneContact(name, phone, "", contxt);
                }
            } else {
                if (getData.mSelectedItems.contains("Email Address")) {
                    myContacts.WritePhoneContact(name, "", email, contxt);
                } else {
                    myContacts.WritePhoneContact(name, "", "", contxt);
                }
            }
        } else {
            if (getData.mSelectedItems.contains("Phone Number")) {
                if (getData.mSelectedItems.contains("Email Address")) {
                    myContacts.WritePhoneContact("", phone, email, contxt);
                } else {
                    myContacts.WritePhoneContact("", phone, "", contxt);
                }
            } else {
                if (getData.mSelectedItems.contains("Email Address")) {
                    myContacts.WritePhoneContact("", "", email, contxt);
                } else {
                    Toast t = Toast.makeText(contxt, "No contact information received", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SharingSettingsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
    public void launchUserInformation(View view){
        Intent i = new Intent(this, UserInformation.class);
        startActivity(i);
    }

    /* Facebook app activation and deactivation tracking */

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
        setBroadcastMessage(constructPayload());
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
        myContacts = new ContactInfo();
        contxt = getApplicationContext();
    }
    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }
}
