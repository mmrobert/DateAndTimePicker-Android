package com.homearound.www.homearound;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by boqiancheng on 2016-12-30.
 */

public class FirebaseIdService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIDService";

    private UserDefaultManager userDefaultManager;

    @Override
    public void onTokenRefresh() {
       // super.onTokenRefresh();
        userDefaultManager = new UserDefaultManager(getApplicationContext());

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

     //   Log.d(TAG, "Refreshed token: " + refreshedToken);

        userDefaultManager.setUserDeviceToken(refreshedToken);
        userDefaultManager.setUserRemoteNotificationAllow(true);
    }
}
