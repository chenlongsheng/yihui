package com.jeeplus.modules.settings.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jeeplus.common.persistence.DataEntity;

import java.util.Date;

/**
 * Created by Administrator on 2018-08-15.
 */
public class TRealData extends DataEntity<TRealData> {
    private static final long serialVersionUID = 1L;

    private Integer realValue;
    private Date realTime;
    private String warn;
    private Date startTime;
    private String endMinute;
    //开始充电时间
    private Date chargeStart;
    //套餐结束时间
    private Date chargeEnd;


    public Integer getRealValue() {
        return realValue;
    }

    public void setRealValue(Integer realValue) {
        this.realValue = realValue;
    }

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getRealTime() {
        return realTime;
    }

    public void setRealTime(Date realTime) {
        this.realTime = realTime;
    }

    public String getWarn() {
        return warn;
    }

    public void setWarn(String warn) {
        this.warn = warn;
    }

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(String endMinute) {
        this.endMinute = endMinute;
    }

    @JsonFormat(timezone = "GMT+8",  pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getChargeStart() {
        return chargeStart;
    }

    public void setChargeStart(Date chargeStart) {
        this.chargeStart = chargeStart;
    }

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getChargeEnd() {
        return chargeEnd;
    }

    public void setChargeEnd(Date chargeEnd) {
        this.chargeEnd = chargeEnd;
    }
}
