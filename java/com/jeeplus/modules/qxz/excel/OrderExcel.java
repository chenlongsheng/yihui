/**
 * 
 */
package com.jeeplus.modules.qxz.excel;

/**
 * @author admin
 *
 */
public class OrderExcel {

	private String id;// 报警id
	private String typeId;// 设备类型
	private String devName;// 设备名称
	private String prec;// 描述
	private String alarmType;// 报警类型
	private String alarmLevel;// 报警级别
	private String alarmTime;// 报警时间
	private String orgName;// 配电房
	private String alarmNumber;// 报警次数
	private String state; // 报警状态
	private String isDispatch;// 工单状态
	private String orderId;// 工单id
	private String phone; // 派单人电话
	private String userName;// 派单人
	private String suggestion;// 处理建议
	 private String codeName;//设备类型名称
     
	    


		/**
		 * @return the codeName
		 */
		public String getCodeName() {
			return codeName;
		}

		/**
		 * @param codeName the codeName to set
		 */
		public void setCodeName(String codeName) {
			this.codeName = codeName;
		}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	
	
	
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}



	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the suggestion
	 */
	public String getSuggestion() {
		return suggestion;
	}

	/**
	 * @param suggestion the suggestion to set
	 */
	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	/**
	 * @return the prec
	 */
	public String getPrec() {
		return prec;
	}

	/**
	 * @param prec the prec to set
	 */
	public void setPrec(String prec) {
		this.prec = prec;
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
	 * @return the alarmType
	 */
	public String getAlarmType() {
		return alarmType;
	}

	/**
	 * @param alarmType the alarmType to set
	 */
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	/**
	 * @return the alarmLevel
	 */
	public String getAlarmLevel() {
		return alarmLevel;
	}

	/**
	 * @param alarmLevel the alarmLevel to set
	 */
	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	/**
	 * @return the alarmTime
	 */
	public String getAlarmTime() {
		return alarmTime;
	}

	/**
	 * @param alarmTime the alarmTime to set
	 */
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}

	/**
	 * @return the orgName
	 */
	public String getOrgName() {
		return orgName;
	}

	/**
	 * @param orgName the orgName to set
	 */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	/**
	 * @return the alarmNumber
	 */
	public String getAlarmNumber() {
		return alarmNumber;
	}

	/**
	 * @param alarmNumber the alarmNumber to set
	 */
	public void setAlarmNumber(String alarmNumber) {
		this.alarmNumber = alarmNumber;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the isDispatch
	 */
	public String getIsDispatch() {
		return isDispatch;
	}

	/**
	 * @param isDispatch the isDispatch to set
	 */
	public void setIsDispatch(String isDispatch) {
		this.isDispatch = isDispatch;
	}

	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}
