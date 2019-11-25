package com.example.areaalert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private final String TAG = "MainActivity";

    final int camera_request = 123;
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private StorageReference imagesStorageRef;
    private FirebaseFirestore db;
    private String authorities;
    FirebaseUser currentUser;
    TextView UserText;
    ImageView Capture;
    Spinner spinner;
    //ProgressBar pb;

    LocationManager locationManager;
    Location location;
    String address = "",imageFilename;
    Uri imageFileUri;
    String filePath,name;

    Button Report,Camera,Feeds;
    EditText ReportText;

    Uri downloadUrl,dataUri;
    Bitmap bitmap;
    Date date;
    File mImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideNavigationBar();

        authorities = getApplicationContext().getPackageName() + ".fileprovider";
        date = new Date();

        UserText = findViewById(R.id.Hello);
        Report = findViewById(R.id.ReportBtn);
        Camera = findViewById(R.id.CameraButton);
        Feeds = findViewById(R.id.Feeds);
        Capture = findViewById(R.id.ImageViewMain);
        spinner = findViewById(R.id.Spinner);
//        pb = findViewById(R.id.progressBar2);
//        pb.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        imagesStorageRef = mStorageRef.child("images");
        db = FirebaseFirestore.getInstance();

        currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(MainActivity.this,SignInActivity.class));
        }
        else {
            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, "Document Received" + document.getId());
                                    if (document.getData().containsValue(currentUser.getUid())) {
                                        SetValues(document);
                                    }
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        } else requestLocation();
        if (!isLocationEnabled())
            showAlert(1);


        Report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SendReport();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickMe();
            }
        });

        Feeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Categories.class));
            }
        });

    }


    public void ClickMe(){

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        dispatchTakePictureIntent();

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            mImageFile = new File(MainActivity.this.getFilesDir(), new Date().getTime() + ".jpg");
//            mImageFilePath = mImageFile.getAbsolutePath();

            imageFileUri = FileProvider.getUriForFile(this, authorities, mImageFile);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);

            startActivityForResult(takePictureIntent, camera_request);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == camera_request && resultCode == RESULT_OK) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(mImageFile));
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (!mImageFile.delete()) {
                Log.d(TAG, "onActivityResult: DELETE FAILED");
            }

            Capture.setImageBitmap(bitmap);
            FirebaseUser user = mAuth.getCurrentUser();
            Date date = new Date();
            imageFilename = user.getPhoneNumber() + "/" + date.getTime() + ".jpg";

        }
    }


    public void uploadFile() {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        //Toast.makeText(MainActivity.this, String.valueOf(data.length), Toast.LENGTH_LONG).show();

        final StorageReference singleStorageRef = imagesStorageRef.child(imageFilename);

        UploadTask imageUploadTask = singleStorageRef.putBytes(data);

        imageUploadTask
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        singleStorageRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        downloadUrl = uri;
                                        Log.d(TAG,uri.toString());
                                        SendAfterUpload();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
                                });

                        Log.d(TAG, "onSuccess: IMAGES DONE");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: UPLOAD IMAGE " + e.toString());
                    }
                });
    }


    public void SendReport() throws IOException {

        ReportText = findViewById(R.id.Report);

        if(ReportText.getText().toString().isEmpty()){
            ReportText.setHint("Enter the Report Here");
            ReportText.requestFocus();
            return;
        }

        uploadFile();

//        try {
//            Toast.makeText(this, location.getLatitude() + " " + location.getLongitude() +
//                    " " + address, Toast.LENGTH_LONG
//            ).show();
//        }
//        catch(Exception exception){
//            Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show();
//        }
    }

    public void SendAfterUpload(){
        String listadd[] = address.split(",");
        int i = listadd.length;

        Map<String, String> name1 = new HashMap<>();
        name1.put("display", name);
        name1.put("given", name);
        name1.put("family", "");

        Map<String, Object> report = new HashMap<>();
        report.put("address", address);
        report.put("city", listadd[i-3]);
        report.put("lat", location.getLatitude());
        report.put("lon", location.getLongitude());
        report.put("postalCode", listadd[i-2].split(" ")[2]);
        report.put("report", ReportText.getText().toString());
        report.put("report_type", spinner.getSelectedItem());
        report.put("name", name1);
        report.put("downloadurl", downloadUrl.toString());

        //Toast.makeText(this, downloadUrl.toString(), Toast.LENGTH_SHORT).show();

        db.collection("reports")
                .add(report)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this, "Report sent successfully", Toast.LENGTH_SHORT);
                        ReportText.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: This FAILED");
                    }
                });
    }

    private void showAlert(final int status) {
        String message, title, btnText;
        if(status == 1) {
            message = "Your Location Settings is set to 'OFF'.\nPlease Enable Location to " +
                    "use this app";
            title = "Enable Location";
            btnText = "Location Settings";
        } else {
            message = "Please allow this this app to access location!";
            title = "permission access";
            btnText = "Grant";
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (status == 1) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        } else
                            requestPermissions(PERMISSIONS, PERMISSION_ALL);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                });
        dialog.show();
    }

    private boolean isPermissionGranted() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission is Granted");
            return true;
        } else {
            Log.v(TAG, "Permission not granted");
            return false;
        }
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 100, 0, this);
        location = locationManager.getLastKnownLocation(provider);
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            address = addresses.get(0).getAddressLine(0);
            if(addresses.get(0).getAddressLine(1) != null)
                address = "," + addresses.get(0).getAddressLine(1);
            if(addresses.get(0).getAddressLine(1) != null)
                address = "," + addresses.get(0).getAddressLine(2);
        }catch(Exception e)
        {
//            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    void SetValues(QueryDocumentSnapshot document) {
        Map<String, Object> user = document.getData();
        name = user.get("name").toString();
        UserText.setText("Hello " + name);

    }

    @Override
    public void onLocationChanged(Location location) {

        this.location = location;

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            address = addresses.get(0).getAddressLine(0);
            if(addresses.get(0).getAddressLine(1) != null)
                address = "," + addresses.get(0).getAddressLine(1);
            if(addresses.get(0).getAddressLine(1) != null)
                address = "," + addresses.get(0).getAddressLine(2);
        }catch(Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    public void hideNavigationBar(){
        this.getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                );
    }

}
