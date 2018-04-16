package com.example.mikos.fakesnaps;

/**
 * Created by John on 4/14/2018.
 * Drawing Path is used to track each stroke by tracking its Path, its color and width, so that
 * lines of different colors and sizes can exist in a single image. DrawingPath is simple used to
 * encapsulate the fields.
 *
 * int color                -an integer representing the color of the stroke
 * int strokeWidth          -an integer in pixels representing the width of the stroke
 * Path path                -A Path object that shows the path the stroke takes
 */

import android.graphics.Path;

    public class DrawingPath {

        public int color;
        public int strokeWidth;
        public Path path;

        public DrawingPath(int color, int strokeWidth, Path path) {
            this.color = color;
            this.strokeWidth = strokeWidth;
            this.path = path;
        }
    }