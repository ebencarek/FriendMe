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
        Log.d("FB", "APP specific user id: " + this.getAccessToken().getUserId());

        profile = Profile.getCurrentProfile();

        Log.d("FB", "Profile user id: " + profile.getId());
        Log.d("FB", "Profile uri: " + profile.getLinkUri().toString());
    }

    public void printCancel() {
        Log.d("FB", "login cancel\n");
    }

    // creates and returns Intent that will open facebook to the target's profile
    public Intent openFacebookProfile(String userID, PackageManager pm) {

        Log.d("FB", "Facebook user ID" + userID);

        String fbURL = "http://www.facebook.com/profile.php?id=" + userID;   //"fb://profile/" + userID;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(fbURL));
//
        //List<ResolveInfo> list = pm.queryIntentActivities(intent, pm.MATCH_DEFAULT_ONLY);
//
        //if (list.size() == 0) {
        //    String browserURL = "http://www.facebook.com/profile.php?id=" + userID;
        //    intent.setData(Uri.parse(browserURL));
        //}

        Log.d("FB", "Facebook Intent URL " + intent.toString());

        return intent;
    }
}
