/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.enterprise.entity;


import com.jeeplus.common.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 预警配置Entity
 * @author ywk
 * @version 2017-04-28
 */
public class TPreAlarmSettings extends DataEntity<TPreAlarmSettings> {
	
	private static final long serialVersionUID = 1L;
	private Long alarmLev;		// alarm_lev
	private Long valueLow;		// value_low
	private Long valueHigh;		// value_high
	private Long smsLev;		// sms_lev
	private Long orgId;		// org_id
	private Long typeId;		// type_id
	private Long codeId;		// code_id
	private Long remindType;		// remind_type
	private String remindPerson;		// remind_person
	private Long duration;		// duration
	
	public TPreAlarmSettings() {
		super();
	}

	public TPreAlarmSettings(Long id){
		super();
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	@ExcelField(title="alarm_lev", align=2, sort=1)
	public Long getAlarmLev() {
		return alarmLev;
	}

	public void setAlarmLev(Long alarmLev) {
		this.alarmLev = alarmLev;
	}
	
	@ExcelField(title="value_low", align=2, sort=2)
	public Long getValueLow() {
		return valueLow;
	}

	public void setValueLow(Long valueLow) {
		this.valueLow = valueLow;
	}
	
	@ExcelField(title="value_high", align=2, sort=3)
	public Long getValueHigh() {
		return valueHigh;
	}

	public void setValueHigh(Long valueHigh) {
		this.valueHigh = valueHigh;
	}
	
	@ExcelField(title="sms_lev", align=2, sort=4)
	public Long getSmsLev() {
		return smsLev;
	}

	public void setSmsLev(Long smsLev) {
		this.smsLev = smsLev;
	}
	
	@ExcelField(title="org_id", align=2, sort=5)
	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	
	@ExcelField(title="type_id", align=2, sort=6)
	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}
	
	@ExcelField(title="code_id", align=2, sort=7)
	public Long getCodeId() {
		return codeId;
	}

	public void setCodeId(Long codeId) {
		this.codeId = codeId;
	}
	
	@ExcelField(title="remind_type", align=2, sort=8)
	public Long getRemindType() {
		return remindType;
	}

	public void setRemindType(Long remindType) {
		this.remindType = remindType;
	}
	
	@ExcelField(title="remind_person", align=2, sort=9)
	public String getRemindPerson() {
		return remindPerson;
	}

	public void setRemindPerson(String remindPerson) {
		this.remindPerson = remindPerson;
	}
	
}