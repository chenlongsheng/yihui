package com.jeeplus.modules.starnet.entity;

import com.jeeplus.common.persistence.DataEntity;

public class ElecUnitStrategy extends DataEntity<ElecUnitStrategy> {
	
	long unitId;
	
	String oprType;
	
	int value;
	
	int realValue;
	
	String month;
	
	String operator;
	
	String name;
	

	public long getUnitId() {
		return unitId;
	}

	public void setUnitId(long unitId) {
		this.unitId = unitId;
	}

	public int getValue() {
		return value;
	}

	public String getOprType() {
		return oprType;
	}

	public void setOprType(String oprType) {
		this.oprType = oprType;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public int getRealValue() {
		return realValue;
	}

	public void setRealValue(int realValue) {
		this.realValue = realValue;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}




	

}
