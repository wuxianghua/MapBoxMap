package com.org.mylibrary.yichemap.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.org.mylibrary.R;
import com.palmaplus.nagrand.view.MapView;


/**
 * Created by 王天明 on 2016/5/6.
 * 指南针视图
 */
public class CompassView extends View {

    /**
     * 指南针图片
     */
    private Bitmap bitmap;
    /**
     * 旋转角度
     */
    private float rotateAngle;

    private Paint paint;

    private final float defaultRotateAngle = 0;

    private int width, height;

    public CompassView(Context context) {
        super(context);
        initalizeAttr(context, null);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initalizeAttr(context, attrs);
    }

    private void initalizeAttr(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CompassView);
        int bitmapResId = typedArray.getResourceId(R.styleable.CompassView_bitMapRes, 0);
        if (bitmapResId != 0) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), bitmapResId);
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }
        rotateAngle = typedArray.getFloat(R.styleable.CompassView_rotateAngle, defaultRotateAngle);
        typedArray.recycle();

        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null) {
            canvas.rotate(rotateAngle, width / 2, height / 2);
            canvas.drawBitmap(bitmap, 0, 0, paint);
//            canvas.restore();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 设置旋转角度
     *
     * @param rotateAngle
     */
    public void setRotateAngle(float rotateAngle) {
        this.rotateAngle = rotateAngle;
        postInvalidate();
    }

    public float getRotateAngle() {
        return rotateAngle;
    }

    public void reset() {
        setRotateAngle(defaultRotateAngle);
    }

    private class RefreshTasK implements Runnable {
        private int index = 0;
        private int maxIndex = 0;
        private MapView mapView;

        private RefreshTasK(MapView mapView, long time) {
            this.maxIndex = (int) (time / 15 + 1);
            this.mapView = mapView;
        }

        @Override
        public void run() {
            if (index < maxIndex) {
                postInvalidate();
                setRotateAngle(-(float) mapView.getRotate());
                postDelayed(this, 15);
                index++;
            }else{
                removeCallbacks(this);
            }
        }
    }

    private RefreshTasK refreshTasK;

    public void animRefresh(MapView mapView, long time) {
        if (mapView == null) {
            return;
        }
        if (refreshTasK != null) {
            removeCallbacks(refreshTasK);
            refreshTasK = null;
        }
        refreshTasK = new RefreshTasK(mapView,time);
        this.post(refreshTasK);
    }

}
