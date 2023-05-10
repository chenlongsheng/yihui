package com.jeeplus.modules.starnet.entity;

import com.jeeplus.common.persistence.DataEntity;

public class ConsumptionWithLoss extends DataEntity<ConsumptionWithLoss> {

	String id;
	String loopOrgId;
	Double consumptionWithLoss;
	Double consumption;
	String month;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLoopOrgId() {
		return loopOrgId;
	}
	public void setLoopOrgId(String loopOrgId) {
		this.loopOrgId = loopOrgId;
	}
	public Double getConsumptionWithLoss() {
		return consumptionWithLoss;
	}
	public void setConsumptionWithLoss(Double consumptionWithLoss) {
		this.consumptionWithLoss = consumptionWithLoss;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public Double getConsumption() {
		return consumption;
	}
	public void setConsumption(Double consumption) {
		this.consumption = consumption;
	}
	
}
