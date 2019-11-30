package com.example.areaalert.Others;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.areaalert.R;
import com.example.areaalert.mapActivities.WomenActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class WomenNumber extends AppCompatActivity {
    EditText editText,editText2;
    TextView textView;
    ProgressBar progressBar;
    FirebaseFirestore db;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_women_number);
        editText=findViewById(R.id.editText);
        editText2=findViewById(R.id.editText2);
        textView=findViewById(R.id.textView9);
        progressBar=findViewById(R.id.progressBar3);
        final String number=mAuth.getCurrentUser().getPhoneNumber();
        progressBar.setVisibility(View.INVISIBLE);
        db=FirebaseFirestore.getInstance();
        final CollectionReference colref=db.collection("women_emergency");

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Intent intent=getIntent();
               String lat= intent.getStringExtra("latitude");
               String lon=intent.getStringExtra("longitude");
                String number1=editText.getText().toString();
                String number2=editText2.getText().toString();
                HashMap<String,String> map=new HashMap<>();
                map.put("number1",number1);
                map.put("number2",number2);
                map.put("lat",lat);
                map.put("lon",lon);
                colref.document(number).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Intent intent=new Intent(WomenNumber.this, WomenActivity.class);
                        startActivity(intent);
                    }
                });

            }
        });


    }
}
