package com.homearound.www.homearound;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMsgService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";

    private UserDefaultManager userDefaultManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
      //  super.onMessageReceived(remoteMessage);
     //   Log.d(TAG, "From: " + remoteMessage.getFrom());
     //   Log.d(TAG, "Msg body: " + remoteMessage.getNotification().getBody());
        String msgBody = remoteMessage.getNotification().getBody();
        String msgTitle = remoteMessage.getNotification().getTitle();

        userDefaultManager = new UserDefaultManager(getApplicationContext());

        String mEmail = userDefaultManager.getUserEmail();
        String mPassword = userDefaultManager.getUserPassword();

        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword)) {

        } else {
            String mRole = userDefaultManager.getUserRole();
            if (mRole.equals("customer")) {
                Intent intent = new Intent(this, CustomerNvDrawerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder noteBuilder = new NotificationCompat.Builder(this)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(msgTitle)
                        .setContentText(msgBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                manager.notify(1, noteBuilder.build());
                //  Intent intent = new Intent(this, SignupActivity.class);
            } else {
                Intent intent = new Intent(this, MerchantNvDrawerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder noteBuilder = new NotificationCompat.Builder(this)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(msgTitle)
                        .setContentText(msgBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                manager.notify(1, noteBuilder.build());
                //  Intent intent = new Intent(this, SignupActivity.class);
            }
        }
    }
}
