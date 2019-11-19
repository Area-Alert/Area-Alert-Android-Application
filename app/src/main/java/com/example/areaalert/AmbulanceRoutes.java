package com.example.areaalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AmbulanceRoutes extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private final String TAG = "AmbulanceRoutes";
    private FirebaseFirestore db;
    private GoogleMap mMap;
    private List<MarkerOptions> places = new ArrayList<>(), place = new ArrayList<>();
    Button getDirection;
    private Polyline currentPolyline;
    MapFragment mapFragment;
    int i=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance_routes);

        db = FirebaseFirestore.getInstance();

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.MapFrag);

        db.collection("reports")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                            try {
                                if (document.getData().get("report_type").toString().equalsIgnoreCase("ambulance")) {
                                    places.add(new MarkerOptions().position(new LatLng(Double.parseDouble(document.getData().get("lat").toString()),
                                            Double.parseDouble(document.getData().get("lon").toString()))));
                                    mapFragment.getMapAsync(AmbulanceRoutes.this);
                                }
                            }catch (Exception e)
                            {
                                Log.d(TAG, "onSuccess: here the report_type is null");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: This Failed");
                    }
                });

        place.add(new MarkerOptions().position(new LatLng(13.018468, 77.558200)).title("Location 1"));
        place.add(new MarkerOptions().position(new LatLng(13.006405, 77.578746)).title("Location 2"));

        getDirection = findViewById(R.id.btnGetDirection);
        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getMapAsync(AmbulanceRoutes.this);
                new FetchURL(AmbulanceRoutes.this)
                        .execute(getUrl(places.get(0).getPosition(), place.get(i).getPosition(), "driving"), "driving");
            }
        });

    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        i = (i+1)%2;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng myCoordinates = places.get(0).getPosition();

        googleMap.clear();

        mMap = googleMap;
        Log.d(TAG, "Added Markers");
        mMap.addMarker(places.get(0));
        places.get(0).title("Ambulance");
        Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_add_black_24dp);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
        places.get(0).icon(markerIcon);
        mMap.addMarker(place.get(i));
        place.get(i).title("Traffic signal " + (i+1));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myCoordinates)      // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" +
                parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }
}
