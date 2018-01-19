package com.vincent.ecg.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent QQ:1032006226
 * @version v1.0
 * @name ECGView
 * @page com.vincent.ecg.view
 * @class describe
 * @date 2018/1/19 18:58
 */
public class ECGView extends View {

    private static final String TAG = ECGView.class.getSimpleName();

    //View宽度
    private float viewWidth;
    //View高度
    private float viewHeight;
    //基线的位置
    private float baseLine;
    //ECG datas
    private List<Integer> datas = new ArrayList<>();
    //心电图画笔
    private Paint mPaint;

    //背景画笔
    private Paint mBgPaint;
    //小格子的颜色
    private int bgColor = Color.parseColor("#53bfed");
    //小格子线条宽度
    private float bgLineWidth = 2f;
    //纵向有多少条线
    private int lineNumberZ;

    //基线画笔
    private Paint mBaseLine;
    //基线的宽度
    private float mBaseLineWidth = 4f;
    //基准线的颜色
    private int mBaseLineColor = Color.RED;
    //心电图的线的宽度
    private float ecgWidth = 4f;
    //小格子的宽度
    private float smallGridWidth = 20;
    //ECG path
    private Path path;
    /**
     * 点的宽度 125个数据 = 5个大格子 = 25个小格子,一个小格子有5个数据
     * dotWidth 表示一个点在X轴的宽度
     */
    private float dotWidth = smallGridWidth/5;
    //这个的意思是两个大格子表示的值是200  200 = 2个大格子的高度 = 2 * 1个大格子的高度 = 2 * 5 个小格子的高度 一个小格子表示的值为20
    private float gridValues = 200/2/5;


    public ECGView(Context context) {
        super(context);
        init();
    }



    public ECGView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ECGView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewWidth = w;
        viewHeight = h;
        //基线的位置为View高度的一半
        baseLine = viewHeight/2;
        //计算偏移量 因为画背景是先画的基线，然后向两边衍生
        lineNumberZ = (int)(viewWidth/smallGridWidth);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private boolean isDrawBg = false;
    private boolean isDrawBaseLine = false;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isDrawBg){
            drawBg(canvas);
        }
       if(!isDrawBaseLine){
           //画基准线
           drawBaseLine(canvas);
       }
        //画数据
        drawData(canvas);
    }

    //绘制数据
    private void drawData(Canvas canvas) {
        //清除路径
        path.reset();
        //移动到起点 这里从中部开始画
        path.moveTo(0,baseLine);
        for (int i = 0;i<datas.size()/10;i++){
            Log.d(TAG, "drawData: "+dotWidth *i+","+valuesToY(datas.get(i)));
            path.lineTo(dotWidth * i,valuesToY(datas.get(i)));
            canvas.drawPath(path,mPaint);
        }
    }

    /**
     * 这个的意思是两个大格子表示的值是200  200 = 2个大格子的高度 = 2 * 1个大格子的高度 = 2 * 5 个小格子的高度 一个小格子表示的值为20
     * 1 小格的数据 表示为20
     * @param data
     * @return
     */
    private float valuesToY(Integer data){
//        return  (-1.0f) * data /(100.0f) * (20.0f) + baseLine;
        return  data * (-1.0f) + baseLine;
    }


    /**
     * 绘制基线
     * @param canvas
     */
    private void drawBaseLine(Canvas canvas) {
        //画基线
        canvas.drawLine(0,baseLine,viewWidth,baseLine,mBaseLine);
        isDrawBaseLine = true;
    }

    /**
     * 绘制背景颜色
     * @param canvas
     */
    private void drawBg(Canvas canvas) {
        //先画横向的线 画上部分
       for (float i = baseLine;i>0;i--){
           if(i % 5 == 0){
               mBgPaint.setStrokeWidth(bgLineWidth * 2);
           }else {
               mBgPaint.setStrokeWidth(bgLineWidth);
           }
           canvas.drawLine(0,baseLine - smallGridWidth * i,viewWidth,baseLine - smallGridWidth * i,mBgPaint);
       }
       //画下部分
        for (float i = 0;i<viewHeight;i++){
            if(i % 5 == 0){
                mBgPaint.setStrokeWidth(bgLineWidth * 2);
            }else {
                mBgPaint.setStrokeWidth(bgLineWidth);
            }
            canvas.drawLine(0,baseLine + smallGridWidth * i,viewWidth,baseLine + smallGridWidth * i,mBgPaint);
        }
        //画纵向的线 从零开始就可以了
      for(int i = 0; i<lineNumberZ;i++){
            if(i % 5 == 0){
                mBgPaint.setStrokeWidth(bgLineWidth * 2);
            }else {
                mBgPaint.setStrokeWidth(bgLineWidth);
            }
            canvas.drawLine(i * smallGridWidth,0,i * smallGridWidth,viewHeight,mBgPaint);
        }
        isDrawBg = true;
    }

    private void init() {
        /*初始化背景画笔*/
        mBgPaint = new Paint();
        //抗锯齿
        mBgPaint.setAntiAlias(true);
        /*背景颜色*/
        mBgPaint.setColor(bgColor);
        /*宽度*/
        mBgPaint.setStrokeWidth(bgLineWidth);



        //初始化基线画笔
        mBaseLine = new Paint();
        mBaseLine.setStrokeWidth(mBaseLineWidth);
        mBaseLine.setAntiAlias(true);
        mBaseLine.setColor(mBaseLineColor);
        mBaseLine.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));

        //初始化心电图画笔
        mPaint = new Paint();
        mPaint.setStrokeWidth(ecgWidth);
        mPaint.setColor(Color.parseColor("#07aef5"));
        mPaint.setAntiAlias(true);
        //设置样式
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));

        path = new Path();
    }


    /**
     * 绘制数据
     * @param datas
     */
    public void setDatas(List<Integer> datas) {
        this.datas = datas;
        invalidate();
    }
}

