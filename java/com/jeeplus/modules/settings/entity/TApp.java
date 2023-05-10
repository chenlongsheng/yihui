/**
 * 
 */
package com.jeeplus.modules.settings.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jeeplus.common.persistence.DataEntity;

/**
 * @author admin
 *
 */
public class TApp extends DataEntity<TApp>{
	
	
	private String systemName;//系统类型
	private String model;//设备型号
	private String modelType;//设备类型
	
	
	private String innerVersion;//内部版本
	private String outVersion;//外部版本
	private Integer noUpdate;//是否更新
	private String minInnerVersion;//最近内部版本
	private String url;//链接
	private Integer noPublish;//是否强制推送
	private Date endDate;//日期
	/**
	 * 
	 */
	public TApp() {
		super();
	}
	/**
	 * @return the systemName
	 */
	public String getSystemName() {
		return systemName;
	}
	/**
	 * @param systemName the systemName to set
	 */
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	/**
	 * @return the innerVersion
	 */
	public String getInnerVersion() {
		return innerVersion;
	}
	/**
	 * @param innerVersion the innerVersion to set
	 */
	public void setInnerVersion(String innerVersion) {
		this.innerVersion = innerVersion;
	}
	
	
	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}
	/**
	 * @return the modelType
	 */
	public String getModelType() {
		return modelType;
	}
	/**
	 * @param modelType the modelType to set
	 */
	public void setModelType(String modelType) {
		this.modelType = modelType;
	}
	/**
	 * @return the outVersion
	 */
	public String getOutVersion() {
		return outVersion;
	}
	/**
	 * @param outVersion the outVersion to set
	 */
	public void setOutVersion(String outVersion) {
		this.outVersion = outVersion;
	}
	/**
	 * @return the noUpdate
	 */
	public Integer getNoUpdate() {
		return noUpdate;
	}
	/**
	 * @param noUpdate the noUpdate to set
	 */
	public void setNoUpdate(Integer noUpdate) {
		this.noUpdate = noUpdate;
	}
	/**
	 * @return the minInnerVersion
	 */
	public String getMinInnerVersion() {
		return minInnerVersion;
	}
	/**
	 * @param minInnerVersion the minInnerVersion to set
	 */
	public void setMinInnerVersion(String minInnerVersion) {
		this.minInnerVersion = minInnerVersion;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the noPublish
	 */
	public Integer getNoPublish() {
		return noPublish;
	}
	/**
	 * @param noPublish the noPublish to set
	 */
	public void setNoPublish(Integer noPublish) {
		this.noPublish = noPublish;
	}
	/**
	 * @return the endDate
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	


}
