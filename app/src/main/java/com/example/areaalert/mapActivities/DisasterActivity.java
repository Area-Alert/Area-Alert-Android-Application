package com.example.areaalert.mapActivities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.areaalert.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisasterActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    Double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster);
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
        LatLng sydney = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        db.collection("reports")
                .whereEqualTo("report_type","women")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                HashMap<String,Integer> map=new HashMap<>();
                ArrayList<String> set=new ArrayList<>();
                for(QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                {
                    if(map.containsKey(queryDocumentSnapshot.get("postalCode")))
                    {
                        map.put(String.valueOf(queryDocumentSnapshot.get("postalCode")),map.get(queryDocumentSnapshot.get("postalCode"))+1);

                    }
                    else
                    {
                        map.put(String.valueOf(queryDocumentSnapshot.get("postalCode")),1);
                        set.add(String.valueOf(queryDocumentSnapshot.get("postalCode")));
                    }
                }
                addHeatMap(map,set);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    private void addHeatMap(HashMap<String,Integer> map, final ArrayList<String> postals) {
        final List<WeightedLatLng> list=new ArrayList<>() ;

        for(int i=0;i<postals.size();i++)
        {
            final int num=map.get(postals.get(i));
            final int j=i;
            db.collection("reports").whereEqualTo("report_type","women")
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots) {
                        Log.d("Query", queryDocumentSnapshot.toString());
                        try {
                            if (queryDocumentSnapshot.get("postalCode").toString()
                                    .equalsIgnoreCase(postals.get(j))) {
                                lat = Double.parseDouble(String.valueOf(queryDocumentSnapshot.get("lat")));
                                lng = Double.parseDouble(String.valueOf(queryDocumentSnapshot.get("lon")));

                                if (num > 0 && num <= 3) {
                                    WeightedLatLng latLng = new WeightedLatLng(new LatLng(lat, lng), 0.2);

                                    list.add(latLng);
                                } else if (num > 3 && num <= 10) {

                                    WeightedLatLng latLng = new WeightedLatLng(new LatLng(lat, lng), 0.7);

                                    list.add(latLng);
                                } else if (num > 10) {
                                    WeightedLatLng latLng = new WeightedLatLng(new LatLng(lat, lng), 1.0);

                                    list.add(latLng);
                                }
                                Log.d("Size", String.valueOf(list.size()));
                            }
                        }catch (Exception e){
                            Log.d("Disaster", "onSuccess: OMG this is a disaster");
                        }
                    }
                    HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                            .weightedData(list)
                            .radius(50)
                            .build();
                    TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                    mMap.setMinZoomPreference(5.0f);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(14.9,77.6)));





                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DisasterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    Log.d("Show",e.toString());

                }
            });
        }



    }
}
