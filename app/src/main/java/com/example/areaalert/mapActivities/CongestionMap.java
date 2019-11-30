package com.example.areaalert.mapActivities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.areaalert.MainActivity;
import com.example.areaalert.Others.FeedClass;
import com.example.areaalert.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

public class CongestionMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    ArrayList<String> lats=new ArrayList<>();
    ArrayList<String> longs=new ArrayList<>();
    ArrayList<String> reports=new ArrayList<>();
    ArrayList<String> id=new ArrayList<>();
    FloatingActionButton CongestionFeeds,CongestionReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congestion_map);
        CongestionFeeds=findViewById(R.id.CongestionFeeds);
        CongestionReport=findViewById(R.id.CongestrionReport);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(CongestionMap.this);
        CongestionFeeds.setOnClickListener(new View.OnClickListener() {

    //  -------------------------------------Congestion Report-----------------------------
            @Override
            public void onClick(View v) {
               // Intent intent=new Intent(CongestionMap.this,);
                //startActivity(intent);

            }
        });
        CongestionReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CongestionMap.this, MainActivity.class);
                startActivity(intent);
            }
        });
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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng default_loc = new LatLng(22, 77);
        CollectionReference colref=db.collection("reports");
        colref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("This","failure");

            }
        });

        mMap.setMinZoomPreference(4.0f);
        //Marker click
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String id=String.valueOf(marker.getTag());
                Intent intent=new Intent(CongestionMap.this, FeedClass.class);
                intent.putExtra("id",id);
                startActivity(intent);
                return false;
            }
        });

        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lats.get(
                    lats.size() - 1)), Double.parseDouble(longs.get(longs.size() - 1)))));
        }
        catch (Exception e){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(default_loc));
        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney[0]));

    }
    public void addMarkers(ArrayList<String> lats,ArrayList<String> longs,ArrayList<String>report,ArrayList<String> id) {
        Log.d("Lat Size",String.valueOf(lats.size()));
        for (int i = 0; i < lats.size(); i++) {
            Log.d("TAG", "addMarkers: " + lats.get(i));
            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lats.get(i)),Double.parseDouble(longs.get(i))))
                    .title(report.get(i)))
                    .setTag(id.get(i));
        }
    }
    private void addHeatMap() {
        List<LatLng> list=new ArrayList<>() ;

        // Get the data: latitude/longitude positions of police stations.
//        try {
//            list = readItems(R.raw.police_stations);
//        } catch (JSONException e) {
//            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
//        }

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        list.add(new LatLng(-37.1886,145.708));
        list.add(new LatLng(-37.8361,144.845));
        list.add(new LatLng(-37.300,146.0));


        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .radius(40)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

//    private ArrayList<LatLng> readItems(int resource) throws JSONException {
//        ArrayList<LatLng> list = new ArrayList<LatLng>();
//        InputStream inputStream = getResources().openRawResource(resource);
//        String json = new Scanner(inputStream).useDelimiter("\\A").next();
//        JSONArray array = new JSONArray(json);
//        for (int i = 0; i < array.length(); i++) {
//            JSONObject object = array.getJSONObject(i);
//            double lat = object.getDouble("lat");
//            double lng = object.getDouble("lng");
//            list.add(new LatLng(lat, lng));
//        }
//        return list;
   // }

}
