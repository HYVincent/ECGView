package com.vincent.ecg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.vincent.ecg.utils.ReadAssetsFileUtils;
import com.vincent.ecg.view.ECGView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Integer> datas = new ArrayList<>();
    private static final String TAG = MainActivity.class.getSimpleName();
    private ECGView ecgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeData(ReadAssetsFileUtils.readAssetsTxt(this,"StarCareData"));
        ecgView = findViewById(R.id.ecg_view);
        ecgView.setDatas(datas);
    }

    private void changeData(String starCareData) {
        String[] strDatas = starCareData.split("\n");
        for (int i = 0;i<strDatas.length;i++){
            datas.add(Integer.valueOf(strDatas[i].replace("\r","")));
        }
        Log.d(TAG, "changeData: allDatas size is "+String.valueOf(datas.size()));
    }
}
