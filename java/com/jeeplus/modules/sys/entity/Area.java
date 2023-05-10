/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.entity;

import org.hibernate.validator.constraints.Length;

import com.jeeplus.common.persistence.TreeEntity;

/**
 * 区域Entity
 * @author jeeplus
 * @version 2013-05-15
 */
public class Area extends TreeEntity<Area> {

	private static final long serialVersionUID = 1L;
//	private Area parent;	// 父级编号
//	private String parentIds; // 所有父级编号
	private String code; 	// 区域编码
//	private String name; 	// 区域名称
//	private Integer sort;		// 排序
	private String type; 	// 区域类型（1：国家；2：省份、直辖市；3：地市；4：区县）
	private String image;
	private String imageName;
	private Long orgId;
	private String coordsX;
	private String coordsY;
	
	
	private String address;
	private String level;
	private String changerNum;
	private String rate;
	private String capacity;
	private String declaredQuantity;
	private String controler;
	private String highVoltage;
	private String highCabinet;
	private String lowCabinet;
	private String electId;
	private String planePic;
	private String picUrl;
	private String bureauId;

	
	private String userId;
	private String userName;
	private String parentId;
	
	private Integer beginCount;
	private Integer endCount;

	    private Integer loopType;
	    
	    private String oldCode;
	    
	
	public Area(){
		super();
		this.orderNo = 30;
	}

	public Area(String id){
		super(id);
	}
	
	
	
/**
	 * @return the highVoltage
	 */
	public String getHighVoltage() {
		return highVoltage;
	}

	/**
	 * @param highVoltage the highVoltage to set
	 */
	public void setHighVoltage(String highVoltage) {
		this.highVoltage = highVoltage;
	}

	/**
	 * @return the highCabinet
	 */
	public String getHighCabinet() {
		return highCabinet;
	}
	
	
	

	/**
     * @return the oldCode
     */
    public String getOldCode() {
        return oldCode;
    }

    /**
     * @param oldCode the oldCode to set
     */
    public void setOldCode(String oldCode) {
        this.oldCode = oldCode;
    }

    /**
     * @return the loopType
     */
    public Integer getLoopType() {
        return loopType;
    }

    /**
     * @param loopType the loopType to set
     */
    public void setLoopType(Integer loopType) {
        this.loopType = loopType;
    }

    /**
	 * @param highCabinet the highCabinet to set
	 */
	public void setHighCabinet(String highCabinet) {
		this.highCabinet = highCabinet;
	}

	/**
	 * @return the lowCabinet
	 */
	public String getLowCabinet() {
		return lowCabinet;
	}

	/**
	 * @param lowCabinet the lowCabinet to set
	 */
	public void setLowCabinet(String lowCabinet) {
		this.lowCabinet = lowCabinet;
	}

/**
	 * @return the electId
	 */
	public String getElectId() {
		return electId;
	}

	/**
	 * @param electId the electId to set
	 */
	public void setElectId(String electId) {
		this.electId = electId;
	}

	
	

	/**
	 * @return the bureau
	 */
	public String getBureauId() {
		return bureauId;
	}

	/**
	 * @param bureau the bureau to set
	 */
	public void setBureauId(String bureauId) {
		this.bureauId = bureauId;
	}

	/**
	 * @return the beginCount
	 */
	public Integer getBeginCount() {
		return beginCount;
	}

	/**
	 * @param beginCount the beginCount to set
	 */
	public void setBeginCount(Integer beginCount) {
		this.beginCount = beginCount;
	}

	/**
	 * @return the endCount
	 */
	public Integer getEndCount() {
		return endCount;
	}

	/**
	 * @param endCount the endCount to set
	 */
	public void setEndCount(Integer endCount) {
		this.endCount = endCount;
	}

	/**
	 * @return the picUrl
	 */
	public String getPicUrl() {
		return picUrl;
	}

	/**
	 * @param picUrl the picUrl to set
	 */
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	
	

	/**
	 * @return the imageName
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * @param imageName the imageName to set
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	/**
	 * @return the planePic
	 */
	public String getPlanePic() {
		return planePic;
	}

	/**
	 * @param planePic the planePic to set
	 */
	public void setPlanePic(String planePic) {
		this.planePic = planePic;
	}

/**
	 * @return the parentId
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	//	@JsonBackReference
//	@NotNull
	public Area getParent() {
		return parent;
	}

	public void setParent(Area parent) {
		this.parent = parent;
	}
	
//	@Length(min=1, max=2000)
//	public String getParentIds() {
//		return parentIds;
//	}
//
//	public void setParentIds(String parentIds) {
//		this.parentIds = parentIds;
//	}
//	
//	@Length(min=1, max=100)
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public Integer getSort() {
//		return sort;
//	}
//
//	public void setSort(Integer sort) {
//		this.sort = sort;
//	}
	

	
	public Long getOrgId() {
		return orgId;
	}
	
	public void setOrgId(long oid) {
		orgId = oid;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Length(min=1, max=1)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Length(min=0, max=100)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
//
//	public String getParentId() {
//		return parent != null && parent.getId() != null ? parent.getId() : "0";
//	}
	
	@Override
	public String toString() {
		return name;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
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
	 * @return the changerNum
	 */
	public String getChangerNum() {
		return changerNum;
	}

	/**
	 * @param changerNum the changerNum to set
	 */
	public void setChangerNum(String changerNum) {
		this.changerNum = changerNum;
	}

	/**
	 * @return the rate
	 */
	public String getRate() {
		return rate;
	}

	/**
	 * @param rate the rate to set
	 */
	public void setRate(String rate) {
		this.rate = rate;
	}

	/**
	 * @return the capacity
	 */
	public String getCapacity() {
		return capacity;
	}

	/**
	 * @param capacity the capacity to set
	 */
	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	/**
	 * @return the declaredQuantity
	 */
	public String getDeclaredQuantity() {
		return declaredQuantity;
	}

	/**
	 * @param declaredQuantity the declaredQuantity to set
	 */
	public void setDeclaredQuantity(String declaredQuantity) {
		this.declaredQuantity = declaredQuantity;
	}

	/**
	 * @return the controler
	 */
	public String getControler() {
		return controler;
	}

	/**
	 * @param controler the controler to set
	 */
	public void setControler(String controler) {
		this.controler = controler;
	}

	/**
	 * @param orgId the orgId to set
	 */
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
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
	 * @return the coordsX
	 */
	public String getCoordsX() {
		return coordsX;
	}

	/**
	 * @param coordsX the coordsX to set
	 */
	public void setCoordsX(String coordsX) {
		this.coordsX = coordsX;
	}

	/**
	 * @return the coordsY
	 */
	public String getCoordsY() {
		return coordsY;
	}

	/**
	 * @param coordsY the coordsY to set
	 */
	public void setCoordsY(String coordsY) {
		this.coordsY = coordsY;
	}
	
	
	
}