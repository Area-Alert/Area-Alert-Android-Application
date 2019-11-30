package com.example.areaalert.mapActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.areaalert.Others.WomenFeeds;
import com.example.areaalert.R;
import com.example.areaalert.ShakeListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WomenActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<WeightedLatLng> list2 = new ArrayList<>();
    Double lat, lng;
    private ShakeListener mShaker;
    Location location;
    String address = "";
    LocationManager locationManager;
    String provider;
    FloatingActionButton w1,w2,w3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_women);
        w1=findViewById(R.id.WomenFeeds);
        w2=findViewById(R.id.WomenForum);
        w3=findViewById(R.id.WomenNumber);

        w1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WomenActivity.this,WomenFeeds.class);
                startActivity(intent);
            }
        });
        //-------------------------Women Forum-------------------------
        w2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        //--------------------Women Number----------------------------
        w3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent=new Intent
                //startActivity(intent);

            }
        });
        final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Log.d("WomenActivity", "onCreate: This is WomenActivity");
        setLocationConfig();

        mShaker = new ShakeListener(this);
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                vibe.vibrate(100);
                //Making a report

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                String listadd[] = address.split(",");
                int i = listadd.length;

                Log.d("TAG", "onShake: " + location);

                Map<String, String> name1 = new HashMap<>();
                name1.put("display", "Damsel in Distress");
                name1.put("given", "Damsel");
                name1.put("family", "in Distress");
                Map<String, Object> report = new HashMap<>();
                report.put("report_number", mAuth.getCurrentUser().getPhoneNumber());
                report.put("address", address);
                report.put("city", listadd[0]);
                report.put("lat", location.getLatitude());
                report.put("loc", new GeoPoint(location.getLatitude(),location.getLongitude()));
                report.put("lon", location.getLongitude());
//                report.put("postalCode", listadd[i-2].split(" ")[2]);
                report.put("report", "Emergency regarding women");
                report.put("report_type", "emergency");
                report.put("name", name1);
                db.collection("women_emergency").document(mAuth.getCurrentUser().getPhoneNumber()).set(report);

               //Alert Dialog
                new AlertDialog.Builder(WomenActivity.this)
                        .setPositiveButton(android.R.string.ok, null)
                        .setMessage("Alert Sent!")
                        .show();
            }
        });

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
}

    private void setLocationConfig() {
        Log.d("TAG", "setLocationConfig: holo");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("WomenActivity", "setLocationConfig: Permission seems to be not granted");
            return;
        }
        Log.d("WomenActivity", "setLocationConfig: Permission is granted");
        locationManager.requestLocationUpdates(provider, 100, 0, this);
        location = locationManager.getLastKnownLocation(provider);

        Log.d("TAG", "setLocationConfig: " + location);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(23, 77);
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
                                Log.d("women safety", "onSuccess: Well we messed up");
                            }
                        }
                                HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                                        .weightedData(list)
                                        .radius(50)
                                        .build();
                                TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                      mMap.setMinZoomPreference(8.0f);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(23,77)));





                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WomenActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        Log.d("Show",e.toString());

                    }
                });
            }



    }
    public void addValues(WeightedLatLng latLng)
    {
        list2.add(latLng);


    }
    @Override
    public void onResume() {
        mShaker.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mShaker.pause();
        super.onPause();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
