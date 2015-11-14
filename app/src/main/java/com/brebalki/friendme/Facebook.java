package com.brebalki.friendme;

import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;

/**
 * Created by ebencarek on 11/14/15.
 */
public class Facebook {
    public void loginSuccess(LoginResult loginResult) {
        Log.d("FB", "success\n");

    }

    public void printCancel() {
        Log.d("FB", "cancel\n");
    }
}
