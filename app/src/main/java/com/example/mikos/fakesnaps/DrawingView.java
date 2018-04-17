package com.example.mikos.fakesnaps;

import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;


/*
* John Litman
*
* Below is the class for the custom widget 'DrawingView' that inherits from an image view. Drawing
* view is used to draw lines on the image after a bitmap has been loaded, resized and applied to the
* canvas. It's members and functions are as follows:
*
*   int BRUSH_SIZE                      -Brush size is an int that represents the size of the brush
    int START_COLOR                     -START_COLOR is an int that represents the starting color of
                                        the paint
    int START_BG_COLOR                  -START_BG_COLOR is the starting background color of the image
    float TOUCH_TOLERANCE               -TOUCH_TOLERANCE is the tolerane of touch movements and when
                                        they will be recorded
    float mX, mY                        -mx and my are the current positions of touch event or stroke
    Path mPath                          -mPath is the currently used path the user is drawing
    Paint mPaint                        -mPaint is the current paint the user is using
    ArrayList<DrawingPath> paths        -paths is an arrayList that stores previous paths to be drawn
    int currentColor;                   -current color of the paint
    int backgroundColor                 -current background color
    int strokeWidth                     -current width of a stroke
    Bitmap mBitmap;                     -a bitmap created to draw on the canvas
    Bitmap mLoadBit;                    -a loaded bitmap from the gallery
    Canvas mCanvas;                     -canvas to draw on
    Paint mBitmapPaint                  -paint to draw the bitmap
* */



public class DrawingView extends ImageView {

    public static int BRUSH_SIZE = 20;
    public static final int START_COLOR = Color.RED;
    public static final int START_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;
    private DrawingRectangle mRectangle;
    private Paint mPaint;

    private ArrayList<DrawingPath> paths = new ArrayList<>();
    private ArrayList<DrawingRectangle> rectangles = new ArrayList<>();

    private int currentColor;
    private int backgroundColor = START_BG_COLOR;
    private int strokeWidth;
    private Bitmap mBitmap;
    private Bitmap mLoadBit;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private boolean isRectangle;
    private boolean isStroke;

    public DrawingView(Context context) {
        this(context, null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(START_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);
        isRectangle = true;
        isStroke=false;
    }


    /*
    * this function is used to initialize the DrawingView after it has been created. it creates a
    * blank bitmap to draw from the background color and sets the current color to the Start color
    * for the  lines to be drawn
    * */
    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mLoadBit = mBitmap;
        setmCanvas(new Canvas(mBitmap));

        setCurrentColor(START_COLOR);
        strokeWidth = BRUSH_SIZE;
    }





//clear simply clears the strokes on the picture
    public void clear() {
        backgroundColor = START_BG_COLOR;
        paths.clear();
        invalidate();
    }

    /*
    * On takes all the paths stored in the paths arraylist and draws them back to the canvas every
    * time it needs to be invalidated,as well as the imaged loaded from the gallery.
    * */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        getmCanvas().drawColor(backgroundColor);
        getmCanvas().drawBitmap(mLoadBit,0.0f,0.0f,null);

        for (DrawingPath drawingPath : paths) {
            mPaint.setColor(drawingPath.color);
            mPaint.setStrokeWidth(drawingPath.strokeWidth);
            mPaint.setMaskFilter(null);

            getmCanvas().drawPath(drawingPath.path, mPaint);

        }
        for (DrawingRectangle r : rectangles){
            mPaint.setColor(r.color);
            mPaint.setStrokeWidth(r.strokeWidth);

            getmCanvas().drawRect(r.rectangle,mPaint);

        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    /*
    * Touch start, move and up are all used as elper functions for drawing and the onTouch evens,
    * they are split up this way for readability, and ease of use. Touch start starts a new path, and
    * sets up the beggening loacation, touch move tracks the finger position to draw a path
    *, and touch up finishes the touch event
    * */
    private void touchStart(float x, float y) {
        if(isStroke) {
            mPath = new Path();
            DrawingPath dp = new DrawingPath(getCurrentColor(), strokeWidth, mPath);
            paths.add(dp);
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }else if(isRectangle){
            Rect r = new Rect();
            mRectangle = new DrawingRectangle(getCurrentColor(),strokeWidth, r );
            mRectangle.rectangle.left = (int)x;
            mRectangle.rectangle.top = (int)y;
            mX = x;
            mY = y;

        }
    }

    private void touchMove(float x, float y) {
        if(isStroke){
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);

             if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                 mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                   mX = x;
                 mY = y;
              }
         }if(isRectangle){
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            mX = x;
            mY = y;
        }

    }

    private void touchUp() {
        if(isStroke) {
            mPath.lineTo(mX, mY);
        }
        if(isRectangle){
            mRectangle.rectangle.right= (int)mX;
            mRectangle.rectangle.bottom= (int)mY;
            rectangles.add(mRectangle);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                touchUp();
                invalidate();
                break;
        }

        return true;
    }
    //the following are simple getters and setters.
    public Canvas getmCanvas() {
        return mCanvas;
    }

    public void setmCanvas(Canvas mCanvas) {
        this.mCanvas = mCanvas;
    }

    public Bitmap getmLoadBit() {
        return mLoadBit;
    }

    public void setmLoadBit(Bitmap mLoadBit) {
        this.mLoadBit = mLoadBit;
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }
}