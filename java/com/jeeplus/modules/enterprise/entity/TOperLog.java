/**
 * Copyright &copy; 2015-2020 
 */
package com.jeeplus.modules.enterprise.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jeeplus.modules.sys.entity.User;

import javax.validation.constraints.NotNull;

import com.jeeplus.common.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 操作日志Entity
 * @author ywk
 * @version 2017-04-11
 */
public class TOperLog extends DataEntity<TOperLog> {
	
	private static final long serialVersionUID = 1L;
	private Date addTime;		// 添加时间
	private String ip;		// IP
	private String oprContent;		// OPR_CONTENT
	private User user;		// 用户ID
	private Long logCode;		// 日志编码
	private Long devId;		// 设备id
	private Long chId;		// 通道id
	private String creator;		// creator
	private Date crDate;		// cr_date
	private String crUnit;		// cr_unit
	private String modificator;		// modificator
	private Date moDate;		// mo_date
	private String moUnit;		// mo_unit
	private Date date;
	private String userId;	
	
	public TOperLog() {
		super();
	}

	public TOperLog(Long id){
		super();
	}

	/**
	 * @param addTime
	 * @param ip
	 * @param oprContent
	 * @param user
	 * @param logCode
	 * @param devId
	 * @param chId
	 * @param creator
	 * @param crDate
	 * @param crUnit
	 * @param modificator
	 * @param moDate
	 * @param moUnit
	 */
	public TOperLog(Date addTime, String ip, String oprContent, User user, Long logCode, Long devId, Long chId,
			String creator, Date crDate, String crUnit, String modificator, Date moDate, String moUnit) {
		super();
		this.addTime = addTime;
		this.ip = ip;
		this.oprContent = oprContent;
		this.user = user;
		this.logCode = logCode;
		this.devId = devId;
		this.chId = chId;
		this.creator = creator;
		this.crDate = crDate;
		this.crUnit = crUnit;
		this.modificator = modificator;
		this.moDate = moDate;
		this.moUnit = moUnit;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="添加时间", align=2, sort=1)
	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	
	@ExcelField(title="IP", align=2, sort=2)
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	@ExcelField(title="OPR_CONTENT", align=2, sort=3)
	public String getOprContent() {
		return oprContent;
	}

	public void setOprContent(String oprContent) {
		this.oprContent = oprContent;
	}
	
	@ExcelField(title="用户ID", fieldType=User.class, value="user.name", align=2, sort=4)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@ExcelField(title="日志编码", align=2, sort=5)
	public Long getLogCode() {
		return logCode;
	}

	public void setLogCode(Long logCode) {
		this.logCode = logCode;
	}
	
	@ExcelField(title="设备id", align=2, sort=6)
	public Long getDevId() {
		return devId;
	}

	public void setDevId(Long devId) {
		this.devId = devId;
	}
	
	@ExcelField(title="通道id", align=2, sort=7)
	public Long getChId() {
		return chId;
	}

	public void setChId(Long chId) {
		this.chId = chId;
	}
	
	@ExcelField(title="creator", align=2, sort=8)
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="cr_date不能为空")
	@ExcelField(title="cr_date", align=2, sort=9)
	public Date getCrDate() {
		return crDate;
	}

	public void setCrDate(Date crDate) {
		this.crDate = crDate;
	}
	
	@ExcelField(title="cr_unit", align=2, sort=10)
	public String getCrUnit() {
		return crUnit;
	}

	public void setCrUnit(String crUnit) {
		this.crUnit = crUnit;
	}
	
	@ExcelField(title="modificator", align=2, sort=11)
	public String getModificator() {
		return modificator;
	}

	public void setModificator(String modificator) {
		this.modificator = modificator;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="mo_date", align=2, sort=12)
	public Date getMoDate() {
		return moDate;
	}

	public void setMoDate(Date moDate) {
		this.moDate = moDate;
	}
	
	@ExcelField(title="mo_unit", align=2, sort=13)
	public String getMoUnit() {
		return moUnit;
	}

	public void setMoUnit(String moUnit) {
		this.moUnit = moUnit;
	}
	
}