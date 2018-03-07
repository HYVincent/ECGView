package com.vincent.ecg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONArray;
import com.vincent.ecg.view.ECGView;
import com.vincent.ecg.view.EcgPointEntity;

import java.util.List;

/**
 * @author Vincent QQ:1032006226
 * @version v1.0
 * @name ECGView
 * @page com.vincent.ecg
 * @class describe
 * @date 2018/3/7 18:55
 */

public class EcgAdapter extends RecyclerView.Adapter<EcgAdapter.EcgViewHolder> {

    private List<List<EcgPointEntity>> data;
    private Context mContext;

    public EcgAdapter(Context mContext) {
        this.mContext = mContext;
    }


    public void setData(List<List<EcgPointEntity>> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EcgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        return new EcgViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_layout_ecg,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull EcgViewHolder holder, int position) {
        /*显示的数据*/
        List<EcgPointEntity> datas = data.get(position);
        Log.d("Item", "onBindViewHolder: "+ JSONArray.toJSONString(datas));
        holder.ecgView.setDatas(datas);
        if(position != 0){
            holder.ecgView.setDrawableHead(false);
        }

    }

    @Override
    public int getItemCount() {
        return data == null?0:data.size();
    }

    class EcgViewHolder extends RecyclerView.ViewHolder{

        private ECGView ecgView;

        public EcgViewHolder(View itemView) {
            super(itemView);
            ecgView = itemView.findViewById(R.id.ecgView);
        }
    }
}
