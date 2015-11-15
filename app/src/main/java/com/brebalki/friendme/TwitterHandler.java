package com.brebalki.friendme;

import android.util.Log;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.Callback;

import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by ebencarek on 11/14/15.
 */
public class TwitterHandler {

    private TwitterSession session;

    public TwitterSession getTwitterSession() {
        return this.session;
    }

    public long getUserId() {
        return this.session.getUserId();
    }

    public void setTwitterSession(TwitterSession session) {
        this.session = session;
    }

    public void loginSuccess(Result<TwitterSession> result) {
        this.setTwitterSession(result.data);
        Log.d("TW", "Twitter user ID " + result.data.getUserId());
    }

    public void sendFollow(long targetUserID) {
        FMTwitterApiClient apiClient = new FMTwitterApiClient(this.getTwitterSession());
        FollowService followService = apiClient.getFollowService();

        Log.d("TW", "Sender User ID: " + getUserId());


        followService.create(this.getUserId(), true, new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                Log.d("TW", "Successfully added " + result.data.name);
            }

            @Override
            public void failure(TwitterException e) {
                Log.d("TW", "Follow failure: " + e.getLocalizedMessage());
            }
        });
    }

    private class FMTwitterApiClient extends TwitterApiClient {
        public FMTwitterApiClient(TwitterSession session) {
            super(session);
        }

        public FollowService getFollowService() {
            return getService(FollowService.class);
        }
    }

    private interface FollowService {
        @POST("/1.1/friendships.create.json")
        void create(@Query("user_id") long id, @Query("follow") boolean follow, Callback<User> cb);
    }

}
