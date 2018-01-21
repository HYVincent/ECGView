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
import android.view.MotionEvent;
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
    //表示每个小格子的点的个数
    private int gridDotNumber = 5;
    //ECG path
    private Path path;
    /**
     * 点的 125个数据 = 5个大格子 = 25个小格子,一个小格子有5个数据
     * dotWidth 表示一个点在X轴的宽度,确切的说是两个点的间距
     */
    private float dotWidth = smallGridWidth/gridDotNumber;
    //x轴原点坐标
    private float xori;
    //滑动查看时，x坐标的变化
    private float x_change ;
    private float x_changed;
    //X轴的最大偏移量
    private float offset_x_max;
    //手指触碰屏幕的x坐标
    private float startX;

    private MoveViewListener moveViewListener;



    public ECGView(Context context) {
        super(context);
        init(context);
    }

    public void setMoveViewListener(MoveViewListener moveViewListener) {
        this.moveViewListener = moveViewListener;
    }

    public ECGView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ECGView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewWidth = w;
        viewHeight = h;
        //基线的位置为View高度的一半
        baseLine = viewHeight/2;
        xori = 0.0f;
        x_change = 0.0f;
        x_changed = 0.0f;
        //计算偏移量 因为画背景是先画的基线，然后向两边衍生
        lineNumberZ = (int)(viewWidth/smallGridWidth);
        offset_x_max = viewWidth - dotWidth * datas.size();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 设置偏移量
     * @param ratio 这个值并不是一个具体的偏移量的值，而是一个相对于最大偏移量的比例
     */
    public void setXChangedRatio(float ratio) {
        this.x_changed = offset_x_max * ratio;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);
        //画基准线
        drawBaseLine(canvas);
        //画数据
        drawData(canvas);
    }

    private Paint mPaint;

    //绘制数据
    private void drawData(Canvas canvas) {
        //清除路径
        path.reset();
        x_changed += x_change;
        if (x_changed > xori){//防止向右滑动太多 超左边界
            x_changed = xori;
        }else if (x_changed < offset_x_max ){//防止向左滑动太多 超右边界
            x_changed = offset_x_max;
        }
        //此处 xori设置为0 ，未用上
        int iXor = 1;
        for (int i = 1 ; i < this.datas.size() ; i ++){
            float nnn = xori + dotWidth * i +  x_changed;//表示为偏移之后点的X轴坐标
            if (nnn >= 0 ){
                iXor = i;
                path.moveTo(nnn, valuesToY(datas.get(i)));
                break;
            }
        }

        for (int i = iXor; i < this.datas.size(); i ++){
            float nnn = xori + dotWidth * i +  x_changed;
            if (nnn < viewWidth + dotWidth){
                path.lineTo(xori + dotWidth * i +  x_changed , valuesToY(datas.get(i)));
            }
        }
        canvas.drawPath(path,mPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                x_change = event.getX() - startX;
                moveViewListener.soffsetX(offset_x_max,x_changed);
                invalidate();
                break;
        }
        return true;
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
    }

    /**
     * 绘制背景颜色
     * @param canvas
     */
    private void drawBg(Canvas canvas) {
        /*初始化背景画笔*/
        Paint mBgPaint = new Paint();
        //抗锯齿
        mBgPaint.setAntiAlias(true);
        /*背景颜色*/
        mBgPaint.setColor(bgColor);
        /*宽度*/
        mBgPaint.setStrokeWidth(bgLineWidth);

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
    }

    private void init(Context context) {

        //初始化心电图画笔
        mPaint = new Paint();
        mPaint.setStrokeWidth(ecgWidth);
        mPaint.setColor(Color.parseColor("#07aef5"));
        mPaint.setAntiAlias(true);

        //设置样式
        mPaint.setStyle(Paint.Style.STROKE);

        //初始化基线画笔
        mBaseLine = new Paint();
        mBaseLine.setStrokeWidth(mBaseLineWidth);
        mBaseLine.setAntiAlias(true);
        mBaseLine.setColor(mBaseLineColor);
        mBaseLine.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
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

    public interface MoveViewListener{
        void soffsetX(float maxOffsetX,float offsetX);
    }

}

