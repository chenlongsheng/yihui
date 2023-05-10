/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.enterprise.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.jeeplus.common.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 报警日志Entity
 * @author ywk
 * @version 2017-05-25
 */
public class TAlarmLog extends DataEntity<TAlarmLog> {
	
	private static final long serialVersionUID = 1L;
	private Long chId;		// 通道ID
	private Long alarmLevel;		// 报警级别
	private Date occurTime;		// 报警时间
	private String procDesc;		// 处理描述
	private Long procUserId;		// 用户id
	private Date procTime;		// 处理时间
	private Long status;		// 报警状态0:正常,1:失效
	private Date procEndTime;		// 处理结束时间
	private Date latestOccurTime;		// 最后一次收到报警事件（同一报警的持续事件）
	private Long beenDone;		// 0=未接警、1=已接警未处警、2=处警挂起、            3=指定派单            31=派单接收            32=派单挂起            33=派单完成（待审核）            34=派单审核             35=已审核  4已处警
	private Long alarmType;		// 1.消防报警  2.防盗报警  3.设备异常告警 4.设备故障告警
	private Long devId;		// 报警所属设备id
	private Long alarmGroupId;		// alarm_group_id
	private Date takenTime;		// taken_time
	private Date timeToDealWith;		// time_to_deal_with
	private Date sendOrderTime;		// send_order_time
	private Date finishTime;		// finish_time
	private Long alarmValue;		// 报警值
	private Long realValue;		// 实际值
	
	public TAlarmLog() {
		super();
	}

	public TAlarmLog(String id){
		super(id);
	}

	@ExcelField(title="通道ID", align=2, sort=1)
	public Long getChId() {
		return chId;
	}

	public void setChId(Long chId) {
		this.chId = chId;
	}
	
	@ExcelField(title="报警级别", align=2, sort=2)
	public Long getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(Long alarmLevel) {
		this.alarmLevel = alarmLevel;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="报警时间", align=2, sort=3)
	public Date getOccurTime() {
		return occurTime;
	}

	public void setOccurTime(Date occurTime) {
		this.occurTime = occurTime;
	}
	
	@ExcelField(title="处理描述", align=2, sort=4)
	public String getProcDesc() {
		return procDesc;
	}

	public void setProcDesc(String procDesc) {
		this.procDesc = procDesc;
	}
	
	@ExcelField(title="用户id", align=2, sort=5)
	public Long getProcUserId() {
		return procUserId;
	}

	public void setProcUserId(Long procUserId) {
		this.procUserId = procUserId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="处理时间", align=2, sort=6)
	public Date getProcTime() {
		return procTime;
	}

	public void setProcTime(Date procTime) {
		this.procTime = procTime;
	}
	
	@ExcelField(title="报警状态0:正常,1:失效", align=2, sort=7)
	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="处理结束时间", align=2, sort=8)
	public Date getProcEndTime() {
		return procEndTime;
	}

	public void setProcEndTime(Date procEndTime) {
		this.procEndTime = procEndTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="最后一次收到报警事件（同一报警的持续事件）", align=2, sort=9)
	public Date getLatestOccurTime() {
		return latestOccurTime;
	}

	public void setLatestOccurTime(Date latestOccurTime) {
		this.latestOccurTime = latestOccurTime;
	}
	
	@ExcelField(title="0=未接警、1=已接警未处警、2=处警挂起、            3=指定派单            31=派单接收            32=派单挂起            33=派单完成（待审核）            34=派单审核             35=已审核  4已处警", align=2, sort=10)
	public Long getBeenDone() {
		return beenDone;
	}

	public void setBeenDone(Long beenDone) {
		this.beenDone = beenDone;
	}
	
	@ExcelField(title="1.消防报警  2.防盗报警  3.设备异常告警 4.设备故障告警", align=2, sort=11)
	public Long getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(Long alarmType) {
		this.alarmType = alarmType;
	}
	
	@ExcelField(title="报警所属设备id", align=2, sort=12)
	public Long getDevId() {
		return devId;
	}

	public void setDevId(Long devId) {
		this.devId = devId;
	}
	
	@ExcelField(title="alarm_group_id", align=2, sort=13)
	public Long getAlarmGroupId() {
		return alarmGroupId;
	}

	public void setAlarmGroupId(Long alarmGroupId) {
		this.alarmGroupId = alarmGroupId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="taken_time", align=2, sort=14)
	public Date getTakenTime() {
		return takenTime;
	}

	public void setTakenTime(Date takenTime) {
		this.takenTime = takenTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="time_to_deal_with", align=2, sort=15)
	public Date getTimeToDealWith() {
		return timeToDealWith;
	}

	public void setTimeToDealWith(Date timeToDealWith) {
		this.timeToDealWith = timeToDealWith;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="send_order_time", align=2, sort=16)
	public Date getSendOrderTime() {
		return sendOrderTime;
	}

	public void setSendOrderTime(Date sendOrderTime) {
		this.sendOrderTime = sendOrderTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="finish_time", align=2, sort=17)
	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}
	
	@ExcelField(title="报警值", align=2, sort=18)
	public Long getAlarmValue() {
		return alarmValue;
	}

	public void setAlarmValue(Long alarmValue) {
		this.alarmValue = alarmValue;
	}
	
	@ExcelField(title="实际值", align=2, sort=19)
	public Long getRealValue() {
		return realValue;
	}

	public void setRealValue(Long realValue) {
		this.realValue = realValue;
	}
	
}