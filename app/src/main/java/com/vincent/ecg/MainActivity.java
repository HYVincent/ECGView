package com.vincent.ecg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.vincent.ecg.utils.ReadAssetsFileUtils;
import com.vincent.ecg.view.ECGView;
import com.vincent.ecg.view.ECGView2;
import com.vincent.ecg.view.MyDataAll;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Integer> datas = new ArrayList<>();
    private static final String TAG = MainActivity.class.getSimpleName();
    private ECGView2 ecgView;
    private MyDataAll myDataAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeData(ReadAssetsFileUtils.readAssetsTxt(this,"StarCareData"));
        ecgView = findViewById(R.id.ecg_view);
        ecgView.setDrawHead(true);
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
    }

    private void setMyDataAllOffsetXRatio(float ratio) {
        myDataAll.setOffsetXRatio(ratio);
    }

    private void changeData(String starCareData) {
        String[] strDatas = starCareData.split("\n");
        for (int i = 0;i<strDatas.length;i++){
            datas.add(Integer.valueOf(strDatas[i].replace("\r","")));
        }
        Log.d(TAG, "changeData: allDatas size is "+String.valueOf(datas.size()));
    }
}
