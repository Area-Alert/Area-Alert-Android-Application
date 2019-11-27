package com.example.areaalert.Others;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.example.areaalert.R;
import com.example.areaalert.WomenListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class WomenFeeds extends AppCompatActivity {
    ListView listView;
    ArrayList<String> urls=new ArrayList<>();
    ArrayList<String> reports =new ArrayList<>();
    ArrayList<String> displays=new ArrayList<>();
    ArrayList<String> addresses=new ArrayList<>();
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference colref=db.collection("reports");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_women_feeds);
        listView=findViewById(R.id.women_feed_list);
        colref.whereEqualTo("report_type","women")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                {
                    String url=String.valueOf(queryDocumentSnapshot.get("downloadurl"));
                    String name=String.valueOf(queryDocumentSnapshot.get("display"));
                    String report=String.valueOf(queryDocumentSnapshot.get("report"));
                    String address=String.valueOf(queryDocumentSnapshot.get("address"));
                    String add[]=address.split(" ");


                            addresses.add("Bengaluru");

                    urls.add(url);
                    reports.add(report);
                    displays.add(name);
                }
                WomenListAdapter adapter=new WomenListAdapter(urls,reports,displays,addresses,WomenFeeds.this);
                listView.setAdapter(adapter);


            }
        });

    }
}
