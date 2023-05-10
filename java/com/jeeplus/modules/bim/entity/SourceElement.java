package com.jeeplus.modules.bim.entity;

import java.util.Date;

/**
 * Created by Administrator on 2018-12-27.
 */
public class SourceElement {
    //构件信息Id
    private String id;
    //构件信息UUID
    private String uuid;
    //项目编码
    private String buildingUuid;
    //楼层uuid
    private String storeyUuid;
    //楼层名称
    private String storeyName;
    //构件名称
    private String elemenName;
    //构建类型
    private String elemType;
    //构件分类名称
    private String elemTypeName;
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

    public String getBuildingUuid() {
        return buildingUuid;
    }

    public void setBuildingUuid(String buildingUuid) {
        this.buildingUuid = buildingUuid;
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

    public String getElemenName() {
        return elemenName;
    }

    public void setElemenName(String elemenName) {
        this.elemenName = elemenName;
    }

    public String getElemType() {
        return elemType;
    }

    public void setElemType(String elemType) {
        this.elemType = elemType;
    }

    public String getElemTypeName() {
        return elemTypeName;
    }

    public void setElemTypeName(String elemTypeName) {
        this.elemTypeName = elemTypeName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
