package com.example.areaalert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.areaalert.mapActivities.AmbulanceRoutes;
import com.example.areaalert.mapActivities.CongestionMap;
import com.example.areaalert.mapActivities.DisasterActivity;
import com.example.areaalert.mapActivities.PedestrianActivity;
import com.example.areaalert.mapActivities.WomenActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Categories extends AppCompatActivity {
    ArrayList<Integer> images=new ArrayList<>();
    ArrayList<String> names=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        hideNavigationbar();
        ListView listView=findViewById(R.id.listView);
        FloatingActionButton fab=findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Categories.this, "Generate", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Categories.this, MainActivity.class));
            }
        });
        images.add(R.drawable.dsce1);
        images.add(R.drawable.dsce2);
        images.add(R.drawable.plus);
        images.add(R.drawable.dis);
        images.add(R.drawable.food);
        names.add("View Road Blockages");
        names.add("Women Suraksha");
        names.add("Ambulance Routing");
        names.add("Natural Disaster Alert!");
        names.add("Pedestrian Convenience");
        FeedAdapter adapter=new FeedAdapter(Categories.this,names,images);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    Intent intent=new Intent(Categories.this, CongestionMap.class);
                    startActivity(intent);
                }
               else if(position==1)
                {
                    Intent intent=new Intent(Categories.this, WomenActivity.class);
                    startActivity(intent);
                }
               else if(position==2)
                {
                    Intent intent=new Intent(Categories.this, AmbulanceRoutes.class);
                    startActivity(intent);
                }
               else if(position==3)
                {
                    Intent intent=new Intent(Categories.this, DisasterActivity.class);
                    startActivity(intent);
                }
               else if(position==4)
                {
                    Intent intent=new Intent(Categories.this, PedestrianActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void hideNavigationbar() {

        this.getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                );


    }

    @Override
    protected void onStart() {
        super.onStart();
        hideNavigationbar();
    }
}
