package com.brebalki.friendme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.view.View;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.widget.TextView;

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


    private CallbackManager callbackManager;
    private Facebook fb = new Facebook();
    private TwitterHandler tw = new TwitterHandler();
    private LoginButton facebookLoginButton;
    private TwitterLoginButton twitterLoginButton;
    private TextView textView;
    private AccessTokenTracker accessTokenTracker;
    public NdefMessage msg;
    public TextView testing;
    NfcAdapter adapter;
    public ContactInfo myContacts;
    public Context contxt;

    public String constructPayload(){
        Context c = MainActivity.this;
        SharedPreferences sharedPref = c.getSharedPreferences("brebalki", Context.MODE_PRIVATE);

        String name = sharedPref.getString("Name", "");
        String phone = sharedPref.getString("Phone", "");
        String email = sharedPref.getString("Email", "");
        String facebook = fb.getAccessToken() != null ? fb.getAccessToken().getUserId() : "";
        String twitter = tw.getTwitterSession() != null ? tw.getUserId() + "" : "";
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

        facebookLoginButton = (LoginButton) this.findViewById(R.id.facebook_login_button);
        facebookLoginButton.setReadPermissions("user_friends");

        facebookLoginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        fb.loginSuccess(loginResult);
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

        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                tw.loginSuccess(result);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TW", exception.getLocalizedMessage());
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        adapter = NfcAdapter.getDefaultAdapter(this);

        //I don't know if this needs to be here, but I think we should leave it for now
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }

        setBroadcastMessage(constructPayload());

        myContacts = new ContactInfo();
        contxt = getApplicationContext();
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

        //Toast t = Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG);
        //t.show();

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

        if (fb.getAccessToken() != null && !facebook.equals("")) {
            fb.openFacebookProfile(facebook, getPackageManager());
        }
        if (tw.getTwitterSession() != null && !facebook.equals("")) {
            tw.sendFollow(Long.parseLong(twitter));
        }
        //Test if these are null
        myContacts = new ContactInfo();
        contxt = getApplicationContext();
        myContacts.WritePhoneContact(name, phone, contxt);
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
            return true;
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
