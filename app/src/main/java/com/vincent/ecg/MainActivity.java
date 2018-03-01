package com.vincent.ecg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.vincent.ecg.utils.ReadAssetsFileUtils;
import com.vincent.ecg.utils.TimeUtils;
import com.vincent.ecg.view.ECGView;
import com.vincent.ecg.view.ECGView2;
import com.vincent.ecg.view.EcgData;
import com.vincent.ecg.view.EcgPointEntity;
import com.vincent.ecg.view.MyData;
import com.vincent.ecg.view.MyDataAll;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private List<EcgPointEntity> datas = new ArrayList<>();
    private static final String TAG = MainActivity.class.getSimpleName();
    private MyData myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeData(ReadAssetsFileUtils.readAssetsTxt(this,"StarCareData"));
        myData = findViewById(R.id.mydata);
        startTime(-1, 0, 1500/125L, 5, new TimeUtils.TimeListener() {
            @Override
            public void doAction(final int index) {
//                Log.d(TAG, "doAction: index = "+String.valueOf(index));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(index > datas.size()-1){
                            cancelTimeTask();
                            return;
                        }
                        myData.addData(datas.get(index));
                    }
                });
            }
        });
    }

    private boolean isRed = false;

    private void changeData(String starCareData) {
        String[] strDatas = starCareData.split("\n");
        for (int i = 0;i<strDatas.length;i++){
            EcgPointEntity ecgData = new EcgPointEntity();
            ecgData.setData(Integer.valueOf(strDatas[i].replace("\r","")));
            if(i % 50 == 0){
                if(isRed){
                    isRed = false;
                }else {
                    isRed = true;
                }
            }
            ecgData.setRed(isRed);
            datas.add(ecgData);
        }
//        Log.d(TAG, "changeData: allDatas size is "+String.valueOf(datas.size()));
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
