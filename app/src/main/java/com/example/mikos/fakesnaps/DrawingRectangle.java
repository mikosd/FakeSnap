package com.example.mikos.fakesnaps;

/**
 * Created by JohnL on 4/16/2018.
 */

import android.graphics.Rect;

public class DrawingRectangle {


    public int color;
    public int strokeWidth;
    public Rect rectangle;

    public DrawingRectangle(int color, int strokeWidth, Rect r) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.rectangle = r;
    }
}