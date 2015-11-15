package com.brebalki.friendme;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;

import java.util.List;

/**
 * Created by ebencarek on 11/14/15.
 */
public class Facebook {

    private AccessToken accessToken;

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public AccessToken getAccessToken() {
        return this.accessToken;
    }

    public void loginSuccess(LoginResult loginResult) {
        Log.d("FB", "login success\n");
        //setAccessToken(loginResult.getAccessToken());
        //Log.d("FB", this.getAccessToken().getUserId());

        new GraphRequest(
                loginResult.getAccessToken(),
                "/me",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("FB", "graph api response " + response.toString());
                    }
                }
        ).executeAsync();
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
