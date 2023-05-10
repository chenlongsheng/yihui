/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.entity;


import com.jeeplus.common.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 联动管理Entity
 * @author long
 * @version 2018-08-08
 */
public class TIdLinkId extends DataEntity<TIdLinkId> {
	
	private static final long serialVersionUID = 1L;
	private Long srcId;		// 源通道
	private Long destId;		// 目标通道

	private Long linkType;		// 联动类型
	private Long param;		// 联动参数
	private Long notUse;		// 0-停用
	private String level;
	
	private String coName;
	private String decoName;
	private String linkName;
	private TChannel tChannel;
	
	private String  devType;
	private String actionId;
	private String action;		//触发条件
    private String pdfLinkTlinkId;	//触发策略id  2
	private String pdfLinkId;		// 策略id  1
	private String tLinkId;		// 联动表id   3
	private String devIds;
	private String devName;		// 源设备
	private String conditionId;
    private String conditions;		// 触发条件
    private String station;		// 触发前提
	private String linkTypeName;		// 联动类型名称
	private String devLinkIds;    //联动设备ids
	private String linkDev;		// 联动设备name
	
	private String chType;    //通道类型id
	private String typeId;	
	private String status;
	private String chName;
	
	private String number;
	private String devTypeLinkId;
	private String chTypeId;
	private String airId;
	private String extParams;
	
	public TIdLinkId() {
		super();
	}

//	public TIdLinkId(String id){
//		super(id);
//	}
	
	

	@ExcelField(title="源通道", align=2, sort=1)
	public Long getSrcId() {
		return srcId;
	}

	
	/**
	 * @return the conditionId
	 */
	public String getConditionId() {
		return conditionId;
	}

	/**
	 * @param conditionId the conditionId to set
	 */
	public void setConditionId(String conditionId) {
		this.conditionId = conditionId;
	}

	
	
	
	/**
	 * @return the extParams
	 */
	public String getExtParams() {
		return extParams;
	}

	/**
	 * @param extParams the extParams to set
	 */
	public void setExtParams(String extParams) {
		this.extParams = extParams;
	}

	/**
	 * @return the devTypeLinkId
	 */
	public String getDevTypeLinkId() {
		return devTypeLinkId;
	}

	/**
	 * @param devTypeLinkId the devTypeLinkId to set
	 */
	public void setDevTypeLinkId(String devTypeLinkId) {
		this.devTypeLinkId = devTypeLinkId;
	}

	/**
	 * @return the actionId
	 */
	public String getActionId() {
		return actionId;
	}

	/**
	 * @param actionId the actionId to set
	 */
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	/**
	 * @return the chTypeId
	 */
	public String getChTypeId() {
		return chTypeId;
	}

	/**
	 * @param chTypeId the chTypeId to set
	 */
	public void setChTypeId(String chTypeId) {
		this.chTypeId = chTypeId;
	}

	/**
	 * @return the airId
	 */
	public String getAirId() {
		return airId;
	}

	/**
	 * @param airId the airId to set
	 */
	public void setAirId(String airId) {
		this.airId = airId;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the chName
	 */
	public String getChName() {
		return chName;
	}

	/**
	 * @param chName the chName to set
	 */
	public void setChName(String chName) {
		this.chName = chName;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	
	
	/**
	 * @return the devType
	 */
	public String getDevType() {
		return devType;
	}

	/**
	 * @param devType the devType to set
	 */
	public void setDevType(String devType) {
		this.devType = devType;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return the chType
	 */
	public String getChType() {
		return chType;
	}

	/**
	 * @param chType the chType to set
	 */
	public void setChType(String chType) {
		this.chType = chType;
	}

	/**
	 * @return the typeId
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * @param typeId the typeId to set
	 */
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	/**
	 * @return the pdfLinkTlinkId
	 */
	public String getPdfLinkTlinkId() {
		return pdfLinkTlinkId;
	}

	/**
	 * @param pdfLinkTlinkId the pdfLinkTlinkId to set
	 */
	public void setPdfLinkTlinkId(String pdfLinkTlinkId) {
		this.pdfLinkTlinkId = pdfLinkTlinkId;
	}

	/**
	 * @return the devIds
	 */
	public String getDevIds() {
		return devIds;
	}

	/**
	 * @param devIds the devIds to set
	 */
	public void setDevIds(String devIds) {
		this.devIds = devIds;
	}

	/**
	 * @return the devLinkIds
	 */
	public String getDevLinkIds() {
		return devLinkIds;
	}

	/**
	 * @param devLinkIds the devLinkIds to set
	 */
	public void setDevLinkIds(String devLinkIds) {
		this.devLinkIds = devLinkIds;
	}

	/**
	 * @return the pdfLinkId
	 */
	public String getPdfLinkId() {
		return pdfLinkId;
	}

	/**
	 * @param pdfLinkId the pdfLinkId to set
	 */
	public void setPdfLinkId(String pdfLinkId) {
		this.pdfLinkId = pdfLinkId;
	}

	/**
	 * @return the tLinkId
	 */
	public String gettLinkId() {
		return tLinkId;
	}

	/**
	 * @param tLinkId the tLinkId to set
	 */
	public void settLinkId(String tLinkId) {
		this.tLinkId = tLinkId;
	}

	/**
	 * @return the devName
	 */
	public String getDevName() {
		return devName;
	}

	/**
	 * @param devName the devName to set
	 */
	public void setDevName(String devName) {
		this.devName = devName;
	}

	/**
	 * @return the condition
	 */
	public String getConditions() {
		return conditions;
	}

	/**
	 * @param condition the condition to set
	 */
	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	/**
	 * @return the station
	 */
	public String getStation() {
		return station;
	}

	/**
	 * @param station the station to set
	 */
	public void setStation(String station) {
		this.station = station;
	}

	/**
	 * @return the linkTypeName
	 */
	public String getLinkTypeName() {
		return linkTypeName;
	}

	/**
	 * @param linkTypeName the linkTypeName to set
	 */
	public void setLinkTypeName(String linkTypeName) {
		this.linkTypeName = linkTypeName;
	}

	/**
	 * @return the linkDev
	 */
	public String getLinkDev() {
		return linkDev;
	}

	/**
	 * @param linkDev the linkDev to set
	 */
	public void setLinkDev(String linkDev) {
		this.linkDev = linkDev;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public String getCoName() {
		return coName;
	}

	public void setCoName(String coName) {
		this.coName = coName;
	}

	public String getDecoName() {
		return decoName;
	}

	public void setDecoName(String decoName) {
		this.decoName = decoName;
	}

	public TChannel gettChannel() {
		return tChannel;
	}

	public void settChannel(TChannel tChannel) {
		this.tChannel = tChannel;
	}

	public void setSrcId(Long srcId) {
		this.srcId = srcId;
	}
	
//	@ExcelField(title="源通道类型", align=2, sort=2)
//	public Long getSrcType() {
//		return srcType;
//	}
//
//	public void setSrcType(Long srcType) {
//		this.srcType = srcType;
//	}
//	
//	@ExcelField(title="codeId", align=2, sort=3)
//	public Long getSrcCodeId() {
//		return srcCodeId;
//	}
//
//	public void setSrcCodeId(Long srcCodeId) {
//		this.srcCodeId = srcCodeId;
//	}
	
	@ExcelField(title="目标通道", align=2, sort=4)
	public Long getDestId() {
		return destId;
	}

	public void setDestId(Long destId) {
		this.destId = destId;
	}
	
//	@ExcelField(title="目标通道类型", align=2, sort=5)
//	public Long getDestType() {
//		return destType;
//	}
//
//	public void setDestType(Long destType) {
//		this.destType = destType;
//	}
	
//	@ExcelField(title="dest_code_id", align=2, sort=6)
//	public Long getDestCodeId() {
//		return destCodeId;
//	}
//
//	public void setDestCodeId(Long destCodeId) {
//		this.destCodeId = destCodeId;
//	}

	@ExcelField(title="联动类型", align=2, sort=7)
	public Long getLinkType() {
		return linkType;
	}

	public void setLinkType(Long linkType) {
		this.linkType = linkType;
	}
	
	@ExcelField(title="联动参数", align=2, sort=8)
	public Long getParam() {
		return param;
	}

	public void setParam(Long param) {
		this.param = param;
	}
	
	@ExcelField(title="1-停用", align=2, sort=9)
	public Long getNotUse() {
		return notUse;
	}

	public void setNotUse(Long notUse) {
		this.notUse = notUse;
	}
	
}