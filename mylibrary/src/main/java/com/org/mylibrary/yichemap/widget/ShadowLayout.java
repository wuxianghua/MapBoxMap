package com.org.mylibrary.yichemap.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.org.mylibrary.R;

/**
 * Created by 王天明 on 2016/9/28.
 */
public class ShadowLayout extends LinearLayout {

    private static final String TAG = "ShadowLayout";
    private static final boolean DEBUG = true;


    private static class DefaultValue {
        public static int OFFSET = 50;
        public static int DX = 0;
        public static int DY = 0;
        public static int FILLCOLOR = Color.WHITE;
        public static int SHADOWCOLOR = 0x77000000;
        public static int SHADOWRADIUS = 20;
        public static int TYPE = 0;
        public static int RX = 0;//5
        public static int RY = 0;//5
        public static boolean HOLE = false;
    }

    /**
     * 阴影画笔
     */
    private Paint shadowPaint = null;
    /**
     * 阴影大小
     */
    private float offset = DefaultValue.OFFSET;
    /**
     * 阴影偏移量-- x
     */
    private float dx = DefaultValue.DX;
    /**
     * 阴影偏移量 -- y
     */
    private float dy = DefaultValue.DY;
    /**
     * 圆角矩形的rx
     */
    private float rx = DefaultValue.RX;
    /**
     * 圆角矩形的ry
     */
    private float ry = DefaultValue.RY;
    /**
     * 背景色
     */
    private int fillColor = DefaultValue.FILLCOLOR;
    /**
     * 阴影半径
     */
    private float shadowRadius = DefaultValue.SHADOWRADIUS;
    /**
     * 阴影颜色
     */
    private int shadowColor = DefaultValue.SHADOWCOLOR;
    /**
     * 阴影类型 0--圆角矩形  1--圆形
     */
    private int type = DefaultValue.TYPE;
    /**
     * 是否镂空
     */
    private boolean hole = DefaultValue.HOLE;

    public ShadowLayout(Context context) {
        this(context, null);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置该layout响应onDraw事件
        setWillNotDraw(false);
        //避免阴影无效
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        initAttrs(attrs);
        initialize();
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ShadowLayout);
        offset = dip2px(a.getDimension(R.styleable.ShadowLayout_offset, DefaultValue.OFFSET));
        dx = dip2px(a.getDimension(R.styleable.ShadowLayout_dx, DefaultValue.DX));
        dy = dip2px(a.getDimension(R.styleable.ShadowLayout_dy, DefaultValue.DY));
        rx = dip2px(a.getDimension(R.styleable.ShadowLayout_rx, DefaultValue.RX));
        ry = dip2px(a.getDimension(R.styleable.ShadowLayout_ry, DefaultValue.RY));
        shadowRadius = dip2px(a.getDimension(R.styleable.ShadowLayout_shadowRadius, DefaultValue.SHADOWRADIUS));
        fillColor = a.getColor(R.styleable.ShadowLayout_fillColor, DefaultValue.FILLCOLOR);
        shadowColor = a.getColor(R.styleable.ShadowLayout_shadowColor, DefaultValue.SHADOWCOLOR);
        type = a.getInteger(R.styleable.ShadowLayout_type, DefaultValue.TYPE);
        hole = a.getBoolean(R.styleable.ShadowLayout_hole, DefaultValue.HOLE);
        a.recycle();
    }

    private void initialize() {
        int iOffset = (int) offset;
        setPadding(iOffset, iOffset, iOffset, iOffset);
        /*getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LogUtil.e("onGlobalLayout");
//                invalidate();
            }
        });*/
    }

    private void initPaint() {
        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);//去除锯齿。
        shadowPaint.setFilterBitmap(true);
        shadowPaint.setDither(true);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(fillColor);
        shadowPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor);
        shadowPaint.setXfermode(null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        initPaint();
        if (1 == type) {
            //获取最长
            int maxLine = width > height ? width : height;
            canvas.drawCircle(width / 2, height / 2, (maxLine - offset - offset) / 2, shadowPaint);
            if (hole) {
                shadowPaint.setShadowLayer(0, dx, dy, shadowColor);
                shadowPaint.setColor(Color.TRANSPARENT);
                shadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
                canvas.drawCircle(width / 2, height / 2, (maxLine - offset - offset) / 2, shadowPaint);
            }
        } else {
            canvas.drawRoundRect(
                    new RectF(offset, offset, width - offset, height - offset),
                    rx, ry,
                    shadowPaint
            );
            if (hole) {
                shadowPaint.setShadowLayer(0, dx, dy, shadowColor);
                shadowPaint.setColor(Color.TRANSPARENT);
                shadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
                canvas.drawRoundRect(
                        new RectF(offset, offset, width - offset, height - offset),
                        rx, ry,
                        shadowPaint
                );
            }
        }
    }

    private void d(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    private float dip2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }
}