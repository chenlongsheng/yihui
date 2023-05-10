package com.jeeplus.modules.settings.entity;

import java.util.Date;

import com.jeeplus.common.persistence.DataEntity;

/**
 * Created by ZZUSER on 2018/8/27.
 */
public class THistoryDataFinal extends DataEntity<THistoryDataFinal> {
    private String chId;//通道id

    private Double historyValue;//历史值

    private Date historyTime;//历史时间
    private String historyTimeStr;
	
    public String getChId() {
        return chId;
    }

    public void setChId(String chId) {
        this.chId = chId;
    }

    public Double getHistoryValue() {
        return historyValue;
    }

    public void setHistoryValue(Double historyValue) {
        this.historyValue = historyValue;
    }

    public Date getHistoryTime() {
        return historyTime;
    }

    public void setHistoryTime(Date historyTime) {
        this.historyTime = historyTime;
    }

	public String getHistoryTimeStr() {
		return historyTimeStr;
	}

	public void setHistoryTimeStr(String historyTimeStr) {
		this.historyTimeStr = historyTimeStr;
	}
    
    
}
