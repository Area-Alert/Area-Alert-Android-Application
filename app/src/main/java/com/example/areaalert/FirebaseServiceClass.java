package com.example.areaalert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.areaalert.App.CHANNEL_1_ID;
import static com.example.areaalert.App.CHANNEL_2_ID;
import static com.example.areaalert.App.CHANNEL_3_ID;
import static com.example.areaalert.App.CHANNEL_4_ID;

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
            Map<String, String> map;
            map = remoteMessage.getData();

            String title = map.get("title");
            String body = map.get("body");
            String priority = map.get("priority");
            String tag = map.get("tag");
            //String url = map.get("url");

            notificationManager = NotificationManagerCompat.from(this);

            if (priority.equals("1")) {
                buildNotification(CHANNEL_1_ID, map);
            } else if (priority.equals("2")) {
                buildNotification(CHANNEL_2_ID, map);
            } else if (priority.equals("3")) {
                buildNotification(CHANNEL_3_ID, map);
            } else {
                buildNotification(CHANNEL_4_ID, map);
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

    public void buildNotification(String CHANNEL_ID, Map map) {

        int id =1;
        //id = Integer.parseInt(map.get("id"));

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID);

        //if(map.get("url").toString().equalsIgnoreCase(""))
            if (map.get("title").toString().equalsIgnoreCase("women")) {
                String message = map.get("body").toString();
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.dsce1);
                Log.d(TAG, "buildNotification: " + message);
                notification
                        .setColor(686559)
                        .setSmallIcon(R.drawable.ic_warning_black_24dp)
                        .setContentTitle((CharSequence) map.get("title"))
                        .setLargeIcon(largeIcon)
                        .setContentText("Reports about women available")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setVibrate(new long[]{500, 1000, 1500});
            } else if (map.get("title").toString().equalsIgnoreCase("congestion")) {
                String message = map.get("body").toString();
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.dsce1);
                notification
                        .setColor(686559)
                        .setSmallIcon(R.drawable.ic_warning_black_24dp)
                        .setContentTitle((CharSequence) map.get("title"))
                        .setLargeIcon(largeIcon)
                        .setContentText("Reports about congestion available")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setVibrate(new long[]{500, 1000, 1500});
            } else if (map.get("title").toString().equalsIgnoreCase("ambulance")) {
                String message = map.get("body").toString();
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
                notification
                        .setColor(686559)
                        .setSmallIcon(R.drawable.ic_warning_black_24dp)
                        .setContentTitle((CharSequence) map.get("title"))
                        .setLargeIcon(largeIcon)
                        .setContentText("Reports about ambulance available")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setVibrate(new long[]{500, 1000, 1500});
            } else if (map.get("title").toString().equalsIgnoreCase("disaster")) {
                String message = map.get("body").toString();
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.dis);
                notification
                        .setColor(686559)
                        .setSmallIcon(R.drawable.ic_warning_black_24dp)
                        .setContentTitle((CharSequence) map.get("title"))
                        .setLargeIcon(largeIcon)
                        .setContentText("Reports about natural disasters available")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setVibrate(new long[]{500, 1000, 1500});
            } else {
                notification
                        .setColor(686559)
                        .setSmallIcon(R.drawable.ic_warning_black_24dp)
                        .setContentTitle("Title")
                        .setVibrate(new long[]{500, 1000, 1500})
                        .setContentText("Message");
            }




        if (CHANNEL_ID.equals(CHANNEL_4_ID)) {
            notification.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        } else if (CHANNEL_ID.equals(CHANNEL_3_ID)) {
            notification.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }

        notificationManager.notify(id, notification.build());
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
//