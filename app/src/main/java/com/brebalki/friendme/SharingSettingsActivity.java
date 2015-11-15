package com.brebalki.friendme;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class SharingSettingsActivity extends AppCompatActivity {

    public SharedPreferences sharedPref;
    public CheckBox name, number, email, facebook, twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_settings);

        Context c = SharingSettingsActivity.this;
        sharedPref = c.getSharedPreferences("brebalki", Context.MODE_PRIVATE);

        name = (CheckBox) findViewById(R.id.nameShare);
        number = (CheckBox) findViewById(R.id.numberShare);
        email = (CheckBox) findViewById(R.id.emailShare);
        facebook = (CheckBox) findViewById(R.id.facebookShare);
        twitter = (CheckBox) findViewById(R.id.twitterShare);

        name.setChecked(sharedPref.getBoolean("nameShare", true));
        number.setChecked(sharedPref.getBoolean("numberShare", true));
        email.setChecked(sharedPref.getBoolean("emailShare", true));
        facebook.setChecked(sharedPref.getBoolean("facebookShare", true));
        twitter.setChecked(sharedPref.getBoolean("twitterShare", true));
    }

    public void apply(View view){
        SharedPreferences.Editor editor = sharedPref.edit();

        boolean bname, bnumber, bemail, bfacebook, btwitter;

        bname = name.isChecked();
        bnumber = number.isChecked();
        bemail = email.isChecked();
        bfacebook = facebook.isChecked();
        btwitter = twitter.isChecked();

        //Save to the shared preferences
        editor.putBoolean("nameShare", bname);
        editor.putBoolean("numberShare", bnumber);
        editor.putBoolean("emailShare", bemail);
        editor.putBoolean("facebookShare", bfacebook);
        editor.putBoolean("twitterShare", btwitter);

        editor.apply();

        Toast t = Toast.makeText(getApplicationContext(), "Settings Saved", Toast.LENGTH_SHORT);
        t.show();
        finish();
    }
    public void cancel(View view){
        finish();
    }
}
