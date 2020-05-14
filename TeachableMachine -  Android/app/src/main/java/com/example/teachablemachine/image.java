package com.example.teachablemachine;

import android.graphics.Bitmap;

public class image {

    private String label;
    private Bitmap image;

    public image(String label,Bitmap image){
        this.label = label;
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getLabel() {
        return label;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
