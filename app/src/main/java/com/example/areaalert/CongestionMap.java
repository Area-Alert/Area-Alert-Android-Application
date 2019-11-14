package com.example.areaalert;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congestion_map);
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
        addHeatMap();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(12.986694,77.575882 );
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        Polyline polyline=googleMap.addPolyline(new PolylineOptions().add(new LatLng(13.0305, 77.5649)).add(new LatLng(10.0305, 90.5649)).add(new LatLng(7.0305, 70.5649)) );

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
