package com.example.areaalert.Others;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.areaalert.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class FeedClass extends AppCompatActivity {
    ImageView image;
    TextView title;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference colref=db.collection("reports");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedclass_layout);
        image=findViewById(R.id.image);
        title=findViewById(R.id.Title);
        Intent intent=getIntent();
        String id=intent.getStringExtra("id");
        colref.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String url=String.valueOf(documentSnapshot.get("downloadurl"));
                if(url.length()==0)
                    image.setVisibility(View.INVISIBLE);
                else
                Picasso.with(FeedClass.this).load(url).into(image);
            }
        });




    }
}
