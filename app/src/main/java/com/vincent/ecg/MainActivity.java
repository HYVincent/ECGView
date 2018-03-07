package com.vincent.ecg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.vincent.ecg.utils.DateUtils;
import com.vincent.ecg.utils.ReadAssetsFileUtils;
import com.vincent.ecg.utils.TimeUtils;
import com.vincent.ecg.view.ECGView;
import com.vincent.ecg.view.ECGView2;
import com.vincent.ecg.view.EcgData;
import com.vincent.ecg.view.EcgPointEntity;
import com.vincent.ecg.view.MyData;
import com.vincent.ecg.view.MyDataAll;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private List<EcgPointEntity> datas = new ArrayList<>();
    private static final String TAG = MainActivity.class.getSimpleName();
    private ECGView myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeData(ReadAssetsFileUtils.readAssetsTxt(this,"StarCareData"));
        myData = findViewById(R.id.ecgView);
        myData.setMoveViewListener(new ECGView.MoveViewListener() {
            @Override
            public void soffsetX(float maxOffsetX, float offsetX) {

            }
        });
        myData.setDatas(datas);
    }

    private boolean isRed = false;

    private void changeData(String starCareData) {
        String[] strDatas = starCareData.split("\n");
        long time = System.currentTimeMillis();
        for (int i = 0;i<strDatas.length;i++){
            EcgPointEntity ecgData = new EcgPointEntity();
            ecgData.setData(Integer.valueOf(strDatas[i].replace("\r","")));
            if(i % 125 == 0){
                if(isRed){
                    isRed = false;
                }else {
                    isRed = true;
                }
            }
            if(i % 125 == 0){
                //变化
                time += 1000L;
            }
            Log.d(TAG, "changeData: time = "+ DateUtils.getDateString(DateUtils.DATE_FORMAT_ALL,time));
            ecgData.setDate(new Date(time));
            ecgData.setRed(isRed);
            datas.add(ecgData);
        }
        Log.d(TAG, "changeData: allDatas size is "+ JSONArray.toJSONString(datas));
    }

    private ScheduledExecutorService scheduledExecutorService;

    public void startTime(int initValues,long delayTime,long interval,  int threadNum, final TimeUtils.TimeListener timeListener) {
        try {
            final AtomicInteger atomicInteger = new AtomicInteger(initValues);
            if (threadNum == 0) {
                threadNum = 1;
            }
            scheduledExecutorService = new ScheduledThreadPoolExecutor(threadNum);
            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    timeListener.doAction(atomicInteger.incrementAndGet());
//                timeListener.doAction(1);
                }
            }, delayTime, interval, TimeUnit.MILLISECONDS);
        }catch (Exception e){

        }
    }


    public void cancelTimeTask(){
        if(scheduledExecutorService!= null){
            scheduledExecutorService.shutdownNow();
        }
    }


}
