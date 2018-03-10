package com.vincent.ecg;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONArray;
import com.elvishew.xlog.XLog;
import com.vincent.ecg.utils.ScreenUtils;
import com.vincent.ecg.view.EcgPointEntity;
import com.vincent.mylibrary.view.SpaceItemDecoration;

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
    private float screenWidth = 0f;
    //屏幕上能显示的最大的点
    private int screenMaxDotNum=0;

    private EcgAdapter adapter;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x11:
                    adapter.setData(showDatas);
                    XLog.d(JSONArray.toJSONString(showDatas));
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
        screenMaxDotNum = 350;
        screenWidth = screenMaxDotNum * 4;
        Log.d(TAG, "onCreate: "+screenMaxDotNum);
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

        rlv.addItemDecoration(new SpaceItemDecoration(0));

        rlv.setLayoutManager(linearLayoutManager);
        adapter.setData(showDatas);
        rlv.setAdapter(adapter);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)rlv.getLayoutParams();
        layoutParams.setMargins(20*5*2,0,0,0);
    }

    /**
     * 分割数据
     * @param datas
     */
    private void segmentationData(List<EcgPointEntity> datas) {
       int totalPag =  (datas.size()/screenMaxDotNum);
       for (int i= 0;i<totalPag;i++){
           List<EcgPointEntity> aa;
           if(i >0){
               aa = datas.subList(i * screenMaxDotNum-1,(i+1) * screenMaxDotNum);
           }else {
               aa = datas.subList(i * screenMaxDotNum, (i + 1) * screenMaxDotNum);
           }
           showDatas.add(aa);
       }
        showDatas.add(datas.subList(totalPag * screenMaxDotNum-1,datas.size()-1));

        Log.d(TAG, "segmentationData: "+showDatas.size());
        Log.d(TAG, "segmentationData: aaaaaaa-->"+ JSONArray.toJSONString(showDatas));
        Message message = Message.obtain();
        message.what = 0x11;
        handler.sendMessage(message);
    }
}
