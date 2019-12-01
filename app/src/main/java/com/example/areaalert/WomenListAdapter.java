package com.example.areaalert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class WomenListAdapter extends BaseAdapter {
    ArrayList<String> urls=new ArrayList<>();
    ArrayList<String> reports =new ArrayList<>();
    ArrayList<String> displays=new ArrayList<>();
    ArrayList<String> addresses=new ArrayList<>();
    Context context;
    public WomenListAdapter(ArrayList<String> urls, ArrayList<String> reports, ArrayList<String> displays, ArrayList<String> addresses,Context context) {
        this.context=context;
        this.urls = urls;
        this.reports = reports;
        this.displays = displays;
        this.addresses = addresses;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int position) {
        return reports.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = LayoutInflater.from(context).inflate(R.layout.women_feed_layout,parent,false);
        ImageView dp=(ImageView) convertView.findViewById(R.id.image);
        TextView tv=(TextView)convertView.findViewById(R.id.Title);
        TextView location=convertView.findViewById(R.id.location);
        if(urls.get(position).length()>0)
        Picasso.with(context).load(urls.get(position)).into(dp);
        tv.setText(reports.get(position));
        location.setText(addresses.get(position));
        TextView likes=convertView.findViewById(R.id.likes);
        Random random=new Random();
        likes.setText("Upvotes "+ random.nextInt(100));


        return convertView;
    }
}
