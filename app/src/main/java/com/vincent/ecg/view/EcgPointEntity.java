package com.vincent.ecg.view;

import java.util.Date;

/**
 * @author Vincent QQ:1032006226
 * @version v1.0
 * @name StartKangMedical_Android
 * @page com.toncentsoft.starkangmedical_android.entity
 * @class describe
 * @date 2018/2/1 15:32
 */

public class EcgPointEntity {
    //记录一个时间，这个时间是数据产生的时间
    private long time;
    private Integer data;
    private boolean isRed;
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }

    public boolean isRed() {
        return isRed;
    }

    public void setRed(boolean red) {
        isRed = red;
    }
}
