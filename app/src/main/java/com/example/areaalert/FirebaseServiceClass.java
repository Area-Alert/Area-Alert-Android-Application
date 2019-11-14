package com.example.areaalert;

//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.NotificationManagerCompat;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.areaalert.App.CHANNEL_1_ID;

public class FirebaseServiceClass extends FirebaseMessagingService {
    String token = "";
    String channel_id = "personal_notifications";
    int notif_id = 001;
    NotificationManagerCompat notificationManager;

    private static final String TAG = "FirebaseServiceClass";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        // Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> map = new HashMap<>();
            map = remoteMessage.getData();
            String title = map.get("title");
            String body = map.get("body");

            notificationManager = NotificationManagerCompat.from(this);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setContentTitle("test")
                    .setContentText("test")
//                   .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();

            notificationManager.notify(1, notification);


            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }
}

//         NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),channel_id);
//          builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
//           builder.setContentTitle(title);
//          builder.setContentText(body)
//           .setPriority(NotificationCompat.PRIORITY_HIGH);
//           Intent intent=new Intent(this,Notification)
//           Notification notification = new Notification.Builder(this)
//                  .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
//                  .setContentTitle(title)
//                  .setContentText(body)
//                   .setContentIntent(new PendingIntent(this, 0, ))


//NotificationManagerCompat managerCompat=NotificationManagerCompat.from(getApplicationContext());
//managerCompat.notify(notif_id,builder.build());

//  NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

//  notificationManager.notify(001, no);

//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                   .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
//                    .setContentTitle(title)
//                    .setContentText(body)
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
//            notificationManager.notify(new Random().nextInt(), builder.build());
