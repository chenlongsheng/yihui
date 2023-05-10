package com.jeeplus.modules.warm.entity;

import com.jeeplus.common.persistence.DataEntity;

/**
 * Created by ZZUSER on 2018/12/6.
 */
public class PdfOrder extends DataEntity<PdfOrder> {
    private String devId;//设备id
    
    private String chId;//设备id

    private String devType;//设备类型

    private String typeId;//类型id

    private String alarmType;//报警类型

    private String prec;//问题描述

    private Integer alarmLevel;//报警级别mainType

    private String alarmTime;//报警时间
    
    private String orgId;//报警地址

    private String alarmAddr;//报警地址


    private String sendOrderUser;//派单人

    private String suggestion;//处理建议

    private String principal;//负责人,外部维保

    private Integer state;//状态
    
    private Integer isDispatch;
    
    private String alarmNumber;

    private String alarmCancelTime;//报警解除时间

    private String confirmUser;//确认人

    private String localOrgId;//当前登录账号所属区域id

    private String alarmStartTime;//报警时间开始

    private String alarmEndTime;//报警时间结束

    private String alarmCancelStart;//报警解除时间开始

    private String alarmCancelEnd;//报警解除时间结束

    private String[] arr;

    private int alarmSource;//报警来源（1系统报警2人工报警）

    private String pdfName;//配电房名称（用于查询用）
    
    private String reason;//理由
    
    private String orderId;//工单id
    
    private String mainType; //维保内外
    
    private String phone; //派单人电话
    
    private String codeName;//设备类型名称
    
    private String pdfUser;//内部维保人员
    
    private String userId;//登入的用户id
     
    private String mobanMessage;//模板
    
    private String devName;
   
    private String  type = "1";



	/**
	 * @return the pdfUser
	 */
	public String getPdfUser() {
		return pdfUser;
	}

	/**
	 * @param pdfUser the pdfUser to set
	 */
	public void setPdfUser(String pdfUser) {
		this.pdfUser = pdfUser;
	}



	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
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
	 * @return the chId
	 */
	public String getChId() {
		return chId;
	}

	/**
	 * @param chId the chId to set
	 */
	public void setChId(String chId) {
		this.chId = chId;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the mobanMessage
	 */
	public String getMobanMessage() {
		return mobanMessage;
	}

	/**
	 * @param mobanMessage the mobanMessage to set
	 */
	public void setMobanMessage(String mobanMessage) {
		this.mobanMessage = mobanMessage;
	}

	
	
	/**
	 * @return the orgId
	 */
	public String getOrgId() {
		return orgId;
	}

	/**
	 * @param orgId the orgId to set
	 */
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

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
	 * @return the mainType
	 */
	public String getMainType() {
		return mainType;
	}

	/**
	 * @param mainType the mainType to set
	 */
	public void setMainType(String mainType) {
		this.mainType = mainType;
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

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
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

	public String getPrec() {
        return prec;
    }

    public void setPrec(String prec) {
        this.prec = prec;
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
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
	 * @return the isDispatch
	 */
	public Integer getIsDispatch() {
		return isDispatch;
	}

	/**
	 * @param isDispatch the isDispatch to set
	 */
	public void setIsDispatch(Integer isDispatch) {
		this.isDispatch = isDispatch;
	}

	public String getAlarmAddr() {
        return alarmAddr;
    }

    public void setAlarmAddr(String alarmAddr) {
        this.alarmAddr = alarmAddr;
    }

    public String getSendOrderUser() {
        return sendOrderUser;
    }

    public void setSendOrderUser(String sendOrderUser) {
        this.sendOrderUser = sendOrderUser;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getAlarmCancelTime() {
        return alarmCancelTime;
    }

    public void setAlarmCancelTime(String alarmCancelTime) {
        this.alarmCancelTime = alarmCancelTime;
    }

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getLocalOrgId() {
        return localOrgId;
    }

    public void setLocalOrgId(String localOrgId) {
        this.localOrgId = localOrgId;
    }

    public String getAlarmStartTime() {
        return alarmStartTime;
    }

    public void setAlarmStartTime(String alarmStartTime) {
        this.alarmStartTime = alarmStartTime;
    }

    public String getAlarmEndTime() {
        return alarmEndTime;
    }

    public void setAlarmEndTime(String alarmEndTime) {
        this.alarmEndTime = alarmEndTime;
    }

    public String getAlarmCancelStart() {
        return alarmCancelStart;
    }

    public void setAlarmCancelStart(String alarmCancelStart) {
        this.alarmCancelStart = alarmCancelStart;
    }

    public String getAlarmCancelEnd() {
        return alarmCancelEnd;
    }

    public void setAlarmCancelEnd(String alarmCancelEnd) {
        this.alarmCancelEnd = alarmCancelEnd;
    }

    public String[] getArr() {
        return arr;
    }

    public void setArr(String[] arr) {
        this.arr = arr;
    }

    public String getConfirmUser() {
        return confirmUser;
    }

    public void setConfirmUser(String confirmUser) {
        this.confirmUser = confirmUser;
    }

    public int getAlarmSource() {
        return alarmSource;
    }

    public void setAlarmSource(int alarmSource) {
        this.alarmSource = alarmSource;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }
}
