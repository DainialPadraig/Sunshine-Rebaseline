package com.example.android.sunshine.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by danstormont on 1/18/15.
 */
public class CompassView extends View {

    private static final String LOG_TAG = CompassView.class.getSimpleName();

    private static final float COMPASS_BEZEL_THICKNESS = 15;
    private static final float POINTER_BASE_SIZE = 10;

    private double mWindDirection;
    private double mWindSpeed;
    private Paint mCompassCirclePaint;
    private int mCompassCircleX;
    private int mCompassCircleY;
    private int mCompassCircleSize;
    private Paint mCompassPointerPaint;
    private Point mCompassCenter;
    private Point mCompassPointerBaseLeft;
    private Point mCompassPointerBaseRight;
    private Point mCompassPointerTip;
    private Path mCompassPointerPath;

    public double getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(double speed) {
        mWindSpeed = speed;
    }

    public double getWindDirection() {
        return mWindDirection;
    }

    public void setWindDirection(double dir) {
        mWindDirection = dir;
    }

    private void init() {

        Log.v(LOG_TAG, "init()");

        // Set paint styles for compass bezel.
        mCompassCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCompassCirclePaint.setColor(getResources().getColor(R.color.sunshine_blue));
        mCompassCirclePaint.setStyle(Paint.Style.STROKE);
        mCompassCirclePaint.setStrokeWidth(COMPASS_BEZEL_THICKNESS);

        // Set paint styles for compass pointer.
        mCompassPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCompassPointerPaint.setColor(getResources().getColor(R.color.sunshine_red));

        // Create the pointer.
        mCompassCenter = new Point();
        mCompassPointerBaseLeft = new Point();
        mCompassPointerBaseRight = new Point();
        mCompassPointerTip = new Point();
        mCompassPointerPath = new Path();
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CompassView,
                0, 0);

        try {
            mWindDirection = array.getFloat(R.styleable.CompassView_windDirection, 0);
            mWindSpeed = array.getFloat(R.styleable.CompassView_windSpeed, 0);
        } finally {
            array.recycle();
        }

        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Log.v(LOG_TAG, "onMeasure()");

        // Try for a width based on the minimum.
        int minWidth = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minWidth, widthMeasureSpec, 1);

        // Whatever the width ends up being, ask for a height that would let the compass get as
        // big as it can.
        int minHeight = getPaddingBottom() + getPaddingTop() + getSuggestedMinimumHeight();
        int h = resolveSizeAndState(minHeight, heightMeasureSpec, 1);

        setMeasuredDimension(w, h);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.v(LOG_TAG, "onDraw()");

        mCompassCircleX = getMeasuredWidth() / 2;
        mCompassCircleY = getMeasuredHeight() / 2;

        // Size to the smallest dimension and don't forget to take the bezel thickness
        // into account.
        mCompassCircleSize = Math.min(mCompassCircleX, mCompassCircleY)
                - (int) COMPASS_BEZEL_THICKNESS;

        // Draw the compass circle.
        canvas.drawCircle(mCompassCircleX, mCompassCircleY,
                mCompassCircleSize, mCompassCirclePaint);

        // Calculate the vertices of the pointer.
        mCompassCenter.x = mCompassCircleX;
        mCompassCenter.y = mCompassCircleY;
        mCompassPointerBaseLeft.x = mCompassCenter.x
                - (int) (Math.cos(mWindDirection) * POINTER_BASE_SIZE);
        mCompassPointerBaseLeft.y = mCompassCenter.y
                - (int) (Math.sin(mWindDirection) * POINTER_BASE_SIZE);
        mCompassPointerBaseRight.x = mCompassCenter.x
                + (int) (Math.cos(mWindDirection) * POINTER_BASE_SIZE);
        mCompassPointerBaseRight.y = mCompassCenter.y
                + (int) (Math.sin(mWindDirection) * POINTER_BASE_SIZE);
        mCompassPointerTip.x = mCompassCenter.x
                + (int) (Math.sin(mWindDirection) * (mCompassCenter.x - COMPASS_BEZEL_THICKNESS));
        mCompassPointerTip.y = mCompassCenter.y
                - (int) (Math.cos(mWindDirection) * (mCompassCenter.y - COMPASS_BEZEL_THICKNESS));

        Log.v(LOG_TAG, "mCompassCircleX: " + mCompassCircleX
                + ", mCompassCircleY: " + mCompassCircleY);

        Log.v(LOG_TAG, "mWindDirection: " + mWindDirection);
        Log.v(LOG_TAG, "mCompassCenter.x: " + mCompassCenter.x);
        Log.v(LOG_TAG, "mCompassCenter.y: " + mCompassCenter.y);
        Log.v(LOG_TAG, "mCompassPointerBaseLeft.x: " + mCompassPointerBaseLeft.x);
        Log.v(LOG_TAG, "mCompassPointerBaseLeft.y: " + mCompassPointerBaseLeft.y);
        Log.v(LOG_TAG, "mCompassPointerTip.x: " + mCompassPointerTip.x);
        Log.v(LOG_TAG, "mCompassPointerTip.y: " + mCompassPointerTip.y);
        Log.v(LOG_TAG, "mCompassPointerBaseRight.x: " + mCompassPointerBaseRight.x);
        Log.v(LOG_TAG, "mCompassPointerBaseRight.y: " + mCompassPointerBaseRight.y);


        // Draw the compass pointer.
        mCompassPointerPath.moveTo(mCompassCenter.x, mCompassCenter.y);
        mCompassPointerPath.lineTo(mCompassPointerBaseLeft.x, mCompassPointerBaseLeft.y);
        mCompassPointerPath.lineTo(mCompassPointerTip.x, mCompassPointerTip.y);
        mCompassPointerPath.lineTo(mCompassPointerBaseRight.x, mCompassPointerBaseRight.y);
        mCompassPointerPath.close(); // close the triangle
        canvas.drawPath(mCompassPointerPath, mCompassPointerPaint);

    }


}
