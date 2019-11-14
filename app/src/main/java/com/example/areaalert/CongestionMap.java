package com.example.areaalert;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CongestionMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    ArrayList<String> lats=new ArrayList<>();
    ArrayList<String> longs=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congestion_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(CongestionMap.this);
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
        addHeatMap();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(12.986694,77.575882 );
        CollectionReference colref=db.collection("verified_reports")
                .document("cLoHtsP4xy3UcAnCYUXE")
                .collection("locations");
        colref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots)
                {
                    String lat=queryDocumentSnapshot.getString("lat");
                    String lng=queryDocumentSnapshot.getString("lng");
                    lats.add(lat);
                    longs.add(lng);
                    Log.d("This","success");

                }
                addMarkers(lats,longs);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("This","failure");

            }
        });

        Polyline polyline=googleMap.addPolyline(new PolylineOptions().add(new LatLng(13.0305, 77.5649)).add(new LatLng(10.0305, 90.5649)).add(new LatLng(7.0305, 70.5649)) );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }
    public void addMarkers(ArrayList<String> lats,ArrayList<String> longs) {
        Log.d("Lat Size",String.valueOf(lats.size()));
        for (int i = 0; i < lats.size(); i++) {
            Log.d("TAG", "addMarkers: " + lats.get(i));
            ArrayList<Double> coords = new ArrayList<>();
            coords.add(Double.parseDouble(lats.get(i)));
            coords.add(Double.parseDouble(longs.get(i)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(coords.get(0), coords.get(1))).title("Marker in Sydney"));
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
