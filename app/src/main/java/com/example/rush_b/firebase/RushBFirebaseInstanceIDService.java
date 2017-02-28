package com.example.rush_b.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by martinock on 25/02/17.
 */

public class RushBFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.v(TAG, "Refreshed Token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}
