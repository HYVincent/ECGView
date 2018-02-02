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
import com.vincent.ecg.view.MyData;
import com.vincent.ecg.view.MyDataAll;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<EcgData> datas = new ArrayList<>();
    private static final String TAG = MainActivity.class.getSimpleName();
    private ECGView2 ecgView;
    private MyDataAll myDataAll;
    private MyData myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeData(ReadAssetsFileUtils.readAssetsTxt(this,"StarCareData"));
        ecgView = findViewById(R.id.ecg_view);
        ecgView.setDrawHead(false);
        ecgView.setDatas(datas);
        ecgView.setMoveViewListener(new ECGView2.MoveViewListener() {
            @Override
            public void soffsetX(float maxOffsetX,float offsetX) {
                Log.d(TAG, "soffsetX: 最大偏移量 = "+String.valueOf(maxOffsetX)+",当前偏移量 = "+String.valueOf(offsetX));
                //偏移量比例
                float ratio = offsetX / maxOffsetX;
                setMyDataAllOffsetXRatio(ratio);
            }
        });
        myDataAll = findViewById(R.id.data_all);
        myDataAll.addAllData(datas);
        myDataAll.setMoveViewListener(new MyDataAll.MoveViewListener() {
            @Override
            public void soffsetX(float maxOffsetX, float offsetX) {
                Log.d(TAG, "soffsetX: 最大偏移量 = "+String.valueOf(maxOffsetX)+",当前偏移量 = "+String.valueOf(offsetX));
                ecgView.setXChangedRatio(offsetX/maxOffsetX);
            }
        });
        myData = findViewById(R.id.mydata);
        myData.setDrawHead(true);
        myData.setAddValues(5);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeUtils.startTime(-1, 1000, 1000, 10, new TimeUtils.TimeListener() {
                    @Override
                    public void doAction(int index) {
                        Log.d(TAG, "doAction: index = "+index);
                        int mm = index %4;
                        switch (mm){
                            case 0:
                               refresh(5);
                                break;
                            case 1:
                                refresh(10);
                                break;
                            case 2:
                                refresh(20);
                                break;
                            case 3:
                                refresh(30);
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        });
    }

    private void refresh(final float values){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                myData.setHeadStart(values);
                myData.setAddValues(values);
//                myData.setAddValues(10);
//                myData.setAddValues(20);
//                myData.setAddValues(30);
            }
        });

    }

    private void setMyDataAllOffsetXRatio(float ratio) {
        myDataAll.setOffsetXRatio(ratio);
    }

    private void changeData(String starCareData) {
        String[] strDatas = starCareData.split("\n");
        for (int i = 0;i<strDatas.length;i++){
            EcgData ecgData = new EcgData();
            ecgData.setData(Integer.valueOf(strDatas[i].replace("\r","")));
            datas.add(ecgData);
        }
        Log.d(TAG, "changeData: allDatas size is "+String.valueOf(datas.size()));
    }



}
