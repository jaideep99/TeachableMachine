package com.example.teachablemachine;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    List<Bitmap> imageset;
    LayoutInflater inflater;



    public ImageAdapter(Context context,List<Bitmap> imageset)
    {
        super();
        this.inflater = LayoutInflater.from(context);
        this.imageset = imageset;
    }

    @Override
    public int getCount() {
        return imageset.size();
    }

    @Override
    public Object getItem(int i) {
        return imageset.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        if(view == null) {
            view = inflater.inflate(R.layout.imageicon, parent, false);
            ImageView imageicon = (ImageView) view.findViewById(R.id.imageicon);

            imageicon.setImageBitmap(imageset.get(i));

        }
        return view;
    }



}
