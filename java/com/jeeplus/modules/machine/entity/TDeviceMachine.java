/**
 * 
 */
package com.jeeplus.modules.machine.entity;

import com.jeeplus.common.persistence.DataEntity;


/**
 * @author admin
 *
 */
public class TDeviceMachine extends DataEntity<TDeviceMachine>{
	
    private static final long serialVersionUID = 1L;
    private String devId;     // 设备id
 
    private String warn;      // 通道号
       // 通道类型2
    private String realValue;        // 通道名称 
    private Integer chNo;        // 参数0
   
    private String name;       // 扩展参数
    private String monad;
    private String chType;  //参数2
    private String stateName;
    private String chId;
    
    
    public TDeviceMachine() {
        
    }
    /**
     * @param devId
     * @param warn
     * @param realValue
     * @param chNo
     * @param name
     * @param monad
     * @param chType
     * @param stateName
     * @param chId
     */
    public TDeviceMachine(String devId, String warn, String realValue, Integer chNo, String name, String monad,
            String chType, String stateName, String chId) {
        super();
        this.devId = devId;
        this.warn = warn;
        this.realValue = realValue;
        this.chNo = chNo;
        this.name = name;
        this.monad = monad;
        this.chType = chType;
        this.stateName = stateName;
        this.chId = chId;
    }
    
    
    
    /**
     * @return the devId
     */
    public String getDevId() {
        return devId;
    }
    /**
     * @param devId the devId to set
     */
    public void setDevId(String devId) {
        this.devId = devId;
    }
    /**
     * @return the warn
     */
    public String getWarn() {
        return warn;
    }
    /**
     * @param warn the warn to set
     */
    public void setWarn(String warn) {
        this.warn = warn;
    }
    /**
     * @return the realValue
     */
    public String getRealValue() {
        return realValue;
    }
    /**
     * @param realValue the realValue to set
     */
    public void setRealValue(String realValue) {
        this.realValue = realValue;
    }
    /**
     * @return the chNo
     */
    public Integer getChNo() {
        return chNo;
    }
    /**
     * @param chNo the chNo to set
     */
    public void setChNo(Integer chNo) {
        this.chNo = chNo;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the monad
     */
    public String getMonad() {
        return monad;
    }
    /**
     * @param monad the monad to set
     */
    public void setMonad(String monad) {
        this.monad = monad;
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
     * @return the stateName
     */
    public String getStateName() {
        return stateName;
    }
    /**
     * @param stateName the stateName to set
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
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
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
   
	

}
