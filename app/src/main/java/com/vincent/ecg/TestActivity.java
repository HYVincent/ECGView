package com.vincent.ecg;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.vincent.ecg.view.EcgPointEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent QQ:1032006226
 * @version v1.0
 * @name ECGView
 * @page com.vincent.ecg
 * @class describe
 * @date 2018/3/7 18:52
 */

public class TestActivity extends AppCompatActivity {
    private static final String TAG = TestActivity.class.getSimpleName();
    private RecyclerView rlv;
    //显示的集合 每页的List
    private List<List<EcgPointEntity>> showDatas = new ArrayList<>();
    //屏幕上显示的点
    private int dotNum = 300;


    private EcgAdapter adapter;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x11:
                    adapter.setData(showDatas);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        rlv = findViewById(R.id.rlv);
        initRecycleView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                segmentationData(MainActivity.datas);
            }
        }).start();
    }

    private void initRecycleView() {
        adapter = new EcgAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rlv.setLayoutManager(linearLayoutManager);
        adapter.setData(showDatas);
        rlv.setAdapter(adapter);
    }

    /**
     * 分割数据
     * @param datas
     */
    private void segmentationData(List<EcgPointEntity> datas) {
       /* for (int i = 0;i<datas.size();i++){
            if(i%dotNum == 0 && i!=0){
                showDatas.add(itemDatas);
                itemDatas.clear();
            }
            itemDatas.add(datas.get(i));
        }*/
       int totalPag = datas.size()/dotNum;
       for (int i= 0;i<totalPag;i++){
           List<EcgPointEntity> aa;
           if(i >0){
               aa = datas.subList(i * dotNum-1,(i+1) * dotNum);
           }else {
               aa = datas.subList(i * dotNum, (i + 1) * dotNum);
           }
           showDatas.add(aa);
       }
        showDatas.add(datas.subList(totalPag * dotNum-1,datas.size()-1));

        Log.d(TAG, "segmentationData: "+showDatas.size());
        Log.d(TAG, "segmentationData: aaaaaaa-->"+ JSONArray.toJSONString(showDatas));
        Message message = Message.obtain();
        message.what = 0x11;
        handler.sendMessage(message);
    }
}
