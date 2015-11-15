package com.brebalki.friendme;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UserInformation extends AppCompatActivity {

    TextView name, phone, email;

    public SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        //Initilaize edittexts
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);

        Context c = UserInformation.this;
        sharedPref = c.getSharedPreferences("brebalki", Context.MODE_PRIVATE);

        //Retrieve the rpevious information
        name.setText(sharedPref.getString("Name", ""));
        phone.setText(sharedPref.getString("Phone", ""));
        email.setText(sharedPref.getString("Email", ""));
    }

    public void apply(View view){
        SharedPreferences.Editor editor = sharedPref.edit();

        String na, ph, em;

        na = name.getText().toString();
        ph = phone.getText().toString();
        em = email.getText().toString();

        //Save to the shared preferences
        editor.putString("Name", name.getText().toString());
        if(!na.equals("")) {
            editor.putString("Name", name.getText().toString());
        }
        if(!ph.equals("")) {
            editor.putString("Phone", phone.getText().toString());
        }
        if(!em.equals("")) {
            editor.putString("Email", email.getText().toString());
        }
        editor.apply();

        Toast t = Toast.makeText(getApplicationContext(), "Information Saved", Toast.LENGTH_SHORT);
        t.show();
        finish();
    }
    public void cancel(View view){
        finish();
    }
}
