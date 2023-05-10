package com.jeeplus.modules.starnet.entity;

import com.jeeplus.common.persistence.DataEntity;

public class ElectricalEnergyReconciliation extends DataEntity<ElectricalEnergyReconciliation> {

	String loopId;
	
	int exceptionState;
	
	String adjustTime;
	
	int oprType;
	
	int electricQuantity;
	
	Long money;
	
	String name;



	public String getLoopId() {
		return loopId;
	}

	public void setLoopId(String loopId) {
		this.loopId = loopId;
	}

	public int getExceptionState() {
		return exceptionState;
	}

	public void setExceptionState(int exceptionState) {
		this.exceptionState = exceptionState;
	}


	public String getAdjustTime() {
		return adjustTime;
	}

	public void setAdjustTime(String adjustTime) {
		this.adjustTime = adjustTime;
	}

	public int getElectricQuantity() {
		return electricQuantity;
	}

	public void setElectricQuantity(int electricQuantity) {
		this.electricQuantity = electricQuantity;
	}

	public Long getMoney() {
		return money;
	}

	public void setMoney(Long money) {
		this.money = money;
	}

	public int getOprType() {
		return oprType;
	}

	public void setOprType(int oprType) {
		this.oprType = oprType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
}
