/**
 * Copyright &copy; 2015-2020 
 */
package com.jeeplus.modules.enterprise.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jeeplus.common.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 通道组管理Entity
 * @author ywk
 * @version 2017-04-06
 */
public class TChGroup extends DataEntity<TChGroup> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// name
	private Long orgId;		// org_id
	private String chIds;		// 逗号分隔
	private String creator;		// creator
	private Date crDate;		// cr_date
	private String crUnit;		// cr_unit
	private String modificator;		// modificator
	private Date moDate;		// mo_date
	private String moUnit;		// mo_unit
	private Long notUse;		// 1-停用
	
	public TChGroup() {
		super();
	}

	public TChGroup(Long id){
		super();
	}

	@ExcelField(title="name", align=2, sort=1)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@ExcelField(title="org_id", align=2, sort=2)
	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	
	@ExcelField(title="逗号分隔", align=2, sort=3)
	public String getChIds() {
		return chIds;
	}

	public void setChIds(String chIds) {
		this.chIds = chIds;
	}
	
	@ExcelField(title="creator", align=2, sort=4)
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="cr_date", align=2, sort=5)
	public Date getCrDate() {
		return crDate;
	}

	public void setCrDate(Date crDate) {
		this.crDate = crDate;
	}
	
	@ExcelField(title="cr_unit", align=2, sort=6)
	public String getCrUnit() {
		return crUnit;
	}

	public void setCrUnit(String crUnit) {
		this.crUnit = crUnit;
	}
	
	@ExcelField(title="modificator", align=2, sort=7)
	public String getModificator() {
		return modificator;
	}

	public void setModificator(String modificator) {
		this.modificator = modificator;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="mo_date", align=2, sort=8)
	public Date getMoDate() {
		return moDate;
	}

	public void setMoDate(Date moDate) {
		this.moDate = moDate;
	}
	
	@ExcelField(title="mo_unit", align=2, sort=9)
	public String getMoUnit() {
		return moUnit;
	}

	public void setMoUnit(String moUnit) {
		this.moUnit = moUnit;
	}
	
	@ExcelField(title="1-停用", align=2, sort=10)
	public Long getNotUse() {
		return notUse;
	}

	public void setNotUse(Long notUse) {
		this.notUse = notUse;
	}
	
}