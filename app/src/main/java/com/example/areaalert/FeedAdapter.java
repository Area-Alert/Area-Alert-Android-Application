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

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedAdapter extends BaseAdapter {
        ArrayList<String> name=new ArrayList<>();
        ArrayList<Integer> images=new ArrayList<>();
        Context context;
        public FeedAdapter(Context context,ArrayList<String> name,ArrayList<Integer> images) {
            this.name = name;
            this.context=context;
            this.images=images;
        }

        @Override
        public int getCount() {
            return name.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = LayoutInflater.from(context).inflate(R.layout.category_layout,parent,false);
            CircleImageView dp=(CircleImageView)convertView.findViewById(R.id.dp);
            TextView tv=(TextView)convertView.findViewById(R.id.textView10);
            dp.setImageResource(images.get(position));
            tv.setText(name.get(position));
            return convertView;
        }
    }