package com.example.areaalert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class LocationService extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATE = "com.example.areaalert.UPDATE_LOCATION";
    private static final String TAG = "TAG";

    public FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (ACTION_PROCESS_UPDATE.equals(intent.getAction())) {
                LocationResult locationResult = LocationResult.extractResult(intent);
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();

                    Log.d(TAG, "onReceive: " + location.getLatitude() + " " + location.getLongitude());

                    if (mAuth.getCurrentUser() != null) {
                        Log.d(TAG, "onReceive: oh wow null and empty apparently :" + mAuth.getCurrentUser().getEmail() + ":");
                        updateDBWithLocation(location);
                    }
                }
            }
        }
    }

    private void updateDBWithLocation(final Location location) {
        Log.d(TAG, "updateDBWithLocation: " + mAuth.getCurrentUser().getEmail());

        db.collection("users")
                .document(mAuth.getCurrentUser().getEmail())
                .update("currentLocation", new GeoPoint(location.getLatitude(), location.getLongitude()))
                
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MainActivity.getInstance().makeToast(location.getLatitude() + " " + location.getLongitude());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MainActivity.getInstance().makeToast(e.toString());
                    }
                });
    }
}
