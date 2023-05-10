package com.jeeplus.modules.bim.entity;

import java.util.Date;

/**
 * Created by Administrator on 2018-12-27.
 */
public class SourceSpace {
    private String id;
    //空间uuid
    private String uuid;
    //空间名称
    private String name;
    //楼层uuid
    private String storeyUuid;
    //楼层名称
    private String storeyName;
    //项目编码
    private String buildingUuid;
    //房间属性key
    private String userdataKey;
    //房间属性val
    private String userdataVal;
    //房间详细属性
    private String userData;
    //创建时间
    private Date createdDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStoreyUuid() {
        return storeyUuid;
    }

    public void setStoreyUuid(String storeyUuid) {
        this.storeyUuid = storeyUuid;
    }

    public String getStoreyName() {
        return storeyName;
    }

    public void setStoreyName(String storeyName) {
        this.storeyName = storeyName;
    }

    public String getBuildingUuid() {
        return buildingUuid;
    }

    public void setBuildingUuid(String buildingUuid) {
        this.buildingUuid = buildingUuid;
    }

    public String getUserdataKey() {
        return userdataKey;
    }

    public void setUserdataKey(String userdataKey) {
        this.userdataKey = userdataKey;
    }

    public String getUserdataVal() {
        return userdataVal;
    }

    public void setUserdataVal(String userdataVal) {
        this.userdataVal = userdataVal;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
