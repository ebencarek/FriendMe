package com.brebalki.friendme;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookActivity;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;

import java.util.List;

/**
 * Created by ebencarek on 11/14/15.
 */
public class Facebook {

    private AccessToken accessToken;
    private Profile profile;

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
        this.profile = Profile.getCurrentProfile();
    }

    public AccessToken getAccessToken() {
        return this.accessToken;
    }

    public Profile getProfile() {
        return profile;
    }

    public void loginSuccess(LoginResult loginResult) {
        Log.d("FB", "login success\n");
        setAccessToken(loginResult.getAccessToken());

        profile = Profile.getCurrentProfile();

        Log.d("FB", "Profile uri: " + profile.getLinkUri().toString());
    }

    public void printCancel() {
        Log.d("FB", "login cancel");
    }

    // creates and returns Intent that will open facebook to the target's profile
    public Intent openFacebookProfile(String uriString, PackageManager pm) {

        Log.d("FB", "Target acebook uri String" + uriString);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uriString));

        Log.d("FB", "Facebook Intent URL " + intent.toString());

        return intent;
    }
}
