package com.example.areaalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import static com.example.areaalert.App.CHANNEL_1_ID;


public class Main2Activity extends AppCompatActivity {

    String token1;
    private NotificationManagerCompat notificationManager;
    private static final String TAG = "Main2Activity";
    Button button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        notificationManager = NotificationManagerCompat.from(this);
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
//        Intent intent = new Intent(this, CongestionMap.class);
//        startActivity(intent);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        token1=token;
                    }
                });

    }


    public void display_notification(String title,String body)
    {

    }
//    private void createNotificationChannels() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel1 = new NotificationChannel(
//                    CHANNEL_1_ID,
//                    "Channel 1",
//                    NotificationManager.IMPORTANCE_HIGH
//            );
//            channel1.setDescription("This is Channel 1");
//
//            NotificationChannel channel2 = new NotificationChannel(
//                    CHANNEL_2_ID,
//                    "Channel 2",
//                    NotificationManager.IMPORTANCE_LOW
//            );
//            channel2.setDescription("This is Channel 2");
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel1);
//            manager.createNotificationChannel(channel2);
//            Intent intent=new Intent(this,Main2Activity.class);
//            startActivity(intent);
//        }
//    }
}
