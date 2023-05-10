package com.jeeplus.modules.homepage.entity;

import com.jeeplus.common.persistence.DataEntity;

/**
 * Created by Administrator on 2018-12-20.
 */
public class Statistics extends DataEntity<Statistics> {
    //设备类型
    private String devType;
    //code_type_id
    private String typeId;
    //通道类型
    private String chType;
    //总数
    private String total;
    //启用数
    private String useTotal;
    //地区id
    private String orgId;
    //地区名称
    private String orgName;
    
    private String userId;
    
    


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

	public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getUseTotal() {
        return useTotal;
    }

    public void setUseTotal(String useTotal) {
        this.useTotal = useTotal;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getChType() {
        return chType;
    }

    public void setChType(String chType) {
        this.chType = chType;
    }
}
