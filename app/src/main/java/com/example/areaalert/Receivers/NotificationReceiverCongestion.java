package com.example.areaalert.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NotificationReceiverCongestion extends BroadcastReceiver {

    FirebaseFirestore db;
    String TAG = "NotificationReceiverCongestion";

    @Override
    public void onReceive(final Context context, Intent intent) {
        db = FirebaseFirestore.getInstance();
        final String message = intent.getStringExtra("text");
        Toast.makeText(context, "Thank you for your feedback", Toast.LENGTH_SHORT).show();
        try{
            db.collection("reports")
                    .document(message)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            try {
                                int i = Integer.parseInt(documentSnapshot.get("upvotes").toString());
                                Map<String, Object> data = new HashMap<>();
                                data.put("upvotes", i + 1);
                                db.collection("reports")
                                        .document(message)
                                        .update(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Upvoted Successfully",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }catch(Exception e){
                                Map<String, Object> data = new HashMap<>();
                                data.put("upvotes", 1);
                                db.collection("reports")
                                        .document(message)
                                        .update(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Upvoted Successfully",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Well this sucks");
                        }
                    });
        }catch (Exception e){
            Map<String, Object> data = new HashMap<>();
            data.put("upvotes", 1);
            db.collection("reports")
                    .document(message)
                    .update(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Upvoted Successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
