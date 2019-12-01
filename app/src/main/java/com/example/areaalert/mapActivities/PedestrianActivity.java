package com.example.areaalert.mapActivities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.areaalert.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PedestrianActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    ArrayList<String> lats=new ArrayList<>();
    ArrayList<String> longs=new ArrayList<>();
    ArrayList<String> reports=new ArrayList<>();
    ArrayList<String> id=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedestrian);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        final LatLng sydney = new LatLng(23.2779,77.373 );
        CollectionReference colref=db.collection("reports");
        colref.whereEqualTo("report_type","pedestrian").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots)
                {
                    String lat=String.valueOf(queryDocumentSnapshot.get("lat"));
                    String lng=String.valueOf(queryDocumentSnapshot.get("lon"));
                    lats.add(lat);
                    String ids=queryDocumentSnapshot.getId();
                    id.add(ids);
                    String report=String.valueOf(queryDocumentSnapshot.get("report"));
                    reports.add(report);
                    longs.add(lng);
                    Log.d("This","success");

                }
                addMarkers(lats,longs,reports,id);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                mMap.setMinZoomPreference(10);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("This","failure");

            }
        });

        mMap.setMinZoomPreference(13.0f);
    }
    public void addMarkers(ArrayList<String> lats,ArrayList<String> longs,ArrayList<String>report,ArrayList<String> id) {
        Log.d("Lat Size",String.valueOf(lats.size()));
        for (int i = 0; i < lats.size(); i++) {
            Log.d("TAG", "addMarkers: " + lats.get(i));
            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lats.get(i)),Double.parseDouble(longs.get(i))))
                    .title(report.get(i)))
                    .setTag(id.get(i));        }
    }
}
